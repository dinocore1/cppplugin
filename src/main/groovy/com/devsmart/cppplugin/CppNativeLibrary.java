package com.devsmart.cppplugin;

import com.devsmart.cppplugin.components.NativeBinary;

import java.util.Set;

public interface CppNativeLibrary extends NativeBinary {

    Set<LibraryDescriptor> getLinkDependencies();
}
