package com.devsmart.cppplugin;

import com.google.common.collect.Sets;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ArtifactView;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.language.ComponentDependencies;
import org.gradle.language.cpp.internal.DefaultUsageContext;
import org.gradle.language.internal.DefaultComponentDependencies;

import javax.inject.Inject;
import java.util.Set;

public abstract class AbstractNativeModuleWithCompileDependencies implements SoftwareComponentInternal {


    private final String name;
    private final Names names;
    private final VariantIdentity variant;
    private final FileCollection sourceFiles;
    private final Configuration implementationConfiguration;
    private final Configuration compileConfiguration;
    private final FileCollection includeDirs;
    private final Property<Task> compileTask;

    public AbstractNativeModuleWithCompileDependencies(String name, Names names, VariantIdentity variant, FileCollection sourceFiles, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        this.name = name;
        this.names = names;
        this.variant = variant;
        this.sourceFiles = sourceFiles;
        this.implementationConfiguration = componentImplementation;

        ObjectFactory objectFactory = getObjectFactory();

        this.compileConfiguration = getConfigurationContainer().create(names.withPrefix("compile"));
        this.compileConfiguration.setCanBeConsumed(false);
        this.compileConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.C_PLUS_PLUS_API));
        this.compileConfiguration.getAttributes().attribute(OperatingSystem.OPERATING_SYSTEM_ATTRIBUTE, this.variant.getPlatform().getOperatingSystem());
        this.compileConfiguration.getAttributes().attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, this.variant.getPlatform().getMachineArchitecture());
        this.compileConfiguration.extendsFrom(implementationConfiguration);

        ArtifactView includeDirs = compileConfiguration.getIncoming().artifactView(viewConfiguration -> {
            viewConfiguration.attributes(attributeContainer -> {
                attributeContainer.attribute(ArtifactAttributes.ARTIFACT_FORMAT, ArtifactTypeDefinition.DIRECTORY_TYPE);
            });
        });

        this.includeDirs = componentIncludeDirs.plus(includeDirs.getFiles());

        this.compileTask = objectFactory.property(Task.class);
    }

    @Inject
    protected ConfigurationContainer getConfigurationContainer() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected ObjectFactory getObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected ProjectLayout getProjectLayout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return name;
    }

    public Names getNames() {
        return this.names;
    }

    public VariantIdentity getVariant() {
        return this.variant;
    }

    public FileCollection getSourceFiles() {
        return this.sourceFiles;
    }

    public Configuration getImplementationDependencies() {
        return implementationConfiguration;
    }

    public FileCollection getIncludeDirs() {
        return this.includeDirs;
    }

    public Property<Task> getCompileTask() {
        return this.compileTask;
    }

    @Override
    public Set<? extends UsageContext> getUsages() {
        return Sets.newHashSet(
                new DefaultUsageContext(compileConfiguration.getName(), compileConfiguration.getAttributes(), compileConfiguration.getAllArtifacts(), compileConfiguration)
        );
    }
}
