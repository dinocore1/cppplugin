package com.devsmart.cppplugin;

public abstract class AbstractTool implements Tool {

    private String exePath;

    @Override
    public String getExePath() {
        return exePath;
    }
}