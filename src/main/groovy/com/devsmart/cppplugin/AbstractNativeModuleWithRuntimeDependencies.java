package com.devsmart.cppplugin;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;

public class AbstractNativeModuleWithRuntimeDependencies extends AbstractNativeModuleWithLinkDependencies {

    private final Configuration runtimeConfiguration;

    public AbstractNativeModuleWithRuntimeDependencies(String name, Names names, VariantIdentity variant, FileCollection sourceFiles, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        super(name, names, variant, sourceFiles, componentIncludeDirs, componentImplementation);

        ObjectFactory objectFactory = getObjectFactory();

        this.runtimeConfiguration = getConfigurationContainer().create(names.withPrefix("runtime"));
        this.runtimeConfiguration.setCanBeConsumed(false);
        this.runtimeConfiguration.getAttributes().attribute(OperatingSystem.OPERATING_SYSTEM_ATTRIBUTE, variant.getPlatform().getOperatingSystem());
        this.runtimeConfiguration.getAttributes().attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, variant.getPlatform().getMachineArchitecture());
        this.runtimeConfiguration.extendsFrom(getImplementationDependencies());

    }

    public FileCollection getRuntimeLibraries() {
        return runtimeConfiguration;
    }
}
