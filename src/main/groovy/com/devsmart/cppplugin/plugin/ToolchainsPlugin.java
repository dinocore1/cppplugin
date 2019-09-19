package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.Toolchains;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ToolchainsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        Toolchains toolchains = project.getObjects().newInstance(Toolchains.class);
        project.getExtensions().add("toolchains", toolchains);

    }
}
