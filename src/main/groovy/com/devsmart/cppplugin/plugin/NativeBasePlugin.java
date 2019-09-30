package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.Usage;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

public class NativeBasePlugin implements Plugin<Project> {

    private Configuration implementationConfiguration;
    private Configuration compileConfiguration;
    private Configuration linkConfiguration;
    private Configuration runConfiguration;

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(LifecycleBasePlugin.class);

        this.implementationConfiguration = project.getConfigurations().create("implementation");
        this.implementationConfiguration.setCanBeResolved(false);
        this.implementationConfiguration.setCanBeConsumed(false);

        this.compileConfiguration = project.getConfigurations().create("compile");
        this.compileConfiguration.extendsFrom(implementationConfiguration);
        this.compileConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.COMPILE));
        this.compileConfiguration.setCanBeResolved(false);
        this.compileConfiguration.setCanBeConsumed(false);

        this.linkConfiguration = project.getConfigurations().create("link");
        this.linkConfiguration.extendsFrom(implementationConfiguration);
        this.compileConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.LINK));
        this.linkConfiguration.setCanBeResolved(false);
        this.linkConfiguration.setCanBeConsumed(false);

        this.runConfiguration = project.getConfigurations().create("run");
        this.runConfiguration.extendsFrom(implementationConfiguration);
        this.compileConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.RUN));
        this.runConfiguration.setCanBeResolved(false);
        this.runConfiguration.setCanBeConsumed(false);

    }

    public Configuration getImplementationConfiguration() {
        return implementationConfiguration;
    }

    public Configuration getCompileConfiguration() {
        return compileConfiguration;
    }

    public Configuration getLinkConfiguration() {
        return linkConfiguration;
    }

    public Configuration getRunConfiguration() {
        return runConfiguration;
    }
}
