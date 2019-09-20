package com.devsmart.cppplugin;

import org.gradle.api.Task;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.provider.Property;
import org.gradle.util.VersionNumber;

public interface NativeBinary extends SoftwareComponent {

    VariantIdentity getVariant();
    VersionNumber getVersion();
    Property<Task> getCompileTask();

}
