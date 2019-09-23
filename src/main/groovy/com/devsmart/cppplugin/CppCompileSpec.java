package com.devsmart.cppplugin;

import com.google.common.base.Function;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface CppCompileSpec extends ToolSpec {

    List<File> getSourceFiles();
    List<File> getIncludeDirs();
    Function<File, File> getSourceToObject();
    CppStandard getCppStandard();
    Map<String, String> getMacros();

}
