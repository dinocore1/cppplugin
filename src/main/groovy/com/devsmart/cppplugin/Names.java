package com.devsmart.cppplugin;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.file.RelativePath;

public class Names {

    private final String[] names;
    private final String baseName;

    public Names(String... names) {
        this.names = names;
        baseName = buildBaseName();
    }

    public static Names of(VariantIdentity variant) {

        Platform platform = variant.getPlatform();
        String[] names = new String[]{
                platform.getOperatingSystem().getName(),
                platform.getMachineArchitecture().getName(),
                variant.getLinkage().getName().toLowerCase(),
                (variant.isDebuggable() ? "debug" : "")
            };

        return new Names(names);
    }

    private String buildBaseName() {
        StringBuilder builder = new StringBuilder();

        builder.append(StringUtils.uncapitalize(names[0]));
        for(int i=1;i<names.length;i++) {
            builder.append(StringUtils.capitalize(names[i]));
        }

        return builder.toString();
    }

    public String getBaseName() {
        return this.baseName;
    }

    public String getTaskName(String verb) {
        return verb + StringUtils.capitalize(baseName);
    }

    public RelativePath getRelativePath() {
        return new RelativePath(false, names);
    }
}
