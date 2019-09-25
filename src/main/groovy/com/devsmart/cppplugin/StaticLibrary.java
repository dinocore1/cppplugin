package com.devsmart.cppplugin;

import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.FileCollection;

import javax.inject.Inject;

public class StaticLibrary implements SoftwareComponent {

    private final Names names;
    private final VariantIdentity variant;

    @Inject
    public StaticLibrary(Names names, VariantIdentity variant) {
        this.names = names;
        this.variant = variant;

    }

    @Override
    public String getName() {
        return names.getBaseName();
    }

    public VariantIdentity getVariant() {
        return this.variant;
    }

    public FileCollection getSource() {

    }

    public FileCollection getPrivateHeaders() {

    }

    public FileCollection getPublicHeaders() {

    }
}
