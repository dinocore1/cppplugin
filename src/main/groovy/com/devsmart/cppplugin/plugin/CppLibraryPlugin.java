package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.attributes.Usage;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.transform.UnzipTransform;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Copy;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.language.cpp.CppPlatform;
import org.gradle.language.cpp.internal.DefaultCppPlatform;
import org.gradle.language.cpp.plugins.CppLangPlugin;
import org.gradle.language.nativeplatform.internal.toolchains.ToolChainSelector;
import org.gradle.nativeplatform.Linkage;
import org.gradle.nativeplatform.MachineArchitecture;
import org.gradle.nativeplatform.OperatingSystemFamily;
import org.gradle.nativeplatform.TargetMachine;
import org.gradle.nativeplatform.internal.DefaultTargetMachine;
import org.gradle.nativeplatform.internal.DefaultTargetMachineFactory;
import org.gradle.nativeplatform.platform.Architecture;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.platform.OperatingSystem;
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal;
import org.gradle.nativeplatform.plugins.NativeComponentPlugin;
import org.gradle.nativeplatform.toolchain.NativeToolChain;
import org.gradle.nativeplatform.toolchain.NativeToolChainRegistry;
import org.gradle.nativeplatform.toolchain.internal.plugins.StandardToolChainsPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Iterator;

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

        ObjectFactory objectFactory = project.getObjects();
        CppLibrary library = objectFactory.newInstance(CppLibrary.class, "main");
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
            StaticLibrary staticLib = library.addStaticLibrary(id);
            CppCompileTask compileTask = project.getTasks().create(staticLib.getNames().getTaskName("compile"), CppCompileTask.class, task -> {
                task.getToolchain().set(toolChain);
                task.getSource().setFrom(staticLib.getCppSource());
                task.getIncludes().setFrom(staticLib.getIncludeDirs());
            });
            staticLib.getCompileTask().set(compileTask);

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
