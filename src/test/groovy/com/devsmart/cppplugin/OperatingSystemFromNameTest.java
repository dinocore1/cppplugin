package com.devsmart.cppplugin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OperatingSystemFromNameTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "linux", DefaultOperatingSystem.LINUX },
                { "windows", DefaultOperatingSystem.WINDOWS }
        });
    }

    private final String input;
    private final DefaultOperatingSystem expected;

    public OperatingSystemFromNameTest(String input, DefaultOperatingSystem expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        DefaultOperatingSystem os = DefaultOperatingSystem.fromName(input);
        assertEquals(expected, os);
    }
}
