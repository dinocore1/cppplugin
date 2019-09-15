package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;

public abstract class Variant implements Named {

    public static final Attribute<Variant> VARIANT_ATTRIBUTE = Attribute.of(Variant.class);
    public static final String DEBUG = "debug";
    public static final String RELEASE = "release";

    @Input
    public abstract String getName();
}
