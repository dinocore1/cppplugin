package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;

public abstract class Platform implements Named {

    public static Attribute<Platform> PLATFORM_ATTRIBUTE = Attribute.of(Platform.class);

    public static final String WINDOWS = "windows";
    public static final String LINUX = "linux";
    public static final String ANDROID = "android";

    @Input
    public abstract String getName();
}
