package com.devsmart.cppplugin;

public class ToolChainHelper {



    public static boolean isCrossCompiler(Platform targetPlatform) {
        Platform host = Platform.getHostPlatform();
        return !host.equals(targetPlatform);
    }
}
