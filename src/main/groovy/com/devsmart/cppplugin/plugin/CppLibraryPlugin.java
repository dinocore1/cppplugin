package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.*;
import com.devsmart.cppplugin.tasks.CppCompileTask;
import com.devsmart.cppplugin.tasks.CreateStaticLibrary;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.ComponentWithVariants;
import org.gradle.api.component.PublishableComponent;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.transform.UnzipTransform;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal;
import org.gradle.api.publish.maven.internal.publisher.MutableMavenProjectIdentity;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent;
import org.gradle.language.nativeplatform.internal.toolchains.ToolChainSelector;
import org.gradle.nativeplatform.Linkage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Set;

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
        project.getPluginManager().apply(CppBasePlugin.class);

        final ObjectFactory objects = project.getObjects();

        CppLibrary library = objects.newInstance(CppLibrary.class, "main");
        project.getExtensions().add("library", library);
        project.getComponents().add(library);

        //Configure the component
        library.getBaseName().convention(project.getName());

        HeaderModule headerModule = library.addHeaderModule();
        TaskProvider<Zip> zipHeadersTask = project.getTasks().register(headerModule.getNames().getTaskName("zipHeaders"), Zip.class, task -> {
            task.from(headerModule.getHeaderFiles());
            task.into(project.getLayout().getBuildDirectory().file(project.provider(() -> {
                return "headers/" + headerModule.getNames().getRelativePath() + "/" + library.getBaseName().get() + ".zip";
            })));
        });
        headerModule.getCompileConfiguration().getOutgoing().artifact(zipHeadersTask);


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




}
