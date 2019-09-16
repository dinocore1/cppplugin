package com.devsmart.cppplugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.work.InputChanges;

import java.util.LinkedHashMap;
import java.util.Map;


public class CompileTask extends DefaultTask {

    //See AbstractNativeCompileTask, AbstractCompiler<T extends BinaryToolSpec>, BuildOperationExecutor, NativeCompiler<T extends NativeCompileSpec>::newInvocationAction

    private final ConfigurableFileCollection source;
    private final ConfigurableFileCollection includes;
    private final Property<ToolChain> toolChain;
    private final Map<String, String> macros = new LinkedHashMap<>();
    private final Property<TargetCombo> targetCombo;

    public CompileTask() {
        ObjectFactory objectFactory = getProject().getObjects();
        source = objectFactory.fileCollection();
        dependsOn(source);
        includes = objectFactory.fileCollection();
        dependsOn(includes);
        toolChain = objectFactory.property(ToolChain.class);
        targetCombo = objectFactory.property(TargetCombo.class);

    }

    @TaskAction
    public void compile(InputChanges inputs) {

    }

    @Internal
    Property<ToolChain> getToolChain() {
        return this.toolChain;
    }

    @SkipWhenEmpty
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public ConfigurableFileCollection getSource() {
        return this.source;
    }

    @Input
    public Map<String, String> getMacros() {
        return this.macros;
    }

    @Input
    public Property<TargetCombo> getTargetCombo() {
        return this.targetCombo;
    }
}
