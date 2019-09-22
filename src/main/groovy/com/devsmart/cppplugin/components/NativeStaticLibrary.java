package com.devsmart.cppplugin.components;

import com.devsmart.cppplugin.tasks.CreateStaticLibrary;
import org.gradle.api.provider.Provider;

public interface NativeStaticLibrary extends NativeComponentWithRuntime {

    Provider<? extends CreateStaticLibrary> getCreateTask();
}
