package com.devsmart.cppplugin.components;

import org.gradle.api.file.FileCollection;

public interface NativeComponentWithObjectFiles extends NativeComponent {

    FileCollection getObjects();
}
