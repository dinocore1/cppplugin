package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;

import java.util.Arrays;
import java.util.List;

public class OperatingSystem implements Named {

    public static final Attribute<? super OperatingSystem> OPERATING_SYSTEM_ATTRIBUTE = Attribute.of("com.devsmart.operatingsystem", OperatingSystem.class);;

    public static final OperatingSystem LINUX = new OperatingSystem("linux");
    public static final OperatingSystem WINDOWS = new OperatingSystem("windows");
    public static final OperatingSystem UNKNOWN = new OperatingSystem("unknown");

    public static final List<OperatingSystem> KNOWN_OPERATINGSYSTEMS = Arrays.asList(LINUX, WINDOWS);


    private String name;

    public OperatingSystem(String name) {
        this.name = name;
    }

    public static OperatingSystem getHostOS() {
        String osArch = System.getProperty("os.name");
        return OperatingSystem.fromName(osArch);
    }

    public static OperatingSystem fromName(String name) {
        for(OperatingSystem os : KNOWN_OPERATINGSYSTEMS) {
            if(os.name.equals(name)) {
                return os;
            }
        }

        return UNKNOWN;
    }

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
