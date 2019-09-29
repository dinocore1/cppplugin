package com.devsmart.cppplugin;

import org.gradle.api.model.ObjectFactory;

import java.util.Arrays;
import java.util.List;

public class MachineArchitectureFactory {


    public static final KnownArchitecture X86 = new MachineArchitectureFactory.KnownArchitecture("x86", new String[]{"i386", "ia-32", "i686"});
    public static final KnownArchitecture X86_64 = new MachineArchitectureFactory.KnownArchitecture("x86-64", new String[]{"x86_64", "amd64", "x64"});
    public static final KnownArchitecture ARM_V7 = new MachineArchitectureFactory.KnownArchitecture("arm-v7", new String[]{"armv7", "arm", "arm32", "arm-v7a"});
    public static final KnownArchitecture ARM_V8 = new MachineArchitectureFactory.KnownArchitecture("arm-v8", new String[]{"armv8", "arm64", "arm64-v8a"});

    public static final List<KnownArchitecture> KNOWN_ARCHITECTURES = Arrays.asList(X86, X86_64, ARM_V7, ARM_V8);

    private final ObjectFactory objects;

    public MachineArchitectureFactory(ObjectFactory objects) {
        this.objects = objects;
    }

    public MachineArchitecture getHostArchitecture() {
        String archString = System.getProperty("os.arch");
        return fromName(archString);
    }

    public MachineArchitecture fromName(String name) {
        for(KnownArchitecture arch : KNOWN_ARCHITECTURES) {
            if(arch.isAlias(name)) {
                return objects.named(MachineArchitecture.class, arch.getName());
            }
        }

        return objects.named(MachineArchitecture.class, name);
    }

    private static class KnownArchitecture {

        private final String name;
        private final List<String> alias;

        public KnownArchitecture(String name, String[] alias) {
            this.name = name;
            this.alias = Arrays.asList(alias);
        }

        public String getName() {
            return this.name;
        }

        public boolean isAlias(String input) {
            return name.equals(input) || alias.contains(input);
        }
    }
}
