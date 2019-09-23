package com.devsmart.cppplugin.tasks;

import com.devsmart.cppplugin.CppCompileSpec;
import com.devsmart.cppplugin.Platform;
import com.devsmart.cppplugin.ToolChain;
import com.devsmart.cppplugin.VariantIdentity;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class CompileTask extends DefaultTask {

    private final ConfigurableFileCollection source;
    private final ConfigurableFileCollection includeDirs;
    private final Provider<Iterable<File>> objectFiles;
    private final Property<ToolChain> toolChain;
    private final Provider<Platform> platformProvider;
    private final ListProperty<String> args;
    private final DirectoryProperty outputDir;
    private final Property<VariantIdentity> variant;
    private final Function<File, File> objectFileTransformer = new Function<File, File>() {

        @Override
        public File apply(File input) {
            VariantIdentity id = variant.get();
            ToolChain theToolchain = toolChain.get();
            File outputDir = getOutputDir().get().getAsFile();

            int hash = Objects.hash(id, getRelitivePath(input));
            String hexStr = Integer.toHexString(hash);
            final String objFileExt = theToolchain.getObjectFileExtention();
            String outputFileName = String.format("%s_%s.%s", input.getName(), hexStr, objFileExt);
            return new File(outputDir, outputFileName);
        }

        private Path getRelitivePath(File input) {
            File projectRoot = getProject().getLayout().getProjectDirectory().getAsFile();
            Path relPath = projectRoot.toPath().relativize(input.toPath());
            return relPath;
        }
    };

    public CompileTask() {
        ObjectFactory objectFactory = getProject().getObjects();
        this.source = getProject().files();
        this.includeDirs = getProject().files();
        this.outputDir = objectFactory.directoryProperty();
        this.objectFiles = getProject().provider(()-> {
            return Iterables.transform(source, objectFileTransformer);
        });
        this.toolChain = objectFactory.property(ToolChain.class);
        this.platformProvider = getProject().provider( () ->
                toolChain.get().getTargetPlatform()
        );
        this.args = objectFactory.listProperty(String.class);
        this.variant = objectFactory.property(VariantIdentity.class);
    }



    @TaskAction
    public void compile() {
        CppCompileSpec spec;


    }

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    @SkipWhenEmpty
    public FileCollection getSource() {
        return this.source;
    }

    @Internal
    public FileCollection getIncludeDirs() {
        return this.includeDirs;
    }

    @Nested
    public Provider<Platform> getTargetPlatform() {
        return this.platformProvider;
    }

    @Input
    public ListProperty<String> getArgs() {
        return this.args;
    }

    @OutputFiles
    public Provider<Iterable<File>> getObjectFiles() {
        return this.objectFiles;
    }

    @Internal
    public DirectoryProperty getOutputDir() {
        return this.outputDir;
    }

    @Internal
    public Property<ToolChain> getToolChain() {
        return this.toolChain;
    }

    /**
     * function that get the object filename from the input source file
     * @return
     */
    public Function<File, File> getObjectFileTransformer() {
        return this.objectFileTransformer;
    }
}
