package com.devsmart.cppplugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface CppCompileSpec extends ToolSpec {

    List<File> getSourceFiles();
    File getObjectFileDir();
    CppStandard getCppStandard();
    Map<String, String> getMacros();

}
