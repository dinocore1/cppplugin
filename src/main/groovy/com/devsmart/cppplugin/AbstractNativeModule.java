package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.model.ObjectFactory;
import org.gradle.language.ComponentDependencies;
import org.gradle.language.ComponentWithDependencies;
import org.gradle.language.internal.DefaultComponentDependencies;

public abstract class AbstractNativeModule implements ComponentWithDependencies {


    private String name;
    private final DefaultComponentDependencies dependencies;

    public AbstractNativeModule(String name, ObjectFactory objectFactory, Configuration componentImplementation) {
        this.name = name;

        dependencies = objectFactory.newInstance(DefaultComponentDependencies.class, name + "Implementation");
        dependencies.getImplementationDependencies().extendsFrom(componentImplementation);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ComponentDependencies getDependencies() {
        return dependencies;
    }

    public void dependencies(Action<? super ComponentDependencies> action) {
        action.execute(getDependencies());
    }

    public Configuration getImplementationDependencies() {
        return dependencies.getImplementationDependencies();
    }
}
