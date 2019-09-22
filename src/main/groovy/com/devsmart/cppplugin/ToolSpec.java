package com.devsmart.cppplugin;

import org.gradle.internal.operations.logging.BuildOperationLogger;

import java.util.List;

public interface ToolSpec {

    List<String> getArgs();
    void args(List<String> args);

    BuildOperationLogger getOperationLogger();
    void setOperationLogger(BuildOperationLogger oplogger);
}
