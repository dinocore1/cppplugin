package com.devsmart.cppplugin;

public abstract class AbstractCompiler implements Tool {

    boolean isCross;
    TargetCombo targetCombo;

    public boolean isCross() {
        return isCross;
    }

    public TargetCombo getTargetCombo() {
        return targetCombo;
    }
}
