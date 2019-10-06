package com.devsmart.cppplugin.tasks;

import com.devsmart.cppplugin.*;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
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


public class CreateStaticLibrary extends DefaultTask {

    private final ConfigurableFileCollection objectFiles;
    private final RegularFileProperty outputFile;
    private final ListProperty<String> staticLibArgs;
    private final Property<ToolChain> toolChain;
    private final Provider<Platform> platformProvider;

    public CreateStaticLibrary() {
        ObjectFactory objectFactory = getProject().getObjects();
        this.objectFiles = getProject().files();
        this.outputFile = objectFactory.fileProperty();
        this.staticLibArgs = getProject().getObjects().listProperty(String.class);
        this.toolChain = objectFactory.property(ToolChain.class);
        this.platformProvider = toolChain.map(tools -> tools.getTargetPlatform());
    }

    /**
     * The source object files to be passed to the archiver.
     */
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    @SkipWhenEmpty
    public ConfigurableFileCollection getObjectFiles() {
        return objectFiles;
    }

    /**
     * Adds a set of object files to be linked. <p> The provided source object is evaluated as per {@link org.gradle.api.Project#files(Object...)}.
     */
    public void source(Object source) {
        this.objectFiles.from(source);
    }

    private class MyArchiverSpec implements StaticLibraryArchiverSpec {

        private final BuildOperationLogger operationLogger;

        MyArchiverSpec() {
            this.operationLogger = getOperationLoggerFactory().newOperationLogger(getName(), getTemporaryDir());
        }

        @Override
        public Set<File> getObjectFiles() {
            return CreateStaticLibrary.this.getObjectFiles().getFiles();
        }

        @Override
        public File getOutputFile() {
            return CreateStaticLibrary.this.getOutputFile().getAsFile().get();
        }

        @Override
        public List<String> getArgs() {
            return CreateStaticLibrary.this.staticLibArgs.get();
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
        StaticLibraryArchiverSpec spec = new MyArchiverSpec();
        spec.getOperationLogger().start();
        Tool<StaticLibraryArchiverSpec> tool = toolChain.get().getArchiveTool();
        WorkResult result = tool.execute(spec);
        spec.getOperationLogger().done();
        setDidWork(result.getDidWork());
    }

    @OutputFile
    public RegularFileProperty getOutputFile() {
        return outputFile;
    }

    @Input
    public ListProperty<String> getStaticLibArgs() {
        return staticLibArgs;
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
