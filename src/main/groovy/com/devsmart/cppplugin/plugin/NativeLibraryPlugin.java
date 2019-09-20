package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.*;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.internal.impldep.com.google.gson.Gson;
import org.gradle.internal.impldep.com.google.gson.GsonBuilder;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.nativeplatform.Linkage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileReader;
import java.io.IOException;
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
        project.getPluginManager().apply(LifecycleBasePlugin.class);
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
            DefaultCppNativeBinary cppBinary = objectFactory.newInstance(DefaultCppNativeBinary.class, lib, id);
            project.getComponents().add(cppBinary);

            String compileTaskName = Names.of(id).getTaskName("compile");
            cppBinary.getCompileTask().set(project.getTasks().register(compileTaskName, CompileTask.class, task -> {
                task.getSource().setFrom(lib.getCppSource());
                task.getIncludes().setFrom(lib.getIncludeDirs());
                task.getOutputDir().set(projectLayout.getBuildDirectory().dir("obj"));
                task.getToolChain().set(toolChain);
                task.getCppStandard().set(lib.getCppStandard());
            }));

            project.getTasks().named("assemble", task -> {
                task.dependsOn(cppBinary.getCompileTask());
            });


            //lib.getVariants().add(new VariantIdentity(toolChain.getTargetPlatform(), true, Linkage.STATIC));
            //lib.getVariants().add(new VariantIdentity(toolChain.getTargetPlatform(), true, Linkage.SHARED));
        });


    }



    private static class CompilerDef {
        public String type;
        public String path;
        public String targetCombo;
    }

    private void loadCompilerDefs(NativeLibraryModel lib, Project project) {


        //if(project.hasProperty(COMPILER_DEF_FILE_PROPERTY)) {
            //Object compilerdeffile = project.property(COMPILER_DEF_FILE_PROPERTY);
            Object compilerdeffile = "/home/paul/compilerdef.json";
            if(compilerdeffile != null && compilerdeffile instanceof String) {
                Gson gson = new GsonBuilder().create();
                try {
                    FileReader reader = new FileReader((String) compilerdeffile);
                    CompilerDef[] compilerDefs = gson.fromJson(reader, CompilerDef[].class);
                    for(CompilerDef def : compilerDefs) {
                        LOGGER.info("compiler def {}", def);

                        //GCCCppCompiler compiler = new GCCCppCompiler(def.path);



                    }

                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        //}



    }
}
