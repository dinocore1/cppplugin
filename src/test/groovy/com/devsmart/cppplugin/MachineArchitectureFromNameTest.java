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
                { "x86", DefaultMachineArchitecture.X86 },
                { "i386", DefaultMachineArchitecture.X86 },
                { "i686", DefaultMachineArchitecture.X86 },
                { "ia-32", DefaultMachineArchitecture.X86 },

                { "x86-64", DefaultMachineArchitecture.X86_64 },
                { "x86_64", DefaultMachineArchitecture.X86_64 },
                { "x64", DefaultMachineArchitecture.X86_64 },
                { "amd64", DefaultMachineArchitecture.X86_64 },

                { "arm-v7", DefaultMachineArchitecture.ARM_V7 },
                { "arm", DefaultMachineArchitecture.ARM_V7 },
                { "armv7", DefaultMachineArchitecture.ARM_V7 },
                { "arm-v7a", DefaultMachineArchitecture.ARM_V7 },
                { "arm32", DefaultMachineArchitecture.ARM_V7 },

                { "arm64", DefaultMachineArchitecture.ARM_V8 },
                { "arm-v8", DefaultMachineArchitecture.ARM_V8 },
        });
    }

    private final String input;
    private final DefaultMachineArchitecture expected;

    public MachineArchitectureFromNameTest(String input, DefaultMachineArchitecture expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        DefaultMachineArchitecture arch = DefaultMachineArchitecture.fromName(input);
        assertEquals(expected, arch);
    }
}
