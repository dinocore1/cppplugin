package com.devsmart.cppplugin;

import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.util.PatternSet;

import javax.inject.Inject;

public class StaticLibrary extends AbstractNativeModuleWithLinkDependencies {


    private final Property<Task> compileTask;

    @Inject
    public StaticLibrary(String name, Names names, VariantIdentity variant, FileCollection sourceFiles, FileCollection componentIncludeDirs, Configuration componentImplementation) {
        super(name, names, variant, sourceFiles, componentIncludeDirs, componentImplementation);

        ObjectFactory objects = getObjectFactory();
        this.compileTask = objects.property(Task.class);
    }

    public FileCollection getCppSource() {
        PatternSet patternSet = new PatternSet();
        patternSet.include("**/*.cpp");
        patternSet.include("**/*.c++");
        patternSet.include("**/*.cc");
        return getSourceFiles().getAsFileTree().matching(patternSet);
    }

    public Property<Task> getCompileTask() {
        return this.compileTask;
    }


}
