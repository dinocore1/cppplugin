package com.devsmart.cppplugin;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
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


    @Inject
    public CppLibrary(String name, ObjectFactory objectFactory, ConfigurationContainer configurations) {
        this.name = name;
        this.baseName = objectFactory.property(String.class);
        this.dependencies = objectFactory.newInstance(DefaultComponentDependencies.class, "implementation");

        this.source = objectFactory.fileCollection();
        this.cppSource = createSourceView(source,"src/" + name + "/cpp", Arrays.asList("cpp", "c++", "cc"));
        this.privateHeaders = objectFactory.fileCollection();
        this.privateHeadersWithConvention = createDirView(privateHeaders, "src/" + name + "/cpp");
        this.publicHeaders = objectFactory.fileCollection();
        this.publicHeadersWithConvention = createDirView(publicHeaders, "src/" + name + "/public");

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

    public StaticLibrary addStaticLibrary(VariantIdentity variant) {

    }



}
