package com.devsmart.cppplugin;

import org.gradle.api.Task;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.util.VersionNumber;

import javax.inject.Inject;

public class DefaultCppNativeBinary implements CppNativeBinary {

    private final NativeLibraryModel libraryModel;
    private final VariantIdentity variant;
    private final Property<Task> compileTask;


    @Inject
    public DefaultCppNativeBinary(ObjectFactory objectFactory, NativeLibraryModel libraryModel, VariantIdentity variantIdentity) {
        this.libraryModel = libraryModel;
        this.variant = variantIdentity;
        this.compileTask = objectFactory.property(Task.class);
    }

    @Override
    public CppStandard getCppStandard() {
        return libraryModel.getCppStandard().get();
    }

    @Override
    public VariantIdentity getVariant() {
        return variant;
    }

    @Override
    public VersionNumber getVersion() {
        //return libraryModel.getVersion();
        return null;
    }

    @Override
    public Property<Task> getCompileTask() {
        return this.compileTask;
    }

    @Override
    public String getName() {
        return libraryModel.getBaseName().get();
    }
}
