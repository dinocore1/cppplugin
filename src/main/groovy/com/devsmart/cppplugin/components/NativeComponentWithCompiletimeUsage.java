package com.devsmart.cppplugin.components;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.SoftwareComponent;

public interface NativeComponentWithCompiletimeUsage extends SoftwareComponent {

    Configuration getCompileDependencies();
}
