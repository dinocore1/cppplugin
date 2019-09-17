package com.devsmart.cppplugin;

import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.gradle.nativeplatform.Linkage;

import java.util.Objects;

public class VariantIdentity {

    private final TargetCombo target;
    private final boolean debuggable;
    private final Linkage linkage;

    public VariantIdentity(TargetCombo target, boolean debuggable, Linkage linkage) {
        this.target = target;
        this.debuggable = debuggable;
        this.linkage = linkage;
    }

    public TargetCombo getTarget() {
        return target;
    }

    public boolean isDebuggable() {
        return debuggable;
    }

    public Linkage getLinkage() {
        return linkage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, debuggable, linkage);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != getClass()) {
            return false;
        }

        VariantIdentity other = (VariantIdentity) obj;
        return Objects.equals(target, other.target) &&
                Objects.equals(debuggable, other.debuggable) &&
                Objects.equals(linkage, other.linkage);
    }

    @Override
    public String toString() {
        return target.toString() + "_" + linkage.getName() + (debuggable ? "_debug" : "");
    }
}
