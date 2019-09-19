package com.devsmart.cppplugin;

import org.gradle.nativeplatform.Linkage;

import java.util.Objects;

public class VariantIdentity {

    private final Platform platform;
    private final boolean debuggable;
    private final Linkage linkage;

    public VariantIdentity(Platform platform, boolean debuggable, Linkage linkage) {
        this.platform = platform;
        this.debuggable = debuggable;
        this.linkage = linkage;
    }

    public Platform getPlatform() {
        return platform;
    }

    public boolean isDebuggable() {
        return debuggable;
    }

    public Linkage getLinkage() {
        return linkage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, debuggable, linkage);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != getClass()) {
            return false;
        }

        VariantIdentity other = (VariantIdentity) obj;
        return Objects.equals(platform, other.platform) &&
                Objects.equals(debuggable, other.debuggable) &&
                Objects.equals(linkage, other.linkage);
    }

    @Override
    public String toString() {
        return platform.toString() + "_" + linkage.getName() + (debuggable ? "_debug" : "");
    }
}
