package com.devsmart.cppplugin;

import com.google.common.base.Function;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CppCompileSpec extends ToolSpec {

    Set<File> getSourceFiles();
    Set<File> getIncludeDirs();
    Function<File, File> getSourceToObject();
    CppStandard getCppStandard();
    Map<String, String> getMacros();

}
