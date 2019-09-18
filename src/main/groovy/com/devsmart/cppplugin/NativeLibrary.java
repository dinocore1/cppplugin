package com.devsmart.cppplugin;

import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.util.PatternSet;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class NativeLibrary implements SoftwareComponent {

    private final String name;
    private final Property<String> baseName;
    private final ConfigurableFileCollection source;
    private final FileCollection cppSource;
    private final ConfigurableFileCollection publicHeaders;
    private final FileCollection publicHeadersWithConvention;
    private final ListProperty<CppCompiler> cppCompilers;

    @Inject
    public NativeLibrary(ProjectLayout layout, ObjectFactory objectFactory, String name) {
        this.name = name;
        this.baseName = objectFactory.property(String.class);
        this.source = objectFactory.fileCollection();
        this.cppSource = createSourceView("src/" + name + "/cpp", Arrays.asList("cpp", "cc"));
        this.publicHeaders = objectFactory.fileCollection();
        this.publicHeadersWithConvention = createDirView(publicHeaders, "src/" + name + "/public");
        this.cppCompilers = objectFactory.listProperty(CppCompiler.class);
    }

    protected FileCollection createSourceView(final String defaultLocation, List<String> sourceExtensions) {
        final PatternSet patternSet = new PatternSet();
        Iterator it = sourceExtensions.iterator();

        while(it.hasNext()) {
            String sourceExtension = (String)it.next();
            patternSet.include("**/*." + sourceExtension);
        }

        return this.getProjectLayout().files(new Callable<Object>() {
            public Object call() {
                FileTree tree;
                if (NativeLibrary.this.source.getFrom().isEmpty()) {
                    tree = NativeLibrary.this.getProjectLayout().getProjectDirectory().dir(defaultLocation).getAsFileTree();
                } else {
                    tree = NativeLibrary.this.source.getAsFileTree();
                }

                return tree.matching(patternSet);
            }
        });
    }

    protected FileCollection createDirView(final ConfigurableFileCollection dirs, final String conventionLocation) {
        return this.getProjectLayout().files((Callable<Object>) () -> {
            return dirs.getFrom().isEmpty() ? NativeLibrary.this.getProjectLayout().getProjectDirectory().dir(conventionLocation) : dirs;
        });
    }

    @Inject
    protected ProjectLayout getProjectLayout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Property<String> getBaseName() {
        return this.baseName;
    }

    public ConfigurableFileCollection getSource() {
        return source;
    }

    public FileCollection getCppSource() {
        return cppSource;
    }

    public FileCollection getPublicHeaderDirs() {
        return this.publicHeadersWithConvention;
    }

    public FileTree getPublicHeaderFiles() {
        PatternSet patterns = new PatternSet();
        patterns.include("**/*.h", "**/*.hpp");
        return this.publicHeadersWithConvention.getAsFileTree().matching(patterns);
    }

    public ListProperty<CppCompiler> getCppCompilers() {
        return this.cppCompilers;
    }
}
