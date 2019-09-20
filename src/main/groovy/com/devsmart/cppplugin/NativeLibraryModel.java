package com.devsmart.cppplugin;

import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.util.PatternSet;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class NativeLibraryModel implements SoftwareComponent {

    private final ObjectFactory objectFactory;
    private final String name;
    private final Property<String> baseName;
    private final ConfigurableFileCollection source;
    private final FileCollection cppSource;
    private final ConfigurableFileCollection publicHeaders;
    private final FileCollection privateHeadersWithConvention;
    private final FileCollection publicHeadersWithConvention;
    private final Property<CppStandard> cppStandard;
    private final SetProperty<NativeBinary> binaries;



    @Inject
    public NativeLibraryModel(ObjectFactory objectFactory, String name) {
        this.objectFactory = objectFactory;
        this.name = name;
        this.baseName = objectFactory.property(String.class);
        this.source = objectFactory.fileCollection();
        this.cppSource = createSourceView("src/" + name + "/cpp", Arrays.asList("cpp", "cc"));
        this.privateHeadersWithConvention = createDirView(source, "src/" + name + "/cpp");
        this.publicHeaders = objectFactory.fileCollection();
        this.publicHeadersWithConvention = createDirView(publicHeaders, "src/" + name + "/public");
        this.cppStandard = objectFactory.property(CppStandard.class);
        this.cppStandard.convention(objectFactory.named(CppStandard.class, CppStandard.CPP98));
        this.binaries = objectFactory.setProperty(NativeBinary.class);
    }

    protected FileCollection createSourceView(final String defaultLocation, List<String> sourceExtensions) {
        final PatternSet patternSet = new PatternSet();
        Iterator it = sourceExtensions.iterator();

        while(it.hasNext()) {
            String sourceExtension = (String)it.next();
            patternSet.include("**/*." + sourceExtension);
        }

        return this.getProjectLayout().files((Callable<Object>) () -> {
            FileTree tree;
            if (NativeLibraryModel.this.source.getFrom().isEmpty()) {
                tree = NativeLibraryModel.this.getProjectLayout().getProjectDirectory().dir(defaultLocation).getAsFileTree();
            } else {
                tree = NativeLibraryModel.this.source.getAsFileTree();
            }

            return tree.matching(patternSet);
        });
    }

    protected FileCollection createDirView(final ConfigurableFileCollection dirs, final String conventionLocation) {
        return this.getProjectLayout().files((Callable<Object>) () -> {
            return dirs.getFrom().isEmpty() ? NativeLibraryModel.this.getProjectLayout().getProjectDirectory().dir(conventionLocation) : dirs;
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

    public FileCollection getIncludeDirs() {
        return getProjectLayout().files(privateHeadersWithConvention, publicHeadersWithConvention);
    }

    public Property<CppStandard> getCppStandard() {
        return this.cppStandard;
    }

    public void cppStandard(String standard) {
        this.cppStandard.set( objectFactory.named(CppStandard.class, standard) );
    }

    public SetProperty<NativeBinary> getBinaries() {
        return this.binaries;
    }
}
