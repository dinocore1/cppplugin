package com.devsmart.cppplugin.tasks;

import com.devsmart.cppplugin.Platform;
import com.devsmart.cppplugin.SharedLibraryLinkerSpec;
import com.devsmart.cppplugin.Tool;
import com.devsmart.cppplugin.ToolChain;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.internal.operations.logging.BuildOperationLogger;
import org.gradle.internal.operations.logging.BuildOperationLoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Set;

public class LinkSharedLibrary extends DefaultTask {

    private final ConfigurableFileCollection objectFiles;
    private final RegularFileProperty outputFile;
    private final ListProperty<String> linkArgs;
    private final Property<ToolChain> toolChain;
    private final Provider<Platform> platformProvider;

    public LinkSharedLibrary() {
        ObjectFactory objectFactory = getProject().getObjects();
        this.objectFiles = getProject().files();
        this.outputFile = objectFactory.fileProperty();
        this.linkArgs = getProject().getObjects().listProperty(String.class);
        this.toolChain = objectFactory.property(ToolChain.class);
        this.platformProvider = toolChain.map(tools -> tools.getTargetPlatform());
    }

    private class MySharedSpec implements SharedLibraryLinkerSpec {

        private final BuildOperationLogger operationLogger;

        MySharedSpec() {
            this.operationLogger = getOperationLoggerFactory().newOperationLogger(getName(), getTemporaryDir());
        }

        @Override
        public Set<File> getObjectFiles() {
            return LinkSharedLibrary.this.getObjectFiles().getFiles();
        }

        @Override
        public File getOutputFile() {
            return LinkSharedLibrary.this.getOutputFile().getAsFile().get();
        }

        @Override
        public List<String> getArgs() {
            return LinkSharedLibrary.this.linkArgs.get();
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
    public void link() {
        SharedLibraryLinkerSpec spec = new MySharedSpec();
        spec.getOperationLogger().start();
        Tool<SharedLibraryLinkerSpec> tool = toolChain.get().getLinkerTool();
        WorkResult result = tool.execute(spec);
        spec.getOperationLogger().done();
        setDidWork(result.getDidWork());
    }

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    @SkipWhenEmpty
    public ConfigurableFileCollection getObjectFiles() {
        return this.objectFiles;
    }

    @OutputFile
    public RegularFileProperty getOutputFile() {
        return this.outputFile;
    }

    @Input
    public ListProperty<String> getLinkArgs() {
        return this.linkArgs;
    }

    @Nested
    public Provider<Platform> getTargetPlatform() {
        return this.platformProvider;
    }

    @Internal
    public Property<ToolChain> getToolChain() {
        return this.toolChain;
    }

    @Inject
    protected BuildOperationLoggerFactory getOperationLoggerFactory() {
        throw new UnsupportedOperationException();
    }
}
