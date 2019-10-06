package com.devsmart.cppplugin.components;

import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;

public interface NativeStaticLibrary extends SoftwareComponent {

    RegularFileProperty getStaticLibrary();
    Provider<Task> getCreateLibraryTask();
    Provider<Configuration> getLinkConfiguration();

}
