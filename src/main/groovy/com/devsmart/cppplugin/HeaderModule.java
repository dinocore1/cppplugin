package com.devsmart.cppplugin;

import com.google.common.collect.Sets;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.language.cpp.internal.DefaultUsageContext;

import javax.inject.Inject;
import java.util.Set;

public class HeaderModule implements SoftwareComponentInternal {

    private final Names names;
    private final Provider<String> baseName;
    private final Provider<Configuration> compileConfiguration;
    private final ConfigurableFileCollection headerFiles;
    private final RegularFileProperty headerZipFile;
    private final Property<Task> zipHeadersTask;

    @Inject
    public HeaderModule(Names names, Provider<String> baseName, String languageName, ProviderFactory providerFactory, ConfigurationContainer configurations, ObjectFactory objectFactory) {
        this.names = names;
        this.baseName = baseName;
        this.headerFiles = objectFactory.fileCollection();
        this.headerZipFile = objectFactory.fileProperty();
        this.zipHeadersTask = objectFactory.property(Task.class);

        final String parentConfigName = languageName + "Compile";
        final Configuration compileConfig = configurations.create(names.withPrefix(parentConfigName));
        compileConfig.extendsFrom(configurations.getByName(parentConfigName));
        compileConfig.setCanBeResolved(true);
        compileConfig.setCanBeConsumed(true);
        compileConfig.getOutgoing().artifact(getHeaderZipFile(), config -> {
            config.builtBy(getZipHeadersTask());
            config.setClassifier("headers");
        });

        this.compileConfiguration = providerFactory.provider(() -> compileConfig);
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
        Configuration compileConfig = getCompileConfiguration().get();
        return Sets.newHashSet(
                new DefaultUsageContext(compileConfig.getName(), compileConfig.getAttributes(), compileConfig.getAllArtifacts(), compileConfig)
        );
    }

    public Provider<Configuration> getCompileConfiguration() {
        return this.compileConfiguration;
    }

    public ConfigurableFileCollection getHeaderFiles() {
        return headerFiles;
    }

    public RegularFileProperty getHeaderZipFile() {
        return this.headerZipFile;
    }

    public Property<Task> getZipHeadersTask() {
        return this.zipHeadersTask;
    }
}
