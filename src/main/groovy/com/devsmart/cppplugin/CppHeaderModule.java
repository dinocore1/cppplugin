package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

public class CppHeaderModule extends HeaderModule {

    private final Provider<Configuration> compileConfiguration;

    @Inject
    public CppHeaderModule(Names names, Provider<String> baseName, Provider<CppStandard> cppStandard, ProviderFactory providerFactory, ConfigurationContainer configurations, ObjectFactory objectFactory) {
        super(names, baseName, "cpp", providerFactory, configurations, objectFactory);

        this.compileConfiguration = super.getCompileConfiguration().map(compileConfig -> {
            compileConfig.getAttributes().attribute(CppStandard.CPPSTANDARD_ATTRIBUTE, cppStandard.get());
            return compileConfig;
        });

    }

    @Override
    public Provider<Configuration> getCompileConfiguration() {
        return compileConfiguration;
    }
}
