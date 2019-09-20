package com.devsmart.cppplugin;

import org.gradle.util.VersionNumber;

public interface LibraryDescriptor {

    String getGroupName();
    String getArtifactName();
    VersionNumber getVersion();
    VariantIdentity getVariant();


}
