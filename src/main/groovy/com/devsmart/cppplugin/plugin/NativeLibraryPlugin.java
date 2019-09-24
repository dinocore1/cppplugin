package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RelativePath;
import org.gradle.api.model.ObjectFactory;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.nativeplatform.Linkage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Iterator;

public class NativeLibraryPlugin implements Plugin<Project> {

    public static final String COMPILER_DEF_FILE_PROPERTY = "com.devsmart.cppplugin.compilerdeffile";
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLibraryPlugin.class);

    private final BuildOperationExecutor buildExecutor;
    private final WorkerLeaseService workerLease;

    @Inject
    public NativeLibraryPlugin(BuildOperationExecutor buildOperationExecutor, WorkerLeaseService workerLeaseService) {
        this.buildExecutor = buildOperationExecutor;
        this.workerLease = workerLeaseService;
    }

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(NativeBasePlugin.class);
        ObjectFactory objectFactory = project.getObjects();
        ProjectLayout projectLayout = project.getLayout();
        NativeLibraryModel lib = objectFactory.newInstance(NativeLibraryModel.class, "main");
        project.getExtensions().add("library", lib);
        project.getComponents().add(lib);
        lib.getBaseName().convention(project.getName());


        //final BuildOperationExecutor buildOperationExecutor = serviceRegistry.get(BuildOperationExecutor.class);


        project.getComponents().withType(CppNativeBinary.class, binary -> {

            Iterator<Configuration> it = project.getConfigurations().iterator();
            while(it.hasNext()) {
                Configuration config = it.next();
            }

        });

        project.getComponents().withType(ToolChain.class, toolChain -> {

            VariantIdentity id = new VariantIdentity(toolChain.getTargetPlatform(), true, Linkage.STATIC);
            addTasksForVariant(id, project, lib, toolChain);

            id = new VariantIdentity(toolChain.getTargetPlatform(), true, Linkage.SHARED);
            addTasksForVariant(id, project, lib, toolChain);

        });


    }

    private void addTasksForVariant(VariantIdentity id, Project project, NativeLibraryModel lib, ToolChain toolChain) {
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
