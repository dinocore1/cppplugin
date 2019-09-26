package com.devsmart.cppplugin;

import org.gradle.api.Named;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.Input;

import java.util.Objects;

public class Platform implements Named {

    public static Attribute<Platform> PLATFORM_ATTRIBUTE = Attribute.of(Platform.class);


    private DefaultMachineArchitecture machineArchitecture;
    private DefaultOperatingSystem operatingSystem;

    public Platform(DefaultMachineArchitecture machineArchitecture, DefaultOperatingSystem operatingSystem) {
        this.machineArchitecture = machineArchitecture;
        this.operatingSystem = operatingSystem;
    }

    public DefaultMachineArchitecture getMachineArchitecture() {
        return machineArchitecture;
    }

    public DefaultOperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public static Platform getHostPlatform() {
        return new Platform(DefaultMachineArchitecture.getHostArchitecture(), DefaultOperatingSystem.getHostOS());
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineArchitecture, operatingSystem);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != getClass()) {
            return false;
        }

        Platform other = (Platform) obj;
        return Objects.equals(machineArchitecture, other.machineArchitecture) &&
                Objects.equals(operatingSystem, other.operatingSystem);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Input
    @Override
    public String getName() {
        return String.format("%s_%s", operatingSystem.getName(), machineArchitecture.getName());
    }


}
