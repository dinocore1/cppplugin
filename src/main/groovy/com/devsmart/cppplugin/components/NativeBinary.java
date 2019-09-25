package com.devsmart.cppplugin.components;

import com.devsmart.cppplugin.VariantIdentity;
import org.gradle.api.Task;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.provider.Property;
import org.gradle.util.VersionNumber;

public interface NativeBinary extends NativeComponentWithObjectFiles, NativeComponentWithLinkUsage {


    VersionNumber getVersion();

}
