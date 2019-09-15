package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;

public abstract class MachineArchitecture implements Named {

    public static final Attribute<MachineArchitecture> MACHINE_ATTRIBUTE = Attribute.of(MachineArchitecture.class);

    //x86
    public static final String X86 = "x86";
    public static final String X86_64 = "x86-64";

    //Arm
    public static final String ARM_V7A = "arm-v7a";
    public static final String ARM64_V8A = "arm64-v8a";

    @Input
    public abstract String getName();
}
