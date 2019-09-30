package com.devsmart.cppplugin;

import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;

public abstract class AbstractNativeModule implements SoftwareComponentInternal {

    private final Names names;
    private final Provider<String> baseName;
    private final Property<Configuration> compiletimeConfig;
    private final Property<Task> compileTask;
    private final Property<Configuration> linktimeConfig;
    private final Property<Task> linkTask;
    private final Property<Configuration> runtimeConfig;


    @Inject
    public AbstractNativeModule(Names names, Provider<String> baseName, ConfigurationContainer configurations, ObjectFactory objectFactory) {
        this.names = names;
        this.baseName = baseName;

        configurations.create(names.withSuffix("link"));

        this.compiletimeConfig = objectFactory.property(Configuration.class);
        this.compileTask = objectFactory.property(Task.class);
        this.linktimeConfig = objectFactory.property(Configuration.class);
        this.linkTask = objectFactory.property(Task.class);
        this.runtimeConfig = objectFactory.property(Configuration.class);
    }

    @Override
    public String getName() {
        return baseName.get();
    }

    public Names getNames() {
        return this.names;
    }

    public Property<Configuration> getCompiletimeConfig() {
        return compiletimeConfig;
    }

    public Property<Task> getCompileTask() {
        return compileTask;
    }

    public Property<Configuration> getLinktimeConfig() {
        return linktimeConfig;
    }

    public Property<Task> getLinkTask() {
        return linkTask;
    }

    public Property<Configuration> getRuntimeConfig() {
        return runtimeConfig;
    }
}
