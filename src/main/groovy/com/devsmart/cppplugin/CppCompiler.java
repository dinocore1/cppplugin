package com.devsmart.cppplugin;

public class CppCompiler extends AbstractCompiler {

    @Override
    public String getExecutablePath() {
        return null;
    }

    public enum Standard {
        CPP98,
        CPP03,
        CPP11,
        CPP14,
        CPP17,
        CPP20
    }

    private Standard standard;
}
