package com.devsmart.cppplugin.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

public class NativeBasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(LifecycleBasePlugin.class);



    }




}
