package smarthome.model;

/**
 * The kinds of device the system can create.
 *
 * <p><b>Pattern role (Factory):</b> callers pass one of these values to
 * {@code DeviceFactory.createDevice(...)} instead of naming a concrete class.
 *
 * @author Smart Home team
 * @version 1.0
 */
public enum DeviceType {
    /** A dimmable light. */
    LIGHT,
    /** A thermostat with a target temperature. */
    THERMOSTAT,
    /** A speaker with a volume level. */
    SPEAKER,
    /** A fan; provided by the optional Adapter around the legacy OldFan. */
    FAN
}
