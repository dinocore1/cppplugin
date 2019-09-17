package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.tasks.Input;

import java.util.Objects;

public class TargetCombo {

    private MachineArchitecture machineArchitecture;
    private Platform platform;

    public TargetCombo(MachineArchitecture machineArchitecture, Platform platform) {
        this.machineArchitecture = machineArchitecture;
        this.platform = platform;
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineArchitecture, platform);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != getClass()) {
            return false;
        }

        TargetCombo other = (TargetCombo) obj;
        return Objects.equals(machineArchitecture, other.machineArchitecture) &&
                Objects.equals(platform, other.platform);
    }

    @Override
    public String toString() {
        return String.format("%s_%s", platform.getName(), machineArchitecture.getName());
    }
}
