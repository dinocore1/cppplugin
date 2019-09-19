package com.devsmart.cppplugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.util.*;


public class CompileTask extends DefaultTask {

    // See https://docs.gradle.org/current/userguide/custom_tasks.html#worker_api
    //See AbstractNativeCompileTask, AbstractTool<T extends BinaryToolSpec>, BuildOperationExecutor, NativeCompiler<T extends NativeCompileSpec>::newInvocationAction

    private final WorkerExecutor workerExecutor;
    private final ConfigurableFileCollection source;
    private final ConfigurableFileCollection includes;
    private final Property<ToolChain> toolChain;
    private final Map<String, String> macros = new LinkedHashMap<>();
    private final Set<String> flags = new LinkedHashSet<>();
    private final DirectoryProperty outputDir;


    @Inject
    public CompileTask(WorkerExecutor workerExecutor, ObjectFactory objectFactory) {
        this.workerExecutor = workerExecutor;
        source = objectFactory.fileCollection();
        dependsOn(source);
        includes = objectFactory.fileCollection();
        dependsOn(includes);
        toolChain = objectFactory.property(ToolChain.class);
        outputDir = objectFactory.directoryProperty();

    }

    @TaskAction
    public void compile(InputChanges inputs) {


        ToolChain theToolchain = toolChain.get();
        CppCompiler theCppCompiler = theToolchain.getCppCompiler();
        Platform thePlatform = getTargetPlatform();
        Set<File> theIncludes = includes.getFiles();
        final int hashCode = Objects.hash(thePlatform, theIncludes, macros, flags);
        File theOutputDir = outputDir.getAsFile().get();

        WorkQueue queue = workerExecutor.noIsolation();
        source.forEach( f -> {

            Class<? extends WorkAction<CppCompileParameters>> compileAction = theCppCompiler.getCompileActionClass();

            queue.submit(compileAction, params -> {

                int outputHash = 31 * hashCode + f.hashCode();
                String outputFileName = String.format("%s_%d.o", f.getName(), outputHash);

                params.getSrcFile().set(f);
                params.getOutputFile().set(new File(theOutputDir, outputFileName));
                params.getIncludeDirs().set(includes);
                params.getMacros().set(macros);
                params.getFlags().set(flags);
            });


        });

    }

    @Internal
    Property<ToolChain> getToolChain() {
        return this.toolChain;
    }

    @SkipWhenEmpty
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public ConfigurableFileCollection getSource() {
        return this.source;
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

    @OutputDirectory
    public DirectoryProperty getOutputDir() {
        return this.outputDir;
    }
}
