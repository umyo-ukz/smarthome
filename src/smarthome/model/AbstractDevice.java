package smarthome.model;

import java.util.ArrayList;
import java.util.List;

import smarthome.behavioral.DeviceObserver;

/**
 * Base class for single devices ({@code Light}, {@code Thermostat},
 * {@code Speaker}) and for adapters around legacy devices.
 *
 * <p>It holds the name, the on/off state and the list of observers, and it
 * provides the shared on/off behaviour so subclasses only add their own
 * level (brightness, temperature, volume) and implement {@link #adjust(int)}.
 *
 * <p><b>Pattern role (Observer):</b> this class is the <i>Subject</i>. It
 * keeps the observer list and calls {@link #notifyObservers(String)} after
 * every real state change.
 *
 * @author Smart Home team
 * @version 1.0
 */
public abstract class AbstractDevice implements Device {

    private final String name;
    private boolean on;
    private final List<DeviceObserver> observers = new ArrayList<>();

    /**
     * Creates a device that starts switched off.
     *
     * @param name the display name, must not be null or blank
     * @throws IllegalArgumentException if the name is null or blank
     */
    protected AbstractDevice(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Device name must not be blank");
        }
        this.name = name.trim();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOn() {
        return on;
    }

    @Override
    public void turnOn() {
        setOn(true, "turned ON");
    }

    @Override
    public void turnOff() {
        setOn(false, "turned OFF");
    }

    /**
     * Updates the on/off flag and notifies observers, but only if the state
     * really changes. Calling {@code turnOn()} on a light that is already on
     * therefore produces no second notification.
     *
     * @param newState the requested state
     * @param change   the text sent to observers if the state changed
     */
    protected void setOn(boolean newState, String change) {
        if (on != newState) {
            on = newState;
            notifyObservers(change);
        }
    }

    @Override
    public void addObserver(DeviceObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Tells every registered observer that this device changed. Subclasses
     * call this from {@link #adjust(int)} after updating their own level.
     *
     * @param change a short description of the change
     */
    protected void notifyObservers(String change) {
        // Iterate over a copy so an observer may register another observer
        // from inside its callback without breaking the loop.
        for (DeviceObserver observer : new ArrayList<>(observers)) {
            observer.onDeviceChanged(this, change);
        }
    }

    /**
     * Returns extra status text, such as {@code "brightness 70"}. Subclasses
     * override this; the default is empty.
     *
     * @return the detail text, never null
     */
    protected String details() {
        return "";
    }

    @Override
    public String getStatus() {
        String extra = details();
        return name + " [" + (on ? "ON" : "OFF") + "]" + (extra.isEmpty() ? "" : ", " + extra);
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
