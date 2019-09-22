package com.devsmart.cppplugin;

import org.gradle.api.tasks.WorkResult;

import java.io.File;

public interface Tool<T extends ToolSpec> {

    File getExe();
    void exe(String exeFilePath);
    void exe(File exeFilePath);

    WorkResult execute(T spec);
}
