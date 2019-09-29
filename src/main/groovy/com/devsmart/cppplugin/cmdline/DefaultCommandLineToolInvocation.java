package com.devsmart.cppplugin.cmdline;

import org.gradle.internal.operations.BuildOperationDescriptor;
import org.gradle.internal.operations.logging.BuildOperationLogger;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultCommandLineToolInvocation implements CommandLineToolInvocation {

    private File exe;
    private Iterable<String> args;
    private String description;
    private BuildOperationLogger logger;
    private Map<String, String> environment = new LinkedHashMap<>();

    public DefaultCommandLineToolInvocation(File exe, Iterable<String> args, String description, BuildOperationLogger logger) {
        this.exe = exe;
        this.args = args;
        this.description = description;
        this.logger = logger;
    }

    @Override
    public File getExe() {
        return this.exe;
    }

    @Override
    public Map<String, String> getEnvironment() {
        return environment;
    }

    @Override
    public File getWorkDirectory() {
        return null;
    }

    @Override
    public Iterable<String> getArgs() {
        return this.args;
    }

    @Override
    public BuildOperationLogger getLogger() {
        return this.logger;
    }

    @Override
    public BuildOperationDescriptor.Builder description() {
        return BuildOperationDescriptor.displayName(this.description);
    }
}
