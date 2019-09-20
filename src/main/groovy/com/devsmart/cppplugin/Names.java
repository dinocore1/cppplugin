package com.devsmart.cppplugin;

import org.apache.commons.lang3.StringUtils;

public class Names {

    private final String name;

    public Names(String name) {
        this.name = name;
    }

    public static Names of(VariantIdentity variant) {
        Platform platform = variant.getPlatform();
        String name = platform.getOperatingSystem().getName()
                + StringUtils.capitalize(platform.getMachineArchitecture().getName())
                + StringUtils.capitalize(variant.getLinkage().getName().toLowerCase())
                + (variant.isDebuggable() ? "Debug" : "");
        return new Names(name);
    }

    public String getTaskName(String verb) {
        return verb + StringUtils.capitalize(name);
    }
}
