package com.devsmart.cppplugin.components;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.SoftwareComponent;

public interface NativeComponentWithRuntimeUsage extends SoftwareComponent {

    Configuration getRuntimeDependencies();
}
