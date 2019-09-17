package com.devsmart.cppplugin.plugin;

import com.devsmart.cppplugin.NativeBinary;
import com.devsmart.cppplugin.NativeLibrary;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.work.WorkerLeaseService;

import javax.inject.Inject;

public class NativeLibraryPlugin implements Plugin<Project> {

    private final BuildOperationExecutor buildExecutor;
    private final WorkerLeaseService workerLease;

    @Inject
    public NativeLibraryPlugin(BuildOperationExecutor buildOperationExecutor, WorkerLeaseService workerLeaseService) {
        this.buildExecutor = buildOperationExecutor;
        this.workerLease = workerLeaseService;
    }

    @Override
    public void apply(Project project) {

        NativeLibrary lib = project.getObjects().newInstance(com.devsmart.cppplugin.NativeLibrary.class, "main");
        project.getExtensions().add("library", lib);
        project.getComponents().add(lib);
        lib.getBaseName().convention(project.getName());


        //final BuildOperationExecutor buildOperationExecutor = serviceRegistry.get(BuildOperationExecutor.class);


        project.getComponents().withType(NativeBinary.class, binary -> {

        });

        project.afterEvaluate(p -> {

        });

    }
}
