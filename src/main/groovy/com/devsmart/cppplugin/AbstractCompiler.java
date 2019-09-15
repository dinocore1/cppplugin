package com.devsmart.cppplugin;

public abstract class AbstractCompiler implements Tool {

    boolean isCross;
    MachineArchitecture machineArch;
    Platform platform;

    public boolean isCross() {
        return isCross;
    }

    public MachineArchitecture getMachineArch() {
        return machineArch;
    }

    public Platform getPlatform() {
        return platform;
    }
}
