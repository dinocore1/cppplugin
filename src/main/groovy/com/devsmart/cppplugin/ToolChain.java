package com.devsmart.cppplugin;

import org.gradle.api.component.SoftwareComponent;

public interface ToolChain extends SoftwareComponent {


    Platform getTargetPlatform();

    CppCompiler getCppCompiler();

}
