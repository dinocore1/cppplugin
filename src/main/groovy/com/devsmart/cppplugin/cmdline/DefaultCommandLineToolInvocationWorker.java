package com.devsmart.cppplugin.cmdline;

import org.gradle.internal.io.StreamByteBuffer;
import org.gradle.internal.operations.BuildOperationContext;
import org.gradle.internal.operations.BuildOperationDescriptor;
import org.gradle.process.internal.ExecAction;
import org.gradle.process.internal.ExecActionFactory;
import org.gradle.process.internal.ExecException;
import org.gradle.util.GFileUtils;

import java.io.File;
import java.nio.charset.Charset;

public class DefaultCommandLineToolInvocationWorker implements CommandLineToolInvocationWorker {

    private final String name;
    private final ExecActionFactory execActionFactory;

    public DefaultCommandLineToolInvocationWorker(String name, ExecActionFactory execActionFactory) {
        this.name = name;
        this.execActionFactory = execActionFactory;
    }

    @Override
    public String getDisplayName() {
        return "command line tool '" + name + "'";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public void execute(CommandLineToolInvocation invocation, BuildOperationContext buildOperationContext) {
        BuildOperationDescriptor description = invocation.description().build();
        ExecAction toolExec = execActionFactory.newExecAction();

        toolExec.executable(invocation.getExe());
        if (invocation.getWorkDirectory() != null) {
            GFileUtils.mkdirs(invocation.getWorkDirectory());
            toolExec.workingDir(invocation.getWorkDirectory());
        }

        toolExec.args(invocation.getArgs());

        toolExec.environment(invocation.getEnvironment());

        StreamByteBuffer errOutput = new StreamByteBuffer();
        StreamByteBuffer stdOutput = new StreamByteBuffer();
        toolExec.setErrorOutput(errOutput.getOutputStream());
        toolExec.setStandardOutput(stdOutput.getOutputStream());

        try {
            toolExec.execute();
            invocation.getLogger().operationSuccess(description.getDisplayName(), combineOutput(stdOutput, errOutput));
        } catch (ExecException e) {
            invocation.getLogger().operationFailed(description.getDisplayName(), combineOutput(stdOutput, errOutput));
            throw new CommandLineToolInvocationFailure(invocation, String.format("%s failed while %s.", name, description.getDisplayName()));
        }
    }

    private String combineOutput(StreamByteBuffer stdOutput, StreamByteBuffer errOutput) {
        return stdOutput.readAsString(Charset.defaultCharset()) + errOutput.readAsString(Charset.defaultCharset());
    }
}
