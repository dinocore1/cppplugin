package com.devsmart.cppplugin;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Usage;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;

public class AbstractNativeModuleWithLinkDependencies extends AbstractNativeModuleWithCompileDependencies {

    private final Configuration linkConfiguration;

    public AbstractNativeModuleWithLinkDependencies(String name, Names names, VariantIdentity variant, FileCollection sourceFiles, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        super(name, names, variant, sourceFiles, componentIncludeDirs, componentImplementation);

        ObjectFactory objectFactory = getObjectFactory();

        this.linkConfiguration = getConfigurationContainer().create(names.withPrefix("link"));
        this.linkConfiguration.setCanBeConsumed(false);
        this.linkConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.NATIVE_LINK));
        this.linkConfiguration.getAttributes().attribute(OperatingSystem.OPERATING_SYSTEM_ATTRIBUTE, variant.getPlatform().getOperatingSystem());
        this.linkConfiguration.getAttributes().attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, variant.getPlatform().getMachineArchitecture());
        this.linkConfiguration.extendsFrom(getImplementationDependencies());


    }

    public FileCollection getLinkLibraries() {
        return linkConfiguration;
    }
}
