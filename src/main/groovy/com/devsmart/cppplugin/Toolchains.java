package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class Toolchains {

    private final SoftwareComponentContainer components;
    private final ObjectFactory objectFactory;

    @Inject
    public Toolchains(SoftwareComponentContainer components, ProjectLayout layout, ObjectFactory objectFactory) {
        this.components = components;
        this.objectFactory = objectFactory;

    }

    public void gcc(Action<? super ToolChain> config) {
        DefaultGccToolchain toolchain = objectFactory.newInstance(DefaultGccToolchain.class);
        config.execute(toolchain);
        components.add(toolchain);
    }
}
