package com.devsmart.cppplugin;

import com.google.common.collect.Sets;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Usage;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.language.cpp.internal.DefaultUsageContext;

import java.util.Set;

public abstract class AbstractNativeModuleWithLinkDependencies extends AbstractNativeModuleWithCompileDependencies {

    private final Configuration linkConfiguration;
    private final Property<Task> linkTask;

    public AbstractNativeModuleWithLinkDependencies(String name, Names names, VariantIdentity variant, FileCollection sourceFiles, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        super(name, names, variant, sourceFiles, componentIncludeDirs, componentImplementation);

        ObjectFactory objectFactory = getObjectFactory();

        linkTask = objectFactory.property(Task.class);

        this.linkConfiguration = getConfigurationContainer().create(names.withPrefix("link"));
        this.linkConfiguration.setCanBeConsumed(false);
        this.linkConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.NATIVE_LINK));
        this.linkConfiguration.getAttributes().attribute(OperatingSystem.OPERATING_SYSTEM_ATTRIBUTE, variant.getPlatform().getOperatingSystem());
        this.linkConfiguration.getAttributes().attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, variant.getPlatform().getMachineArchitecture());
        this.linkConfiguration.extendsFrom(getImplementationDependencies());


    }

    public Configuration getLinkConfiguration() {
        return linkConfiguration;
    }

    public Property<Task> getLinkTask() {
        return this.linkTask;
    }

    @Override
    public Set<? extends UsageContext> getUsages() {
        Set usageContexts = super.getUsages();
        DefaultUsageContext linkUsage = new DefaultUsageContext(linkConfiguration.getName(), linkConfiguration.getAttributes(), linkConfiguration.getAllArtifacts(), linkConfiguration);
        usageContexts.add(linkUsage);
        return usageContexts;
    }
}
