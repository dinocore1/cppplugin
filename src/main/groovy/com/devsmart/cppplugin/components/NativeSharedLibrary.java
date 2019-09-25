package com.devsmart.cppplugin.components;

import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.nativeplatform.tasks.LinkSharedLibrary;

public interface NativeSharedLibrary extends NativeBinary, NativeComponentWithRuntime {

    /**
     * Returns the link libraries to use to link the shared library. Includes the link libraries of the component's dependencies.
     */
    FileCollection getLinkLibraries();

    /**
     * Returns the link task for the shared library.
     */
    Provider<? extends LinkSharedLibrary> getLinkTask();
}
