package smarthome.creational;

import smarthome.model.Device;
import smarthome.model.DeviceType;
import smarthome.model.Light;
import smarthome.model.Speaker;
import smarthome.model.Thermostat;
import smarthome.structural.OldFanAdapter;

/**
 * Creates devices by type.
 *
 * <p><b>Pattern role (Factory):</b> callers ask for a {@link DeviceType} and
 * receive a {@link Device}. They never call {@code new Light(...)} themselves
 * and never depend on concrete device classes, so adding a new device type
 * changes only this class.
 *
 * @author Smart Home team
 * @version 1.0
 */
public final class DeviceFactory {

    private DeviceFactory() {
        // Static factory: no instances.
    }

    /**
     * Builds a new device of the requested type. The device starts switched off.
     *
     * @param type the kind of device to create, must not be null
     * @param name the display name, must not be blank
     * @return a new device
     * @throws IllegalArgumentException if the type is null, not supported yet,
     *                                  or the name is blank
     */
    public static Device createDevice(DeviceType type, String name) {
        if (type == null) {
            throw new IllegalArgumentException("Device type must not be null");
        }
        switch (type) {
            case LIGHT:
                return new Light(name);
            case THERMOSTAT:
                return new Thermostat(name);
            case SPEAKER:
                return new Speaker(name);
            case FAN:
                return new OldFanAdapter(name);
            default:
                throw new IllegalArgumentException("Device type not supported yet: " + type);
        }
    }
}
