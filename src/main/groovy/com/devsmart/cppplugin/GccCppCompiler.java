package com.devsmart.cppplugin;

import org.gradle.internal.impldep.aQute.bnd.build.Run;
import org.gradle.workers.WorkAction;
import org.gradle.workers.internal.DefaultWorkResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GccCppCompiler extends CppCompiler {


    @Override
    public Class<? extends WorkAction<CppCompileParameters>> getCompileActionClass() {
        return GccCompileWorkAction.class;
    }

    public static abstract class GccCompileWorkAction extends CompileWorkAction {

        DefaultWorkResult workResult;

        @Override
        public DefaultWorkResult getWorkResult() {
            return workResult;
        }

        @Override
        public void execute() {
            CppCompileParameters params = getParameters();

            List<String> commands = new LinkedList<String>();
            commands.add(params.getExePath().get());
            commands.add("-c");
            commands.add(params.getSrcFile().get().getAbsolutePath());

            commands.add("-o");
            commands.add(params.getOutputFile().get().getAbsolutePath());

            params.getIncludeDirs().get().forEach(f -> {
               commands.add("-I");
               commands.add(f.getAbsolutePath());
            });

            params.getMacros().get().forEach((k, v) -> {
                commands.add("-D"+k+"="+v);
            });

            Set<String> flags = new LinkedHashSet<>(params.getFlags().get());
            if(params.getCppStandard().isPresent()) {
                flags.add("-std=" + params.getCppStandard().get().getName());
            }

            flags.forEach(v -> {
                commands.add(v);
            });

            try {
                Process process = new ProcessBuilder(commands)
                        .start();
                int exitCode = process.waitFor();
                if(exitCode == 0){
                    workResult = DefaultWorkResult.SUCCESS;
                } else {
                    workResult = new DefaultWorkResult(true, new Exception("compile process returned " + exitCode));
                }
            } catch (Exception e) {
                workResult = new DefaultWorkResult(true, e);
            }


        }
    }
}
