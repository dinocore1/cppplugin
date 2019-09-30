package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;

public interface Usage extends Named {

    Attribute<Usage> USAGE_ATTRIBUTE = Attribute.of("com.devsmart.usage", Usage.class);

    String COMPILE = "compiletime";
    String LINK = "linktime";
    String RUN = "runtime";
}
