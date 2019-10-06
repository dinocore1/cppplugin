package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.language.internal.DefaultComponentDependencies;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class CppLibrary implements SoftwareComponent {

    private final String name;
    private final Property<String> baseName;
    private final DefaultComponentDependencies dependencies;
    private final ConfigurableFileCollection source;
    private final FileCollection cppSource;
    private final ConfigurableFileCollection privateHeaders;
    private final FileCollection privateHeadersWithConvention;
    private final ConfigurableFileCollection publicHeaders;
    private final FileCollection publicHeadersWithConvention;
    private final Property<CppStandard> cppStandard;


    @Inject
    public CppLibrary(String name, ObjectFactory objectFactory, ConfigurationContainer configurations) {
        this.name = name;
        this.baseName = objectFactory.property(String.class);
        this.dependencies = objectFactory.newInstance(DefaultComponentDependencies.class, name + "Implementation");
        Configuration implementationConfiguration = configurations.getByName("implementation");
        this.dependencies.getImplementationDependencies().extendsFrom(implementationConfiguration);
        this.cppStandard = objectFactory.property(CppStandard.class).convention(objectFactory.named(CppStandard.class, CppStandard.CPP98));
        this.source = objectFactory.fileCollection();
        this.cppSource = createSourceView(source,"src/" + name + "/cpp", Arrays.asList("cpp", "c++", "cc"));
        this.privateHeaders = objectFactory.fileCollection();
        this.privateHeadersWithConvention = createDirView(privateHeaders, "src/" + name + "/cpp");
        this.publicHeaders = objectFactory.fileCollection();
        this.publicHeadersWithConvention = createDirView(publicHeaders, "src/" + name + "/public");




        Configuration compileConfiguration = configurations.create("cppCompile");
        compileConfiguration.setCanBeResolved(true);
        compileConfiguration.setCanBeConsumed(true);
        compileConfiguration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.C_PLUS_PLUS_API));
        compileConfiguration.extendsFrom(dependencies.getImplementationDependencies());
        //compileConfiguration.getOutgoing().artifact(headerZip);

    }

    protected FileCollection createSourceView(final ConfigurableFileCollection dirs, final String defaultLocation, List<String> sourceExtensions) {
        final PatternSet patternSet = new PatternSet();
        for (String sourceExtension : sourceExtensions) {
            patternSet.include("**/*." + sourceExtension);
        }
        return getProjectLayout().files(new Callable<Object>() {
            @Override
            public Object call() {
                FileTree tree;
                if (dirs.getFrom().isEmpty()) {
                    tree = getProjectLayout().getProjectDirectory().dir(defaultLocation).getAsFileTree();
                } else {
                    tree = dirs.getAsFileTree();
                }
                return tree.matching(patternSet);
            }
        });
    }

    protected FileCollection createDirView(final ConfigurableFileCollection dirs, final String conventionLocation) {
        return getProjectLayout().files(new Callable<Object>() {
            @Override
            public Object call() {
                if (dirs.getFrom().isEmpty()) {
                    return getProjectLayout().getProjectDirectory().dir(conventionLocation);
                }
                return dirs;
            }
        });
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

    public ConfigurableFileCollection getSource() {
        return this.source;
    }

    public FileCollection getAllIncludeDirs() {
        return getProjectLayout().files(privateHeadersWithConvention, publicHeadersWithConvention);
    }

    public void cppStandard(String standard) {
        this.cppStandard.set(getObjectFactory().named(CppStandard.class, standard));
    }

    public Property<CppStandard> getCppStandard() {
        return this.cppStandard;
    }

    public HeaderModule addHeaderModule() {
        Names names = Names.of(this);
        HeaderModule headerModule = getObjectFactory().newInstance(CppHeaderModule.class, names, baseName, cppStandard);
        headerModule.getHeaderFiles().setFrom(publicHeadersWithConvention);

        return headerModule;
    }

    public DefaultStaticLibrary addStaticLibrary(VariantIdentity variant) {
        Names names = Names.of(variant);
        DefaultStaticLibrary staticLibrary = getObjectFactory().newInstance(DefaultStaticLibrary.class, name, names, variant, cppSource, getAllIncludeDirs(), dependencies.getImplementationDependencies());
        return staticLibrary;
    }

    public SharedLibrary addSharedLibrary(VariantIdentity variant) {
        Names names = Names.of(variant);
        SharedLibrary sharedLibrary = getObjectFactory().newInstance(SharedLibrary.class, name, names, variant, cppSource, getAllIncludeDirs(), dependencies.getImplementationDependencies());
        return sharedLibrary;
    }
}
