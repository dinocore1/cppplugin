package com.devsmart.cppplugin;

import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class DefaultGccToolchain implements ToolChain {

    private final ObjectFactory objectFactory;
    private Platform platform;

    @Inject
    public DefaultGccToolchain(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;

    }

    @Override
    public Platform getTargetPlatform() {
        return this.platform;
    }

    @Override
    public CppCompiler getCppCompiler() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public void platform(String os, String arch) {
        platform = new Platform(MachineArchitecture.fromName(arch), OperatingSystem.fromName(os));
    }


}
