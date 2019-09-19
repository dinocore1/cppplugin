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
                { "linux", OperatingSystem.LINUX },
                { "windows", OperatingSystem.WINDOWS }
        });
    }

    private final String input;
    private final OperatingSystem expected;

    public OperatingSystemFromNameTest(String input, OperatingSystem expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        OperatingSystem os = OperatingSystem.fromName(input);
        assertEquals(expected, os);
    }
}
