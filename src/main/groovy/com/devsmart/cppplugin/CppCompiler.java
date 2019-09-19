package com.devsmart.cppplugin;

import org.gradle.workers.WorkAction;

public abstract class CppCompiler extends AbstractTool {


    public abstract Class<? extends WorkAction<CppCompileParameters>> getCompileActionClass();
}
