package com.devsmart.cppplugin;

import org.gradle.internal.operations.logging.BuildOperationLogger;

import java.io.File;
import java.util.List;

public class DefaultStaticLibraryArchiverSpec implements StaticLibraryArchiverSpec {
    @Override
    public List<File> getObjectFiles() {
        return null;
    }

    @Override
    public void objectFiles(Iterable<File> inputFiles) {

    }

    @Override
    public File getOutputFile() {
        return null;
    }

    @Override
    public void setOutputFile(File outputFile) {

    }

    @Override
    public List<String> getArgs() {
        return null;
    }

    @Override
    public void args(List<String> args) {

    }

    @Override
    public BuildOperationLogger getOperationLogger() {
        return null;
    }

    @Override
    public void setOperationLogger(BuildOperationLogger oplogger) {

    }
}
