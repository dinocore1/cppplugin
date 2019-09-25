package com.devsmart.cppplugin.components;

import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;

/**
 * Represents a component that produces outputs that run on a native platform.
 * Examples:
 *   - shared lib file (.so)
 *   - executable file (.exe)
 */
public interface NativeComponentWithRuntimeArtifact extends NativeComponent {

    /**
     * Returns the file of this component.
     */
    Provider<RegularFile> getRuntimeFile();


}
