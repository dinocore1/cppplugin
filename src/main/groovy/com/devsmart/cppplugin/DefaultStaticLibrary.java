package com.devsmart.cppplugin;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.util.PatternSet;

import javax.inject.Inject;

public class DefaultStaticLibrary extends AbstractNativeModuleWithLinkDependencies {

    @Inject
    public DefaultStaticLibrary(String name, Names names, VariantIdentity variant, FileCollection sourceFiles, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        super(name, names, variant, sourceFiles, componentIncludeDirs, componentImplementation);
    }


}
