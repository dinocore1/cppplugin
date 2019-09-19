package com.devsmart.cppplugin;

public abstract class AbstractCompiler implements Tool {

    boolean isCross;
    Platform targetPlatform;

    public boolean isCross() {
        return isCross;
    }

    public Platform getTargetPlatform() {
        return targetPlatform;
    }
}
