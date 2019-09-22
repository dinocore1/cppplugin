package com.devsmart.cppplugin.cmdline;

import org.gradle.internal.operations.BuildOperation;
import org.gradle.internal.operations.BuildOperationFailure;

class CommandLineToolInvocationFailure extends BuildOperationFailure {
    CommandLineToolInvocationFailure(BuildOperation operation, String message) {
        super(operation, message);
    }
}