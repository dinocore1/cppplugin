package com.devsmart.cppplugin;

import java.io.File;
import java.util.Set;

public interface StaticLibraryArchiverSpec extends ToolSpec {

    Set<File> getObjectFiles();

    File getOutputFile();
}
