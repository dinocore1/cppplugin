package com.devsmart.cppplugin;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.util.PatternSet;

import javax.inject.Inject;
import java.util.Arrays;

public class CppLibraryModel extends NativeLibraryModel {

    private final FileCollection cppSource;
    private final FileCollection privateHeadersWithConvention;
    private final ConfigurableFileCollection publicHeaders;
    private final FileCollection publicHeadersWithConvention;
    private final Property<CppStandard> cppStandard;

    @Inject
    public CppLibraryModel(String name, ObjectFactory objectFactory) {
        super(name, objectFactory);
        this.cppSource = createSourceView("src/" + name + "/cpp", Arrays.asList("cpp", "cc"));
        this.privateHeadersWithConvention = createDirView(getSource(), "src/" + name + "/cpp");
        this.publicHeaders = objectFactory.fileCollection();
        this.publicHeadersWithConvention = createDirView(publicHeaders, "src/" + name + "/public");
        this.cppStandard = objectFactory.property(CppStandard.class);
        this.cppStandard.convention(objectFactory.named(CppStandard.class, CppStandard.CPP98));
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
        this.cppStandard.set( getObjectFactory().named(CppStandard.class, standard) );
    }
}
