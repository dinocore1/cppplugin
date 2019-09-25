package com.devsmart.cppplugin;

import com.devsmart.cppplugin.components.ComponentWithNames;
import com.devsmart.cppplugin.components.NativeComponent;
import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.model.ObjectFactory;
import org.gradle.language.ComponentDependencies;
import org.gradle.language.ComponentWithDependencies;
import org.gradle.language.internal.DefaultComponentDependencies;

public abstract class AbstractNativeModule implements NativeComponent, ComponentWithNames, ComponentWithDependencies {


    private final String name;
    private final Names names;
    private final VariantIdentity variant;
    private final DefaultComponentDependencies dependencies;

    public AbstractNativeModule(String name, Names names, VariantIdentity variantIdentity, ObjectFactory objectFactory, Configuration componentImplementation) {
        this.name = name;
        this.names = names;
        this.variant = variantIdentity;

        dependencies = objectFactory.newInstance(DefaultComponentDependencies.class, name + "Implementation");
        dependencies.getImplementationDependencies().extendsFrom(componentImplementation);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Names getNames() {
        return this.names;
    }

    @Override
    public VariantIdentity getVariant() {
        return this.variant;
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
