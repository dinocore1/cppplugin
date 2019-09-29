package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.*;
import com.devsmart.cppplugin.tasks.CppCompileTask;
import com.devsmart.cppplugin.tasks.CreateStaticLibrary;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.attributes.Usage;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.transform.UnzipTransform;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.language.nativeplatform.internal.toolchains.ToolChainSelector;
import org.gradle.nativeplatform.Linkage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.DIRECTORY_TYPE;
import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.ZIP_TYPE;

public class CppLibraryPlugin implements Plugin<Project> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CppLibraryPlugin.class);

    private final BuildOperationExecutor buildExecutor;
    private final WorkerLeaseService workerLease;
    private final ToolChainSelector toolchainSelector;


    @Inject
    public CppLibraryPlugin(BuildOperationExecutor buildOperationExecutor, WorkerLeaseService workerLeaseService, ToolChainSelector toolChainSelector) {
        this.buildExecutor = buildOperationExecutor;
        this.workerLease = workerLeaseService;
        this.toolchainSelector = toolChainSelector;
    }

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(LifecycleBasePlugin.class);

        // Add incoming artifact transforms
        final DependencyHandler dependencyHandler = project.getDependencies();
        final ObjectFactory objects = project.getObjects();

        addHeaderZipTransform(dependencyHandler, objects);

        CppLibrary library = objects.newInstance(CppLibrary.class, "main");
        project.getExtensions().add("library", library);
        project.getComponents().add(library);

        //Configure the component
        library.getBaseName().convention(project.getName());


        //final BuildOperationExecutor buildOperationExecutor = serviceRegistry.get(BuildOperationExecutor.class);


        /*
        NativeToolChainRegistry toolchainRegistry = project.getExtensions().getByType(NativeToolChainRegistry.class);
        toolchainRegistry.all(toolChain -> {
            LOGGER.info("toolchain " + toolChain);
        });

        DefaultNativePlatform host = DefaultNativePlatform.host();
        OperatingSystemFamily operatingSystemFamily = objectFactory.named(OperatingSystemFamily.class, host.getOperatingSystem().toFamilyName());
        org.gradle.nativeplatform.MachineArchitecture machineArchitecture = objectFactory.named(MachineArchitecture.class, host.getArchitecture().getName());
        TargetMachine targetMachine = new DefaultTargetMachine(operatingSystemFamily, machineArchitecture);
        toolchainSelector.select(CppPlatform.class, new DefaultCppPlatform(targetMachine));

        project.afterEvaluate(p -> {
            DefaultNativePlatform platform = DefaultNativePlatform.host();
            NativeToolChain theToolchain = toolchainRegistry.getForPlatform(platform);
        });
        */


        project.getComponents().withType(ToolChain.class, toolChain -> {

            VariantIdentity id = new VariantIdentity(toolChain.getTargetPlatform(), true, Linkage.STATIC);
            addStaticLibrary(toolChain, library, id, project);

            id = new VariantIdentity(toolChain.getTargetPlatform(), false, Linkage.STATIC);
            addStaticLibrary(toolChain, library, id, project);

            id = new VariantIdentity(toolChain.getTargetPlatform(), true, Linkage.SHARED);
            addSharedLibrary(toolChain, library, id, project);

            id = new VariantIdentity(toolChain.getTargetPlatform(), false, Linkage.SHARED);
            addSharedLibrary(toolChain, library, id, project);

        });
    }

    private void addStaticLibrary(ToolChain toolChain, CppLibrary library, VariantIdentity id, Project project) {
        StaticLibrary staticLib = library.addStaticLibrary(id);
        Names names = staticLib.getNames();
        TaskProvider<CppCompileTask> compileTask = project.getTasks().register(names.getTaskName("compile"), CppCompileTask.class, task -> {
            task.getVariant().set(id);
            task.getToolchain().set(toolChain);
            task.getCppStandard().set(library.getCppStandard());
            task.getSource().setFrom(ToolChainHelper.getCppSource(staticLib.getSourceFiles()));
            task.getIncludes().setFrom(staticLib.getIncludeDirs());
            task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("obj/" + names.getRelativePath()));
        });
        staticLib.getCompileTask().set(compileTask);

        TaskProvider<CreateStaticLibrary> archiveTask = project.getTasks().register(staticLib.getNames().getTaskName("archive"), CreateStaticLibrary.class, task -> {
            task.getToolChain().set(toolChain);
            task.getObjectFiles().setFrom(staticLib.getCompileTask());
            task.getOutputFile().set(project.getLayout().getBuildDirectory().file( project.provider(() -> {
                return "binary/" + names.getRelativePath() + "/" + library.getBaseName().get() + "." + toolChain.getStaticLibraryFileExtention();
            })));

        });

        project.getTasks().named("assemble", task -> {
            task.dependsOn(archiveTask);
        });
    }

    private void addSharedLibrary(ToolChain toolChain, CppLibrary library, VariantIdentity id, Project project) {
        SharedLibrary sharedLib = library.addSharedLibrary(id);
        Names names = sharedLib.getNames();
        TaskProvider<CppCompileTask> compileTask = project.getTasks().register(names.getTaskName("compile"), CppCompileTask.class, task -> {
            task.getVariant().set(id);
            task.getToolchain().set(toolChain);
            task.getCppStandard().set(library.getCppStandard());
            task.getSource().setFrom(ToolChainHelper.getCppSource(sharedLib.getSourceFiles()));
            task.getIncludes().setFrom(sharedLib.getIncludeDirs());
            task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("obj/" + names.getRelativePath()));
        });
        sharedLib.getCompileTask().set(compileTask);

        TaskProvider<CreateStaticLibrary> archiveTask = project.getTasks().register(sharedLib.getNames().getTaskName("archive"), CreateStaticLibrary.class, task -> {
            task.getToolChain().set(toolChain);
            task.getObjectFiles().setFrom(sharedLib.getCompileTask());
            task.getOutputFile().set(project.getLayout().getBuildDirectory().file( project.provider(() -> {
                return "binary/" + names.getRelativePath() + "/" + library.getBaseName().get() + "." + toolChain.getStaticLibraryFileExtention();
            })));

        });

        project.getTasks().named("assemble", task -> {
            task.dependsOn(archiveTask);
        });
    }

    private void addHeaderZipTransform(DependencyHandler dependencyHandler, ObjectFactory objects) {
        dependencyHandler.registerTransform(UnzipTransform.class, variantTransform -> {
            variantTransform.getFrom().attribute(ArtifactAttributes.ARTIFACT_FORMAT, ZIP_TYPE);
            variantTransform.getFrom().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.C_PLUS_PLUS_API));
            variantTransform.getTo().attribute(ArtifactAttributes.ARTIFACT_FORMAT, DIRECTORY_TYPE);
            variantTransform.getTo().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.C_PLUS_PLUS_API));
        });
    }

    private void addTasksForVariant(VariantIdentity id, Project project, CppLibrary lib, ToolChain toolChain) {
        /*
        ObjectFactory objectFactory = project.getObjects();
        DefaultCppNativeBinary cppBinary = objectFactory.newInstance(DefaultCppNativeBinary.class, lib, id);
        project.getComponents().add(cppBinary);

        Names names = Names.of(id);
        String compileTaskName = names.getTaskName("compile");
        cppBinary.getCompileTask().set(project.getTasks().register(compileTaskName, CppCompileTask.class, task -> {
            task.from(lib, id, toolChain);
        }));

        String linkTaskName = names.getTaskName("link");
        switch (id.getLinkage()) {
            case SHARED:
                cppBinary.getLinkTask().set(project.getTasks().register(linkTaskName, LinkTask.class, task -> {
                    task.getExePath().set(toolChain.getCppCompiler().getExePath());
                    task.getObjectFiles().setFrom(cppBinary.getCompileTask());
                    task.getFlags().add("-shared");

                    RelativePath outputFile = new RelativePath(true, "binary",
                            id.getPlatform().getOperatingSystem().getName().toLowerCase(),
                            id.getPlatform().getMachineArchitecture().getName().toLowerCase(),
                            id.getLinkage().getName().toLowerCase(),
                            String.format("%s.so", lib.getBaseName().get())
                    );
                    task.getOutputFile().set(project.getLayout().getBuildDirectory().file(outputFile.getPathString()));
                }));
                break;

            case STATIC:
                cppBinary.getLinkTask().set(project.getTasks().register(linkTaskName, ArchiveTask.class, task -> {
                    task.getExePath().set(toolChain.getArchiveTool().getExePath());
                    task.getObjectFiles().setFrom(cppBinary.getCompileTask());

                    RelativePath outputFile = new RelativePath(true, "binary",
                            id.getPlatform().getOperatingSystem().getName().toLowerCase(),
                            id.getPlatform().getMachineArchitecture().getName().toLowerCase(),
                            id.getLinkage().getName().toLowerCase(),
                            String.format("%s.a", lib.getBaseName().get())
                    );

                    task.getOutputFile().set(project.getLayout().getBuildDirectory().file(outputFile.getPathString()));
                }));
                break;
        }

        project.getTasks().named("assemble", task -> {
            task.dependsOn(cppBinary.getLinkTask());
        });
         */
    }

}
