package com.devsmart.cppplugin;

import com.google.common.base.Function;
import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.internal.Cast;
import org.gradle.internal.operations.logging.BuildOperationLogger;
import org.gradle.internal.operations.logging.BuildOperationLoggerFactory;
import org.gradle.work.InputChanges;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CppCompileTask extends AbstractCompileTask {

    private final Property<CppStandard> cppStandard;
    private final Property<Platform> platform;

    @Inject
    public CppCompileTask() {
        super();
        ObjectFactory objects = getObjectFactory();
        this.platform = objects.property(Platform.class);
        this.cppStandard = objects.property(CppStandard.class);
    }


    @Input
    public Property<CppStandard> getCppStandard() {
        return this.cppStandard;
    }

    @Nested
    public Property<Platform> getTargetPlatform() {
        return this.platform;
    }

    private class MyCompileSpec implements CppCompileSpec {

        private final BuildOperationLogger operationLogger;

        MyCompileSpec() {
            operationLogger = getOperationLoggerFactory().newOperationLogger(getName(), getTemporaryDir());
        }

        @Override
        public Set<File> getSourceFiles() {
            return source.getFiles();
        }

        @Override
        public Set<File> getIncludeDirs() {
            return includes.getFiles();
        }

        @Override
        public Function<File, File> getSourceToObject() {
            return null;
        }

        @Override
        public CppStandard getCppStandard() {
            return cppStandard.get();
        }

        @Override
        public Map<String, String> getMacros() {
            return getMacros();
        }

        @Override
        public List<String> getArgs() {
            return getArgs();
        }

        @Override
        public BuildOperationLogger getOperationLogger() {
            return operationLogger;
        }

        @Override
        public void setOperationLogger(BuildOperationLogger oplogger) {

        }
    }

    @TaskAction
    public void compile(InputChanges inputs) {

        CppCompileSpec spec = new MyCompileSpec();

        Tool<CppCompileSpec> cppCompilerTool = Cast.uncheckedCast(cppCompiler.get());
        WorkResult result = cppCompilerTool.execute(spec);
        setDidWork(result.getDidWork());


        /*
        WorkerExecutor workerExecutor = getWorkerExecutor();

        ToolChain theToolchain = toolChain.get();
        CppCompiler theCppCompiler = theToolchain.getCppCompiler();


        WorkQueue queue = workerExecutor.noIsolation();
        source.forEach( f -> {

            Class<? extends WorkAction<CppCompileParameters>> compileAction = theCppCompiler.getCompileActionClass();

            queue.submit(compileAction, params -> {
                params.getExePath().set(theCppCompiler.getExePath());
                params.getSrcFile().set(f);
                getOutputsFileForInput(f, o -> {
                    params.getOutputFile().set(o);
                });
                params.getIncludeDirs().set(includes);
                params.getMacros().set(macros);
                params.getFlags().set(flags);
                params.getCppStandard().set(cppStandard);
            });


        });

        queue.await();
         */

    }

    @Override
    protected void getOutputsFileForInput(File input, Action<File> output) {
        ToolChain theToolchain = toolChain.get();
        File theOutputDir = outputDir.getAsFile().get();

        int hash = Objects.hash(variant, getRelitivePath(input));
        String hexStr = Integer.toHexString(hash);
        final String objFileExt = theToolchain.getObjectFileExtention();
        String outputFileName = String.format("%s_%s.%s", input.getName(), hexStr, objFileExt);

        File outputFile = new File(theOutputDir, outputFileName);
        output.execute(outputFile);
    }

    @Inject
    BuildOperationLoggerFactory getOperationLoggerFactory() {
        throw new UnsupportedOperationException();
    }




}
