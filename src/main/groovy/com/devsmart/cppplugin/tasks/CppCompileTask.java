package com.devsmart.cppplugin.tasks;

import com.devsmart.cppplugin.*;
import com.google.common.collect.Iterables;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.internal.operations.logging.BuildOperationLogger;
import org.gradle.internal.operations.logging.BuildOperationLoggerFactory;
import org.gradle.work.InputChanges;

import javax.inject.Inject;
import java.io.File;
import java.util.*;


public class CppCompileTask extends AbstractCompileTask {

    private final Property<CppStandard> cppStandard;
    private final Property<VariantIdentity> variant;

    @Inject
    public CppCompileTask() {
        super();
        ObjectFactory objects = getObjectFactory();
        this.variant = objects.property(VariantIdentity.class);
        this.cppStandard = objects.property(CppStandard.class);
    }


    @Input
    public Property<CppStandard> getCppStandard() {
        return this.cppStandard;
    }

    @Nested
    public Platform getTargetPlatform() {
        return this.variant.get().getPlatform();
    }

    @Internal
    public Property<VariantIdentity> getVariant() {
        return this.variant;
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
        public File getObjectFile(File sourceFile) {
            Set<File> retval = new HashSet<>();
            getOutputsFileForInput(sourceFile, o -> {
                retval.add(o);
            });
            return Iterables.getFirst(retval, null);
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
            return Arrays.asList();
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
        ToolChain theToolchain = getToolchain().get();
        Tool<CppCompileSpec> cppCompilerTool = theToolchain.getCppCompiler();
        spec.getOperationLogger().start();
        WorkResult result = cppCompilerTool.execute(spec);
        spec.getOperationLogger().done();
        setDidWork(result.getDidWork());
    }

    @Override
    protected void getOutputsFileForInput(File input, Action<File> output) {
        ToolChain theToolchain = getToolchain().get();
        File theOutputDir = outputDir.getAsFile().get();

        int hash = Objects.hash(variant.get(), getRelitivePath(input));
        String hexStr = Integer.toHexString(hash);
        final String objFileExt = theToolchain.getObjectFileExtention();
        String outputFileName = String.format("%s_%s.%s", input.getName(), hexStr, objFileExt);

        File outputFile = new File(theOutputDir, outputFileName);
        output.execute(outputFile);
    }

    @Inject
    protected BuildOperationLoggerFactory getOperationLoggerFactory() {
        throw new UnsupportedOperationException();
    }


}
