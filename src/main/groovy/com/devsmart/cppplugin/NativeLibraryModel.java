package com.devsmart.cppplugin;

import com.devsmart.cppplugin.components.NativeBinary;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class NativeLibraryModel implements SoftwareComponent {

    private final String name;
    private final Property<String> baseName;
    private final ConfigurableFileCollection source;
    private final SetProperty<NativeBinary> binaries;


    public NativeLibraryModel(String name, ObjectFactory objectFactory) {
        this.name = name;
        this.baseName = objectFactory.property(String.class);
        this.source = objectFactory.fileCollection();
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

    public ConfigurableFileCollection getSource() {
        return source;
    }

    public SetProperty<NativeBinary> getBinaries() {
        return this.binaries;
    }
}
