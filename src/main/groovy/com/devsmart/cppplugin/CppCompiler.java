package com.devsmart.cppplugin;

public abstract class CppCompiler extends AbstractCompiler {

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
