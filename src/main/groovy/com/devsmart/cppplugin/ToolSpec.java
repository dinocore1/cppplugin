package com.devsmart.cppplugin;

import org.gradle.internal.operations.logging.BuildOperationLogger;

import java.util.List;

public interface ToolSpec {

    List<String> getArgs();

    BuildOperationLogger getOperationLogger();
    void setOperationLogger(BuildOperationLogger oplogger);
}
