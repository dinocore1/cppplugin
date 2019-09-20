package com.devsmart.cppplugin;

import org.gradle.workers.WorkAction;
import org.gradle.workers.internal.ProvidesWorkResult;

public abstract class CompileWorkAction implements WorkAction<CppCompileParameters>, ProvidesWorkResult {

}
