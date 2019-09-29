package com.devsmart.cppplugin;

import org.gradle.api.model.ObjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MachineArchitectureFromNameTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "x86", MachineArchitectureFactory.X86 },
                { "i386", MachineArchitectureFactory.X86 },
                { "i686", MachineArchitectureFactory.X86 },
                { "ia-32", MachineArchitectureFactory.X86 },

                { "x86-64", MachineArchitectureFactory.X86_64 },
                { "x86_64", MachineArchitectureFactory.X86_64 },
                { "x64", MachineArchitectureFactory.X86_64 },
                { "amd64", MachineArchitectureFactory.X86_64 },

                { "arm-v7", MachineArchitectureFactory.ARM_V7 },
                { "arm", MachineArchitectureFactory.ARM_V7 },
                { "armv7", MachineArchitectureFactory.ARM_V7 },
                { "arm-v7a", MachineArchitectureFactory.ARM_V7 },
                { "arm32", MachineArchitectureFactory.ARM_V7 },

                { "arm64", MachineArchitectureFactory.ARM_V8 },
                { "arm-v8", MachineArchitectureFactory.ARM_V8 },
        });
    }

    private final String input;
    private final MachineArchitectureFactory expected;



    public MachineArchitectureFromNameTest(String input, MachineArchitectureFactory expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        //MachineArchitectureFactory arch = MachineArchitectureFactory.fromName(input);
        //assertEquals(expected, arch);
    }
}
