package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.tasks.Input;

public class TargetCombo implements Named {

    private MachineArchitecture machineArchitecture;
    private Platform platform;


    @Input
    public String getName() {
        return String.format("%s_%s", platform.getName(), machineArchitecture.getName());
    }
}
