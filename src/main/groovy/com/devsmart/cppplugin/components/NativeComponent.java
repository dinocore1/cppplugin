package com.devsmart.cppplugin.components;

import com.devsmart.cppplugin.Platform;
import org.gradle.api.component.SoftwareComponent;

/**
 *  Represents a component that produces output to run on a native platform
 */
public interface NativeComponent extends SoftwareComponent {

    /**
     * This is used to calculate output file names.
     * @return
     */
    String getBaseName();

    Platform getTargetPlatform();
}
