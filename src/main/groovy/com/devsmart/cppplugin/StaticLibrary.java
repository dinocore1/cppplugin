package com.devsmart.cppplugin;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.FileCollection;

import javax.inject.Inject;

public class StaticLibrary extends AbstractNativeModule {



    @Inject
    public StaticLibrary(String name, Names names, VariantIdentity variant, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        super(name, names, variant, componentIncludeDirs, componentImplementation);


    }


}
