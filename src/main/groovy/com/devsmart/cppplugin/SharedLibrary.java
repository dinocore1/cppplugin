package com.devsmart.cppplugin;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;

import javax.inject.Inject;

public class SharedLibrary extends AbstractNativeModuleWithRuntimeDependencies {

    @Inject
    public SharedLibrary(String name, Names names, VariantIdentity variant, FileCollection sourceFiles, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        super(name, names, variant, sourceFiles, componentIncludeDirs, componentImplementation);
    }
}
