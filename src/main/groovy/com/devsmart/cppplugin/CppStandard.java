package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;

public interface CppStandard extends Named {

    Attribute<CppStandard> CPPSTANDARD_ATTRIBUTE = Attribute.of(CppStandard.class);

    String CPP98 = "c++98";
    String CPP03 = "c++03";
    String CPP11 = "c++11";
    String CPP14 = "c++14";
    String CPP17 = "c++17";
    String CPP20 = "c++20";


}
