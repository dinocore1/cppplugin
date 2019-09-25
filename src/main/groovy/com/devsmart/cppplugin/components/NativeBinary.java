package com.devsmart.cppplugin.components;

import org.gradle.util.VersionNumber;

public interface NativeBinary extends NativeComponentWithObjectFiles, NativeComponentWithLinktimeUsage {


    VersionNumber getVersion();

}
