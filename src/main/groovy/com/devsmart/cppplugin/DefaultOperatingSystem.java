package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;

import java.util.Arrays;
import java.util.List;

public class DefaultOperatingSystem implements OperatingSystem {

    public static final DefaultOperatingSystem LINUX = new DefaultOperatingSystem("linux");
    public static final DefaultOperatingSystem WINDOWS = new DefaultOperatingSystem("windows");
    public static final DefaultOperatingSystem UNKNOWN = new DefaultOperatingSystem("unknown");

    public static final List<DefaultOperatingSystem> KNOWN_OPERATINGSYSTEMS = Arrays.asList(LINUX, WINDOWS);


    private String name;

    public DefaultOperatingSystem(String name) {
        this.name = name;
    }

    public static DefaultOperatingSystem getHostOS() {
        String osArch = System.getProperty("os.name");
        return DefaultOperatingSystem.fromName(osArch);
    }

    public static DefaultOperatingSystem fromName(String name) {
        for(DefaultOperatingSystem os : KNOWN_OPERATINGSYSTEMS) {
            if(os.name.equals(name)) {
                return os;
            }
        }

        return UNKNOWN;
    }

    @Input
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return UNKNOWN.name.equals(name);
        }

        String otherString = null;
        if(obj instanceof String) {
            otherString = (String) obj;
        } else if(obj instanceof Named) {
            otherString = ((Named) obj).getName();
        }

        return name.equals(otherString);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
