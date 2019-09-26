package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;

public interface MachineArchitecture extends Named {

    Attribute<MachineArchitecture> ARCHITECTURE_ATTRIBUTE = Attribute.of("com.devsmart.architecture", MachineArchitecture.class);

    @Override
    @Input
    String getName();
}
