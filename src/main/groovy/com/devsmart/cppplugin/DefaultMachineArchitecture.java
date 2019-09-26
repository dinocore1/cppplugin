package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;

import java.util.Arrays;
import java.util.List;

public class DefaultMachineArchitecture implements MachineArchitecture {




    public static final KnownArchitecture X86 = new DefaultMachineArchitecture.KnownArchitecture("x86", new String[]{"i386", "ia-32", "i686"});
    public static final KnownArchitecture X86_64 = new DefaultMachineArchitecture.KnownArchitecture("x86-64", new String[]{"x86_64", "amd64", "x64"});
    public static final KnownArchitecture ARM_V7 = new DefaultMachineArchitecture.KnownArchitecture("arm-v7", new String[]{"armv7", "arm", "arm32", "arm-v7a"});
    public static final KnownArchitecture ARM_V8 = new DefaultMachineArchitecture.KnownArchitecture("arm-v8", new String[]{"armv8", "arm64", "arm64-v8a"});
    public static final DefaultMachineArchitecture UNKNOWN = new DefaultMachineArchitecture("unknown");

    public static final List<KnownArchitecture> KNOWN_ARCHITECTURES = Arrays.asList(X86, X86_64, ARM_V7, ARM_V8);


    protected String name;

    public DefaultMachineArchitecture(String name) {
        this.name = name;
    }

    public static DefaultMachineArchitecture getHostArchitecture() {
        String archString = System.getProperty("os.arch");
        return DefaultMachineArchitecture.fromName(archString);
    }

    public static DefaultMachineArchitecture fromName(String name) {
        for(KnownArchitecture arch : KNOWN_ARCHITECTURES) {
            if(arch.isAlias(name)) {
                return arch;
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

    public static class KnownArchitecture extends DefaultMachineArchitecture {

        private final List<String> alias;

        public KnownArchitecture(String name, String[] alias) {
            super(name);
            this.alias = Arrays.asList(alias);
        }

        public boolean isAlias(String input) {
            return name.equals(input) || alias.contains(input);
        }
    }
}
