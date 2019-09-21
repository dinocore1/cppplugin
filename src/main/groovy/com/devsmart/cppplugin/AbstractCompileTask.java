package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.internal.impldep.com.google.common.base.Predicate;
import org.gradle.internal.impldep.com.google.common.collect.Iterables;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

public abstract class AbstractCompileTask extends DefaultTask {

    // See https://docs.gradle.org/current/userguide/custom_tasks.html#worker_api
    //See AbstractNativeCompileTask, AbstractTool<T extends BinaryToolSpec>, BuildOperationExecutor, NativeCompiler<T extends NativeCompileSpec>::newInvocationAction


    final ConfigurableFileCollection source;
    final ConfigurableFileCollection includes;
    final Property<ToolChain> toolChain;
    final Map<String, String> macros = new LinkedHashMap<>();
    final Set<String> flags = new LinkedHashSet<>();
    final DirectoryProperty outputDir;

    public AbstractCompileTask() {
        ObjectFactory objectFactory = getObjectFactory();
        source = objectFactory.fileCollection();
        dependsOn(source);
        includes = objectFactory.fileCollection();
        dependsOn(includes);
        toolChain = objectFactory.property(ToolChain.class);
        outputDir = objectFactory.directoryProperty();

    }

    @Inject
    public ObjectFactory getObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    public WorkerExecutor getWorkerExecutor() {
        throw new UnsupportedOperationException();
    }

    @Internal
    public Property<ToolChain> getToolChain() {
        return this.toolChain;
    }

    @SkipWhenEmpty
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public ConfigurableFileCollection getSource() {
        return this.source;
    }

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public ConfigurableFileCollection getIncludes() {
        return this.includes;
    }

    @Input
    public Map<String, String> getMacros() {
        return this.macros;
    }

    @Nested
    public Platform getTargetPlatform() {
        return toolChain.get().getTargetPlatform();
    }

    @Input
    public Set<String> getFlags() {
        return this.flags;
    }

    @Internal
    public DirectoryProperty getOutputDir() {
        return this.outputDir;
    }

    protected Path getRelitivePath(File input) {
        File projectRoot = getProject().getLayout().getProjectDirectory().getAsFile();
        Path relPath = projectRoot.toPath().relativize(input.toPath());
        return relPath;
    }

    protected abstract void getOutputsFileForInput(File input, Action<File> output);

    @OutputFiles
    public Iterable<File> getObjectFiles() {
        Set<File> retval = new HashSet<>();
        source.forEach( i -> {
            getOutputsFileForInput(i, o -> {
                retval.add(o);
            });
        });
        return retval;

    }

}
