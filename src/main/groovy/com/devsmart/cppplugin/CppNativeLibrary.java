package com.devsmart.cppplugin;

import java.util.Set;

public interface CppNativeLibrary extends NativeBinary {

    Set<LibraryDescriptor> getLinkDependencies();
}
