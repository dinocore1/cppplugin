package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;

public interface OperatingSystem extends Named {

    Attribute<OperatingSystem> OPERATING_SYSTEM_ATTRIBUTE = Attribute.of("com.devsmart.operatingsystem", OperatingSystem.class);;

    @Override
    @Input
    String getName();
}
