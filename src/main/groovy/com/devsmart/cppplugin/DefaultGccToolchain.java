package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class DefaultGccToolchain implements ToolChain {

    private final ObjectFactory objectFactory;
    private final GccCppCompiler cppCompiler;
    private final GccArchiverTool archiveTool;
    private Platform platform;

    @Inject
    public DefaultGccToolchain(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.cppCompiler = new GccCppCompiler();
        this.archiveTool = new GccArchiverTool();

    }

    @Override
    public Platform getTargetPlatform() {
        return this.platform;
    }

    @Override
    public CppCompiler getCppCompiler() {
        return this.cppCompiler;
    }

    @Override
    public ArchiverTool getArchiveTool() {
        return this.archiveTool;
    }

    @Override
    public String getObjectFileExtention() {
        return "o";
    }

    @Override
    public String getName() {
        return String.format("gcc %s", platform);
    }

    public void platform(String os, String arch) {
        platform = new Platform(MachineArchitecture.fromName(arch), OperatingSystem.fromName(os));
    }

    public void cppCompiler(Action<? super CppCompiler> config) {
        config.execute(cppCompiler);
    }

    public void archiveTool(Action<? super ArchiverTool> config) {
        config.execute(archiveTool);
    }


}
