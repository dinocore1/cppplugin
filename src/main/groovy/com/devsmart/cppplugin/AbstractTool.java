package com.devsmart.cppplugin;

import com.devsmart.cppplugin.cmdline.CommandLineToolInvocation;
import com.devsmart.cppplugin.cmdline.CommandLineToolInvocationWorker;
import com.devsmart.cppplugin.cmdline.DefaultCommandLineToolInvocation;
import org.gradle.api.Action;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.operations.BuildOperationQueue;
import org.gradle.internal.operations.logging.BuildOperationLogger;
import org.gradle.internal.work.WorkerLeaseService;

import java.io.File;
import java.util.List;

public abstract class AbstractTool<T> implements Tool<T> {

    private File exe;
    private final BuildOperationExecutor buildOperationExecutor;
    private final CommandLineToolInvocationWorker commandLineToolInvocationWorker;
    private final WorkerLeaseService workerLeaseService;

    public AbstractTool(BuildOperationExecutor buildOperationExecutor, CommandLineToolInvocationWorker commandLineToolInvocationWorker, WorkerLeaseService workerLeaseService) {
        this.buildOperationExecutor = buildOperationExecutor;
        this.commandLineToolInvocationWorker = commandLineToolInvocationWorker;
        this.workerLeaseService = workerLeaseService;
    }

    @Override
    public File getExe() {
        return this.exe;
    }

    @Override
    public void exe(String exeFilePath) {
        this.exe = new File(exeFilePath);
    }

    @Override
    public void exe(File exeFilePath) {
        this.exe = exeFilePath;
    }

    @Override
    public WorkResult execute(T spec) {
        final Action<BuildOperationQueue<CommandLineToolInvocation>> invocationAction = newInvocationAction(spec);

        workerLeaseService.withoutProjectLock(new Runnable() {
            @Override
            public void run() {
                buildOperationExecutor.runAll(commandLineToolInvocationWorker, invocationAction);
            }
        });

        return WorkResults.didWork(true);
    }

    protected abstract Action<BuildOperationQueue<CommandLineToolInvocation>> newInvocationAction(T spec);

    protected CommandLineToolInvocation newInvocation(String displayName, Iterable<String> args, BuildOperationLogger logger) {
        return new DefaultCommandLineToolInvocation(exe, args, displayName, logger);
    }

}
