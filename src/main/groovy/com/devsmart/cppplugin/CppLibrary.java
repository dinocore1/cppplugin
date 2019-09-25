package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.internal.DefaultComponentDependencies;
import org.gradle.nativeplatform.Linkage;
import org.gradle.nativeplatform.MachineArchitecture;
import org.gradle.nativeplatform.OperatingSystemFamily;

import javax.inject.Inject;

public class CppLibrary implements SoftwareComponent {

    private final String name;
    private final Property<String> baseName;
    private final DefaultComponentDependencies dependencies;
    private final Configuration nativeLink;


    @Inject
    public CppLibrary(String name, ObjectFactory objectFactory, ConfigurationContainer configurations) {
        this.name = name;
        this.baseName = objectFactory.property(String.class);
        this.dependencies = objectFactory.newInstance(DefaultComponentDependencies.class, "implementation");

        this.nativeLink = configurations.create("mainNativeLink");
        nativeLink.setCanBeConsumed(false);
        nativeLink.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.NATIVE_LINK));
        //nativeLink.getAttributes().attribute(DEBUGGABLE_ATTRIBUTE, identity.isDebuggable());
        nativeLink.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false);
        nativeLink.getAttributes().attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objectFactory.named(OperatingSystemFamily.class, "linux"));
        nativeLink.getAttributes().attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objectFactory.named(MachineArchitecture.class, "x86-64"));
        nativeLink.getAttributes().attribute(CppBinary.LINKAGE_ATTRIBUTE, Linkage.SHARED);
        nativeLink.extendsFrom(dependencies.getImplementationDependencies());
    }

    @Inject
    protected ProjectLayout getProjectLayout() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected ObjectFactory getObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Property<String> getBaseName() {
        return this.baseName;
    }

    public DefaultComponentDependencies getDependencies() {
        return this.dependencies;
    }

    public void dependencies(Action<? super DefaultComponentDependencies> action) {
        action.execute(dependencies);
    }

    public FileCollection getLinkLibraries() {
        return nativeLink;
    }
}
