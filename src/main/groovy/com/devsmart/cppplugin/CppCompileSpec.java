package com.devsmart.cppplugin;

import com.google.common.base.Function;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CppCompileSpec extends ToolSpec {

    Set<File> getSourceFiles();
    Set<File> getIncludeDirs();
    File getObjectFile(File sourceFile);
    Map<String, String> getMacros();
    CppStandard getCppStandard();
    boolean isPositionIndependent();

}
