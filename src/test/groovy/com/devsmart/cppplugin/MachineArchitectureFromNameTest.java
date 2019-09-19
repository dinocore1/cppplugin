package com.devsmart.cppplugin;

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
                { "x86", MachineArchitecture.X86 },
                { "i386", MachineArchitecture.X86 },
                { "i686", MachineArchitecture.X86 },
                { "ia-32", MachineArchitecture.X86 },

                { "x86-64", MachineArchitecture.X86_64 },
                { "x86_64", MachineArchitecture.X86_64 },
                { "x64", MachineArchitecture.X86_64 },
                { "amd64", MachineArchitecture.X86_64 },

                { "arm-v7", MachineArchitecture.ARM_V7 },
                { "arm", MachineArchitecture.ARM_V7 },
                { "armv7", MachineArchitecture.ARM_V7 },
                { "arm-v7a", MachineArchitecture.ARM_V7 },
                { "arm32", MachineArchitecture.ARM_V7 },

                { "arm64", MachineArchitecture.ARM_V8 },
                { "arm-v8", MachineArchitecture.ARM_V8 },
        });
    }

    private final String input;
    private final MachineArchitecture expected;

    public MachineArchitectureFromNameTest(String input, MachineArchitecture expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        MachineArchitecture arch = MachineArchitecture.fromName(input);
        assertEquals(expected, arch);
    }
}
