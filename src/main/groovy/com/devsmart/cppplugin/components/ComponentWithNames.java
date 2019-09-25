package com.devsmart.cppplugin.components;

import com.devsmart.cppplugin.Names;
import org.gradle.api.component.SoftwareComponent;

public interface ComponentWithNames extends SoftwareComponent {

    Names getNames();
}
