package com.devsmart.cppplugin.components;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.nativeplatform.Linkage;


public interface NativeComponentWithLinkUsage extends ComponentWithNames, SoftwareComponent {

    Configuration getImplementationDependencies();

    Linkage getLinkage();

    Property<Configuration> getLinkElements();

    Provider<RegularFile> getLinkFile();

    AttributeContainer getLinkAttributes();
}
