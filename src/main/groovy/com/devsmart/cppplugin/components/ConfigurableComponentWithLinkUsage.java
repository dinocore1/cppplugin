package com.devsmart.cppplugin.components;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

public interface ConfigurableComponentWithLinkUsage extends SoftwareComponent {

    Configuration getImplementationDependencies();

    Property<Configuration> getLinkElements();

    Provider<RegularFile> getLinkFile();

    AttributeContainer getLinkAttributes();


}
