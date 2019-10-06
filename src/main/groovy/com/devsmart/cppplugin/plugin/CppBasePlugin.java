package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.Usage;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.transform.UnzipTransform;
import org.gradle.api.model.ObjectFactory;

import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.DIRECTORY_TYPE;
import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.ZIP_TYPE;

public class CppBasePlugin implements Plugin<Project> {


    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(NativeBasePlugin.class);

        ConfigurationContainer configurations = project.getConfigurations();
        ObjectFactory objectFactory = project.getObjects();

        Configuration implementationConfiguration = configurations.getByName("implementation");

        Configuration compileConfiguration = configurations.create("cppCompile");
        compileConfiguration.setCanBeResolved(false);
        compileConfiguration.setCanBeConsumed(false);
        compileConfiguration.getAttributes().attribute(org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE, objectFactory.named(org.gradle.api.attributes.Usage.class, org.gradle.api.attributes.Usage.C_PLUS_PLUS_API));
        compileConfiguration.extendsFrom(implementationConfiguration);

        // Add incoming artifact transforms
        final DependencyHandler dependencyHandler = project.getDependencies();
        final ObjectFactory objects = project.getObjects();

        addHeaderZipTransform(dependencyHandler, objects);
    }

    private void addHeaderZipTransform(DependencyHandler dependencyHandler, ObjectFactory objects) {
        dependencyHandler.registerTransform(UnzipTransform.class, variantTransform -> {
            variantTransform.getFrom().attribute(ArtifactAttributes.ARTIFACT_FORMAT, ZIP_TYPE);
            variantTransform.getFrom().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.COMPILE));
            variantTransform.getTo().attribute(ArtifactAttributes.ARTIFACT_FORMAT, DIRECTORY_TYPE);
            variantTransform.getTo().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.COMPILE));
        });
    }

}
