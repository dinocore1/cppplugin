package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.file.RelativePath;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.nativeplatform.Linkage;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.util.Objects;
import java.util.Set;


public class CppCompileTask extends AbstractCompileTask {

    private final Property<CppStandard> cppStandard;
    private VariantIdentity variant;

    @Inject
    public CppCompileTask() {
        super();
        cppStandard = getObjectFactory().property(CppStandard.class);
    }



    public void from(NativeLibraryModel lib, VariantIdentity variant, ToolChain toolChain) {
        this.variant = variant;
        getSource().setFrom(lib.getCppSource());
        getIncludes().setFrom(lib.getIncludeDirs());

        RelativePath outputDir = new RelativePath(false, "obj",
                variant.getPlatform().getOperatingSystem().getName().toLowerCase(),
                variant.getPlatform().getMachineArchitecture().getName().toLowerCase());

        getOutputDir().set(getProject().getLayout().getBuildDirectory().dir(outputDir.getPathString()));
        getToolChain().set(toolChain);
        getCppStandard().set(lib.getCppStandard());

        if(Linkage.SHARED == variant.getLinkage()) {
            getFlags().add("-fpic");
        }

        if(variant.isDebuggable()) {
            getFlags().add("-g");
        }
    }

    @TaskAction
    public void compile(InputChanges inputs) {

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

    @Input
    public Property<CppStandard> getCppStandard() {
        return cppStandard;
    }


}
