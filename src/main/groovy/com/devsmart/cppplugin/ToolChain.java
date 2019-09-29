package com.devsmart.cppplugin;

import org.gradle.api.component.SoftwareComponent;

public interface ToolChain extends SoftwareComponent {


    Platform getTargetPlatform();

    Tool<CppCompileSpec> getCppCompiler();

    Tool<StaticLibraryArchiverSpec> getArchiveTool();

    String getObjectFileExtention();
    String getStaticLibraryFileExtention();

}
