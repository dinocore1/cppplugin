package com.devsmart.cppplugin;

import org.gradle.internal.impldep.com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class FileHashTest {

    @Test
    public void ensureSetOfStringsHashes() {
        Set<String> seta = ImmutableSet.of("a", "b", "c");
        Set<String> setb = ImmutableSet.of("a", "b", "c");

        int setahash = Objects.hash(seta, "one");
        int setbhash = Objects.hash(setb, "one");


        assertEquals(setahash, setbhash);
    }


}
