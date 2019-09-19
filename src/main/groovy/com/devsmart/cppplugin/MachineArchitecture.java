package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;
import org.gradle.internal.impldep.com.google.common.collect.ImmutableList;
import org.gradle.nativeplatform.platform.Architecture;

import java.util.Arrays;
import java.util.List;

public class MachineArchitecture implements Named {

    public static final Attribute<MachineArchitecture> MACHINE_ATTRIBUTE = Attribute.of(MachineArchitecture.class);


    public static final KnownArchitecture X86 = new MachineArchitecture.KnownArchitecture("x86", new String[]{"i386", "ia-32", "i686"});
    public static final KnownArchitecture X86_64 = new MachineArchitecture.KnownArchitecture("x86-64", new String[]{"x86_64", "amd64", "x64"});
    public static final KnownArchitecture ARM_V7 = new MachineArchitecture.KnownArchitecture("arm-v7", new String[]{"armv7", "arm", "arm32", "arm-v7a"});
    public static final KnownArchitecture ARM_V8 = new MachineArchitecture.KnownArchitecture("arm-v8", new String[]{"armv8", "arm64", "arm64-v8a"});
    public static final MachineArchitecture UNKNOWN = new MachineArchitecture("unknown");

    public static final List<KnownArchitecture> KNOWN_ARCHITECTURES = Arrays.asList(X86, X86_64, ARM_V7, ARM_V8);



    protected String name;

    public MachineArchitecture(String name) {
        this.name = name;
    }

    public static MachineArchitecture getHostArchitecture() {
        String archString = System.getProperty("os.arch");
        return MachineArchitecture.fromName(archString);
    }

    public static MachineArchitecture fromName(String name) {
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

    public static class KnownArchitecture extends MachineArchitecture {

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
