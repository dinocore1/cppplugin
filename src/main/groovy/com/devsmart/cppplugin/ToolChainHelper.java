package com.devsmart.cppplugin;

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.util.PatternSet;

public class ToolChainHelper {

    public static FileCollection getCppSource(FileCollection sources) {
        PatternSet patternSet = new PatternSet();
        patternSet.include("**/*.cpp");
        patternSet.include("**/*.c++");
        patternSet.include("**/*.cc");
        return sources.getAsFileTree().matching(patternSet);
    }


}
