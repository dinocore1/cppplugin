package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.NativeBinary;
import com.devsmart.cppplugin.NativeLibrary;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import javax.inject.Inject;

public class NativeLibraryPlugin implements Plugin<Project> {

    @Inject
    public NativeLibraryPlugin() {

    }

    @Override
    public void apply(Project project) {

        NativeLibrary lib = project.getObjects().newInstance(com.devsmart.cppplugin.NativeLibrary.class, "main");
        project.getExtensions().add("library", lib);
        project.getComponents().add(lib);
        lib.getBaseName().convention(project.getName());


        project.getComponents().withType(NativeBinary.class, binary -> {

        });

    }
}
