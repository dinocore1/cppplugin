package com.devsmart.cppplugin;

import java.io.File;
import java.util.List;

public interface StaticLibraryArchiverSpec extends ToolSpec {

    List<File> getObjectFiles();
    void objectFiles(Iterable<File> inputFiles);

    File getOutputFile();
    void setOutputFile(File outputFile);
}
