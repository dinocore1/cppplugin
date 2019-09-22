package com.devsmart.cppplugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

public class ArchiveTask extends DefaultTask {

    private final ConfigurableFileCollection objectFiles;
    private final RegularFileProperty outputFile;
    private final Property<String> exePath;

    @Inject
    public ArchiveTask() {
        Project project = getProject();
        ObjectFactory objectFactory = project.getObjects();
        this.objectFiles = objectFactory.fileCollection();
        this.outputFile = objectFactory.fileProperty();
        this.exePath = objectFactory.property(String.class);
    }

    @InputFiles
    public ConfigurableFileCollection getObjectFiles() {
        return this.objectFiles;
    }


    @Internal
    public Property<String> getExePath() {
        return this.exePath;
    }

    @OutputFile
    public RegularFileProperty getOutputFile() {
        return this.outputFile;
    }

    @TaskAction
    public void doLink() {

        List<String> commands = new LinkedList<String>();
        commands.add(exePath.get());
        commands.add("rcs");
        commands.add(outputFile.get().getAsFile().getAbsolutePath());

        objectFiles.forEach( f -> {
            commands.add(f.getAbsolutePath());
        });

        try {
            Process process = new ProcessBuilder(commands)
                    .start();
            int exitCode = process.waitFor();
            if(exitCode != 0){
                throw new RuntimeException("compile process returned " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
