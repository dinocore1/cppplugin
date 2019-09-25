package com.devsmart.cppplugin.components;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.SoftwareComponent;


public interface NativeComponentWithLinktimeUsage extends SoftwareComponent {

    Configuration getLinkDependencies();

}
