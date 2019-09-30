package com.devsmart.cppplugin;

import com.devsmart.cppplugin.plugin.NativeBasePlugin;
import com.google.common.collect.Sets;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.provider.Provider;
import org.gradle.language.cpp.internal.DefaultUsageContext;

import javax.inject.Inject;
import java.util.Set;

public class HeaderModule implements SoftwareComponentInternal {

    private final Names names;
    private final Provider<String> baseName;
    private final Configuration compileConfiguration;
    private final ConfigurableFileCollection headerFiles;

    @Inject
    public HeaderModule(Names names, Provider<String> baseName, String languageName, ConfigurationContainer configurations, ObjectFactory objectFactory) {
        this.names = names;
        this.baseName = baseName;

        final String parentConfigName = languageName + "Compile";
        this.compileConfiguration = configurations.create(names.withPrefix(parentConfigName));
        this.compileConfiguration.extendsFrom(configurations.getByName(parentConfigName));
        this.compileConfiguration.setCanBeResolved(true);
        this.compileConfiguration.setCanBeConsumed(true);

        this.headerFiles = objectFactory.fileCollection();
    }

    public Names getNames() {
        return this.names;
    }

    @Override
    public String getName() {
        return baseName.get();
    }

    @Override
    public Set<? extends UsageContext> getUsages() {
        return Sets.newHashSet(
                new DefaultUsageContext(compileConfiguration.getName(), compileConfiguration.getAttributes(), compileConfiguration.getAllArtifacts(), compileConfiguration)
        );
    }

    public Configuration getCompileConfiguration() {
        return compileConfiguration;
    }

    public ConfigurableFileCollection getHeaderFiles() {
        return headerFiles;
    }


}
