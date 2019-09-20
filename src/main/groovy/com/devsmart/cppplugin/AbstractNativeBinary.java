package com.devsmart.cppplugin;

import org.gradle.util.VersionNumber;

public abstract class AbstractNativeBinary implements NativeBinary {


    private String name;
    private VersionNumber versionNumber;
    private VariantIdentity variantIdentity;

    public AbstractNativeBinary(String name, VersionNumber versionNumber, VariantIdentity variantIdentity) {
        this.name = name;
        this.versionNumber = versionNumber;
        this.variantIdentity = variantIdentity;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public VersionNumber getVersion() {
        return versionNumber;
    }

    @Override
    public VariantIdentity getVariant() {
        return variantIdentity;
    }
}
