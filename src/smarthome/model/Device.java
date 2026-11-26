package smarthome.model;

import smarthome.behavioral.DeviceObserver;

/**
 * Common contract for everything that can be controlled in the smart home:
 * a single device, a room (group of devices) or an adapted legacy device.
 *
 * <p><b>Pattern roles:</b>
 * <ul>
 *   <li><b>Composite</b>: this is the <i>Component</i>. A {@code DeviceGroup}
 *       implements it too, so a room can be controlled exactly like one device.</li>
 *   <li><b>Factory</b>: {@code DeviceFactory} returns this type, so callers
 *       never depend on concrete device classes.</li>
 *   <li><b>Command</b>: commands hold a {@code Device} as their <i>Receiver</i>.</li>
 *   <li><b>Observer</b>: this is the <i>Subject</i> side; observers register
 *       through {@link #addObserver(DeviceObserver)}.</li>
 *   <li><b>Adapter</b>: this is the <i>Target</i> interface that a legacy
 *       device is adapted to.</li>
 * </ul>
 *
 * @author Smart Home team
 * @version 1.0
 */
public interface Device {

    /**
     * Returns the display name of this device or group.
     *
     * @return the name, never null or blank
     */
    String getName();

    /**
     * Switches this device (or every device in a group) on.
     * Observers are notified only if the state actually changes.
     */
    void turnOn();

    /**
     * Switches this device (or every device in a group) off.
     * Observers are notified only if the state actually changes.
     */
    void turnOff();

    /**
     * Reports whether this device is on. For a group, this is true if at
     * least one device inside it is on.
     *
     * @return true if on, false if off
     */
    boolean isOn();

    /**
     * Sets the device's main level: brightness for a light, temperature for
     * a thermostat, volume for a speaker. Out-of-range values are rejected
     * with an {@link IllegalArgumentException}.
     *
     * @param value the new level
     * @throws IllegalArgumentException if the value is outside the valid range
     */
    void adjust(int value);

    /**
     * Returns a human-readable status. A group may return several indented
     * lines, one per child.
     *
     * @return the status text
     */
    String getStatus();

    /**
     * Registers an observer to be told when this device's state changes.
     * A group forwards the registration to the devices it contains.
     *
     * @param observer the observer to register, ignored if null
     */
    void addObserver(DeviceObserver observer);
}
