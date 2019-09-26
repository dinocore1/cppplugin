package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ArtifactView;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.model.ObjectFactory;
import org.gradle.language.ComponentDependencies;
import org.gradle.language.internal.DefaultComponentDependencies;

import javax.inject.Inject;

public abstract class AbstractNativeModule implements SoftwareComponent {


    private final String name;
    private final Names names;
    private final VariantIdentity variant;
    private final DefaultComponentDependencies dependencies;
    private final Configuration compileConfiguration;
    private final FileCollection includeDirs;

    public AbstractNativeModule(String name, Names names, VariantIdentity variant, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        this.name = name;
        this.names = names;
        this.variant = variant;

        ObjectFactory objectFactory = getObjectFactory();

        this.dependencies = objectFactory.newInstance(DefaultComponentDependencies.class, name + "Implementation");
        this.dependencies.getImplementationDependencies().extendsFrom(componentImplementation);

        this.compileConfiguration = getConfigurationContainer().create(names.withPrefix("compile"));
        this.compileConfiguration.setCanBeConsumed(false);
        this.compileConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.C_PLUS_PLUS_API));
        //this.compileConfiguration.getAttributes().attribute(OperatingSystem.OPERATING_SYSTEM_ATTRIBUTE, this.variant.getPlatform().getOperatingSystem());
        //this.compileConfiguration.getAttributes().attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, this.variant.getPlatform().getMachineArchitecture());
        this.compileConfiguration.extendsFrom(getImplementationDependencies());

        ArtifactView includeDirs = compileConfiguration.getIncoming().artifactView(viewConfiguration -> {
            viewConfiguration.attributes(attributeContainer -> {
                attributeContainer.attribute(ArtifactAttributes.ARTIFACT_FORMAT, ArtifactTypeDefinition.DIRECTORY_TYPE);
            });
        });

        this.includeDirs = componentIncludeDirs.plus(includeDirs.getFiles());
    }

    @Inject
    protected ConfigurationContainer getConfigurationContainer() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected ObjectFactory getObjectFactory() {
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

    public ComponentDependencies getDependencies() {
        return dependencies;
    }

    public void dependencies(Action<? super ComponentDependencies> action) {
        action.execute(getDependencies());
    }

    public Configuration getImplementationDependencies() {
        return dependencies.getImplementationDependencies();
    }

    public FileCollection getIncludeDirs() {
        return this.includeDirs;
    }
}
