package com.devsmart.cppplugin;

import org.gradle.workers.WorkAction;

public class GccCppCompiler extends CppCompiler {


    @Override
    public Class<? extends WorkAction<CppCompileParameters>> getCompileActionClass() {
        return null;
    }
}
