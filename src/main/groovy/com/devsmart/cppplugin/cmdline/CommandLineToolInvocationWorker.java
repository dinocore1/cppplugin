package com.devsmart.cppplugin.cmdline;

import org.gradle.internal.operations.BuildOperationWorker;

public interface CommandLineToolInvocationWorker extends BuildOperationWorker<CommandLineToolInvocation> {
    /**
     * Returns a human consumable name for this tool.
     */
    @Override
    String getDisplayName();
}