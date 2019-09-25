package com.devsmart.cppplugin.components;

import com.devsmart.cppplugin.VariantIdentity;
import org.gradle.api.component.SoftwareComponent;

public interface NativeComponent extends SoftwareComponent {

    VariantIdentity getVariant();
}
