package com.devsmart.cppplugin.components;

import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;

public interface NativeComponentWithRuntime extends NativeComponent {

    /**
     * Returns the file of this component.
     */
    Provider<RegularFile> getRuntimeFile();
}
