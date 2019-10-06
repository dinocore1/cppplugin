package com.devsmart.cppplugin;

import java.io.File;
import java.util.Set;

public interface SharedLibraryLinkerSpec extends ToolSpec {

    Set<File> getObjectFiles();

    File getOutputFile();
}
