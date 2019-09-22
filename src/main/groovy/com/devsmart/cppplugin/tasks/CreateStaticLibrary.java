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


public class CreateStaticLibrary extends DefaultTask {

    private final ConfigurableFileCollection source;
    private final RegularFileProperty outputFile;
    private final ListProperty<String> staticLibArgs;
    private final Property<ToolChain> toolChain;
    private final Provider<Platform> platformProvider;

    public CreateStaticLibrary() {
        ObjectFactory objectFactory = getProject().getObjects();
        this.source = getProject().files();
        this.outputFile = objectFactory.fileProperty();
        this.staticLibArgs = getProject().getObjects().listProperty(String.class);
        this.toolChain = objectFactory.property(ToolChain.class);
        this.platformProvider = getProject().provider( () ->
           toolChain.get().getTargetPlatform()
        );
    }

    /**
     * The source object files to be passed to the archiver.
     */
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    @SkipWhenEmpty
    public FileCollection getSource() {
        return source;
    }

    /**
     * Adds a set of object files to be linked. <p> The provided source object is evaluated as per {@link org.gradle.api.Project#files(Object...)}.
     */
    public void source(Object source) {
        this.source.from(source);
    }

    @Inject
    BuildOperationLoggerFactory getOperationLoggerFactory() {
        throw new UnsupportedOperationException();
    }

    @TaskAction
    protected void link() {

        StaticLibraryArchiverSpec spec = new DefaultStaticLibraryArchiverSpec();
        spec.getObjectFiles().addAll(source.getFiles());
        spec.setOutputFile(outputFile.get().getAsFile());
        spec.getArgs().addAll(staticLibArgs.get());
        BuildOperationLogger operationLogger = getOperationLoggerFactory().newOperationLogger(getName(), getTemporaryDir());
        spec.setOperationLogger(operationLogger);

        Tool<StaticLibraryArchiverSpec> tool = toolChain.get().getArchiveTool();
        WorkResult result = tool.execute(spec);
        setDidWork(result.getDidWork());


        /*
        StaticLibraryArchiverSpec spec = new DefaultStaticLibraryArchiverSpec();
        spec.setTempDir(getTemporaryDir());
        spec.setOutputFile(getOutputFile().get().getAsFile());
        spec.objectFiles(getSource());
        spec.args(getStaticLibArgs().get());

        BuildOperationLogger operationLogger = getOperationLoggerFactory().newOperationLogger(getName(), getTemporaryDir());
        spec.setOperationLogger(operationLogger);

        Compiler<StaticLibraryArchiverSpec> compiler = createCompiler();
        WorkResult result = BuildOperationLoggingCompilerDecorator.wrap(compiler).execute(spec);
        setDidWork(result.getDidWork());
        */

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
}
