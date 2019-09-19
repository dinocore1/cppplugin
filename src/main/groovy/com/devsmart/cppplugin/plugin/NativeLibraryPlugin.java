package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.*;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.internal.impldep.com.google.gson.Gson;
import org.gradle.internal.impldep.com.google.gson.GsonBuilder;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.nativeplatform.Linkage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

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

        NativeLibrary lib = project.getObjects().newInstance(com.devsmart.cppplugin.NativeLibrary.class, "main");
        project.getExtensions().add("library", lib);
        project.getComponents().add(lib);
        lib.getBaseName().convention(project.getName());

        loadCompilerDefs(lib, project);


        //final BuildOperationExecutor buildOperationExecutor = serviceRegistry.get(BuildOperationExecutor.class);


        project.getComponents().withType(NativeBinary.class, binary -> {

        });

        project.afterEvaluate(p -> {

            dimentions(lib.getCppCompilers().get(), id -> {
                NativeBinary binary;
            });

        });

    }

    private void dimentions(Collection<CppCompiler> compilers, Action<VariantIdentity> action) {
        for(CppCompiler compiler : compilers) {
            action.execute(new VariantIdentity(compiler.getTargetPlatform(), true, Linkage.STATIC));
            action.execute(new VariantIdentity(compiler.getTargetPlatform(), true, Linkage.SHARED));
        }
    }

    private static class CompilerDef {
        public String type;
        public String path;
        public String targetCombo;
    }

    private void loadCompilerDefs(NativeLibrary lib, Project project) {


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

                        GCCCppCompiler compiler = new GCCCppCompiler(def.path);
                        lib.getCppCompilers().add(compiler);



                    }

                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        //}



    }
}
