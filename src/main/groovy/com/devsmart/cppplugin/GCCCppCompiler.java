package com.devsmart.cppplugin;

public class GCCCppCompiler extends CppCompiler {

    private String path;

    public GCCCppCompiler(String path) {
        this.path = path;
    }

    @Override
    public String getExecutablePath() {
        return path;
    }
}
