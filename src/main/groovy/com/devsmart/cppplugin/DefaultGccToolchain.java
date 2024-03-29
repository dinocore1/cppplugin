package com.devsmart.cppplugin;

import com.devsmart.cppplugin.cmdline.CommandLineToolInvocation;
import com.devsmart.cppplugin.cmdline.CommandLineToolInvocationWorker;
import com.devsmart.cppplugin.cmdline.DefaultCommandLineToolInvocationWorker;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.WorkResult;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.operations.BuildOperationQueue;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.process.internal.ExecActionFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultGccToolchain implements ToolChain {

    private final ObjectFactory objectFactory;
    private final CppCompiler cppCompiler;
    private final ArStaticLibraryArchiver archiveTool;
    private final CppLinker linkerTool;
    private Platform platform;

    @Inject
    public DefaultGccToolchain(ObjectFactory objectFactory, BuildOperationExecutor executor, ExecActionFactory execActionFactory, WorkerLeaseService workerLeaseService) {
        this.objectFactory = objectFactory;
        this.cppCompiler = new CppCompiler(executor, new DefaultCommandLineToolInvocationWorker("c++", execActionFactory), workerLeaseService);
        this.archiveTool = new ArStaticLibraryArchiver(executor, new DefaultCommandLineToolInvocationWorker("ar", execActionFactory), workerLeaseService);
        this.linkerTool = new CppLinker(executor, new DefaultCommandLineToolInvocationWorker("c++", execActionFactory), workerLeaseService);
    }

    @Override
    public Platform getTargetPlatform() {
        return this.platform;
    }

    @Override
    public Tool<CppCompileSpec> getCppCompiler() {
        return this.cppCompiler;
    }

    @Override
    public Tool<SharedLibraryLinkerSpec> getLinkerTool() {
        return this.linkerTool;
    }

    @Override
    public Tool<StaticLibraryArchiverSpec> getArchiveTool() {
        return this.archiveTool;
    }

    @Override
    public String getObjectFileExtention() {
        return "o";
    }

    @Override
    public String getStaticLibraryFileExtention() {
        return "a";
    }

    @Override
    public String getSharedLibraryFileExtention() {
        return "so";
    }

    @Override
    public String getName() {
        return String.format("gcc %s", platform);
    }

    public void platform(String os, String arch) {
        platform = new Platform(objectFactory.named(MachineArchitecture.class, arch),
                objectFactory.named(OperatingSystem.class, os));
    }

    public void cppCompiler(Action<? super CppCompiler> config) {
        config.execute(cppCompiler);
    }

    public void archiveTool(Action<? super ArStaticLibraryArchiver> config) {
        config.execute(archiveTool);
    }

    public void linkerTool(Action<? super CppLinker> config) {
        config.execute(linkerTool);
    }

    private static class CppCompiler extends AbstractTool<CppCompileSpec> {

        public CppCompiler(BuildOperationExecutor buildOperationExecutor, CommandLineToolInvocationWorker commandLineToolInvocationWorker, WorkerLeaseService workerLeaseService) {
            super(buildOperationExecutor, commandLineToolInvocationWorker, workerLeaseService);
        }

        @Override
        protected Action<BuildOperationQueue<CommandLineToolInvocation>> newInvocationAction(CppCompileSpec spec) {
            return queue -> {
                queue.setLogLocation(spec.getOperationLogger().getLogLocation());
                for(File sourceFile : spec.getSourceFiles()) {
                    queue.add(newInvocation("compiling ".concat(sourceFile.getName()), getArgs(spec, sourceFile), spec.getOperationLogger()));
                }
            };
        }

        List<String> getArgs(CppCompileSpec spec, File sourceFile) {
            List<String> args = new ArrayList<String>();
            args.add("-c");
            args.add(sourceFile.getAbsolutePath());

            spec.getIncludeDirs().forEach(f -> {
                args.add("-I");
                args.add(f.getAbsolutePath());
            });

            spec.getArgs().forEach(a -> {
                args.add(a);
            });

            args.add("-std=" + spec.getCppStandard());

            if(spec.isPositionIndependent()) {
                args.add("-fPIC");
            }

            args.add("-o");
            args.add(spec.getObjectFile(sourceFile).getAbsolutePath());

            return args;
        }
    }

    private static class ArStaticLibraryArchiver extends AbstractTool<StaticLibraryArchiverSpec> implements ArgsTransformer<StaticLibraryArchiverSpec> {

        public ArStaticLibraryArchiver(BuildOperationExecutor buildOperationExecutor, CommandLineToolInvocationWorker commandLineToolInvocationWorker, WorkerLeaseService workerLeaseService) {
            super(buildOperationExecutor, commandLineToolInvocationWorker, workerLeaseService);
        }

        @Override
        public WorkResult execute(StaticLibraryArchiverSpec spec) {
            deletePreviousOutput(spec);
            return super.execute(spec);
        }

        private void deletePreviousOutput(StaticLibraryArchiverSpec spec) {
            // Need to delete the previous archive, otherwise stale object files will remain
            if (!spec.getOutputFile().isFile()) {
                return;
            }
            if (!(spec.getOutputFile().delete())) {
                throw new GradleException("Create static archive failed: could not delete previous archive");
            }
        }

        @Override
        protected Action<BuildOperationQueue<CommandLineToolInvocation>> newInvocationAction(StaticLibraryArchiverSpec spec) {
            return queue -> {
                CommandLineToolInvocation invocation = newInvocation("archiving ".concat(spec.getOutputFile().getName()),
                        getArgs(spec), spec.getOperationLogger());

                queue.setLogLocation(spec.getOperationLogger().getLogLocation());
                queue.add(invocation);
            };
        }

        @Override
        public List<String> getArgs(StaticLibraryArchiverSpec spec) {
            List<String> args = new ArrayList<String>();
            // -r : Add files to static archive, creating if required
            // -c : Don't write message to standard error when creating archive
            // -s : Create an object file index (equivalent to running 'ranlib')
            args.add("-rcs");
            args.addAll(spec.getArgs());
            args.add(spec.getOutputFile().getAbsolutePath());
            for (File file : spec.getObjectFiles()) {
                args.add(file.getAbsolutePath());
            }
            return args;
        }
    }

    private static class CppLinker extends AbstractTool<SharedLibraryLinkerSpec> implements ArgsTransformer<SharedLibraryLinkerSpec> {

        public CppLinker(BuildOperationExecutor buildOperationExecutor, CommandLineToolInvocationWorker commandLineToolInvocationWorker, WorkerLeaseService workerLeaseService) {
            super(buildOperationExecutor, commandLineToolInvocationWorker, workerLeaseService);
        }

        @Override
        protected Action<BuildOperationQueue<CommandLineToolInvocation>> newInvocationAction(SharedLibraryLinkerSpec spec) {
            return queue -> {
                CommandLineToolInvocation invocation = newInvocation("linking ".concat(spec.getOutputFile().getName()),
                        getArgs(spec), spec.getOperationLogger());

                queue.setLogLocation(spec.getOperationLogger().getLogLocation());
                queue.add(invocation);
            };
        }


        @Override
        public List<String> getArgs(SharedLibraryLinkerSpec spec) {
            List<String> args = new ArrayList<>();
            args.addAll(spec.getArgs());
            args.add("--shared");
            args.add("-o");
            args.add(spec.getOutputFile().getAbsolutePath());
            for(File objectFile : spec.getObjectFiles()) {
                args.add(objectFile.getAbsolutePath());
            }
            return args;
        }
    }


}
