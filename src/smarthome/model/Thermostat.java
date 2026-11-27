package smarthome.model;

/**
 * A thermostat with a target temperature in degrees Celsius.
 * The range is 16 to 30 and it starts at 22.
 *
 * <p>Created by {@code DeviceFactory}; acts as a <i>leaf</i> in the Composite
 * pattern and as a <i>Subject</i> in the Observer pattern (through
 * {@link AbstractDevice}).
 *
 * @author Smart Home team
 * @version 1.0
 */
public class Thermostat extends AbstractDevice {

    /** Lowest allowed target temperature, in degrees Celsius. */
    public static final int MIN_TEMPERATURE = 16;
    /** Highest allowed target temperature, in degrees Celsius. */
    public static final int MAX_TEMPERATURE = 30;

    private int temperature = 22;

    /**
     * Creates a thermostat that is off, with a target of 22 degrees.
     *
     * @param name the display name, must not be blank
     */
    public Thermostat(String name) {
        super(name);
    }

    /**
     * Sets the target temperature.
     *
     * @param value target temperature from 16 to 30 degrees Celsius
     * @throws IllegalArgumentException if the value is outside 16 to 30
     */
    @Override
    public void adjust(int value) {
        if (value < MIN_TEMPERATURE || value > MAX_TEMPERATURE) {
            throw new IllegalArgumentException(
                    "Temperature must be between " + MIN_TEMPERATURE + " and " + MAX_TEMPERATURE);
        }
        if (value != temperature) {
            temperature = value;
            notifyObservers("temperature set to " + temperature + " C");
        }
    }

    /**
     * Returns the current target temperature.
     *
     * @return the temperature in degrees Celsius
     */
    public int getTemperature() {
        return temperature;
    }

    @Override
    public int getValue() {
        return temperature;
    }

    @Override
    public String getValueLabel() {
        return "Temperature (16-30 °C)";
    }

    @Override
    protected String details() {
        return "target " + temperature + " C";
    }
}
