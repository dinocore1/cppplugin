package com.devsmart.cppplugin;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.workers.WorkParameters;

import java.io.File;

public interface CppCompileParameters extends WorkParameters {

    Property<String> getExePath();
    Property<File> getSrcFile();
    Property<File> getOutputFile();
    ListProperty<File> getIncludeDirs();
    MapProperty<String, String> getMacros();
    SetProperty<String> getFlags();
    Property<CppStandard> getCppStandard();

}
