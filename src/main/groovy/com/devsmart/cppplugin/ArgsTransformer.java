package com.devsmart.cppplugin;

import java.util.List;

public interface ArgsTransformer<T extends ToolSpec> {

    List<String> getArgs(T spec);
}
