package com.devsmart.cppplugin.cmdline;

import org.gradle.internal.operations.BuildOperation;
import org.gradle.internal.operations.logging.BuildOperationLogger;

import java.io.File;
import java.util.Map;

public interface CommandLineToolInvocation extends BuildOperation {

    File getExe();

    Map<String, String> getEnvironment();

    File getWorkDirectory();

    Iterable<String> getArgs();

    BuildOperationLogger getLogger();
}