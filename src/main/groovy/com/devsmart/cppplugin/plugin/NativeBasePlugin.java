package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.components.ConfigurableComponentWithLinkUsage;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.attributes.*;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.transform.UnzipTransform;
import org.gradle.api.model.ObjectFactory;
import org.gradle.internal.Cast;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.language.nativeplatform.internal.Names;
import org.gradle.nativeplatform.Linkage;

import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.DIRECTORY_TYPE;
import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.ZIP_TYPE;
import static org.gradle.language.cpp.CppBinary.LINKAGE_ATTRIBUTE;

public class NativeBasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(LifecycleBasePlugin.class);

        final SoftwareComponentContainer components = project.getComponents();


        // Add incoming artifact transforms
        final DependencyHandler dependencyHandler = project.getDependencies();
        final ObjectFactory objects = project.getObjects();

        addHeaderZipTransform(dependencyHandler, objects);

        // Add outgoing configurations and publications
        final ConfigurationContainer configurations = project.getConfigurations();

        project.getDependencies().getAttributesSchema().attribute(LINKAGE_ATTRIBUTE).getDisambiguationRules().add(LinkageSelectionRule.class);

        addOutgoingConfigurationForLinkUsage(components, configurations);

    }

    private void addHeaderZipTransform(DependencyHandler dependencyHandler, ObjectFactory objects) {
        dependencyHandler.registerTransform(UnzipTransform.class, variantTransform -> {
            variantTransform.getFrom().attribute(ArtifactAttributes.ARTIFACT_FORMAT, ZIP_TYPE);
            variantTransform.getFrom().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.C_PLUS_PLUS_API));
            variantTransform.getTo().attribute(ArtifactAttributes.ARTIFACT_FORMAT, DIRECTORY_TYPE);
            variantTransform.getTo().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.C_PLUS_PLUS_API));
        });
    }

    static class LinkageSelectionRule implements AttributeDisambiguationRule<Linkage> {
        @Override
        public void execute(MultipleCandidatesDetails<Linkage> details) {
            if (details.getCandidateValues().contains(Linkage.SHARED)) {
                details.closestMatch(Linkage.SHARED);
            }
        }
    }

    private void addOutgoingConfigurationForLinkUsage(SoftwareComponentContainer components, final ConfigurationContainer configurations) {
        components.withType(ConfigurableComponentWithLinkUsage.class, component -> {
            //Names names = component.getNames();
            //Configuration linkElements = configurations.create(names.withSuffix("linkElements"));
            Configuration linkElements = configurations.create("mainLinkElements");
            linkElements.extendsFrom(component.getImplementationDependencies());
            linkElements.setCanBeResolved(false);
            AttributeContainer attributes = component.getLinkAttributes();
            copyAttributesTo(attributes, linkElements);

            linkElements.getOutgoing().artifact(component.getLinkFile());

            component.getLinkElements().set(linkElements);
        });
    }

    private void copyAttributesTo(AttributeContainer attributes, Configuration linkElements) {
        for (Attribute<?> attribute : attributes.keySet()) {
            Object value = attributes.getAttribute(attribute);
            linkElements.getAttributes().attribute(Cast.<Attribute<Object>>uncheckedCast(attribute), value);
        }
    }
}
