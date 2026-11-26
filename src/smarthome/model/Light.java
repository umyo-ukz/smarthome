package smarthome.model;

/**
 * A dimmable light. Brightness runs from 0 to 100 and starts at 100.
 *
 * <p>Created by {@code DeviceFactory}; acts as a <i>leaf</i> in the Composite
 * pattern and as a <i>Subject</i> in the Observer pattern (through
 * {@link AbstractDevice}).
 *
 * @author Smart Home team
 * @version 1.0
 */
public class Light extends AbstractDevice {

    /** Lowest allowed brightness. */
    public static final int MIN_BRIGHTNESS = 0;
    /** Highest allowed brightness. */
    public static final int MAX_BRIGHTNESS = 100;

    private int brightness = MAX_BRIGHTNESS;

    /**
     * Creates a light that is off, at full brightness.
     *
     * @param name the display name, must not be blank
     */
    public Light(String name) {
        super(name);
    }

    /**
     * Sets the brightness.
     *
     * @param value brightness from 0 to 100
     * @throws IllegalArgumentException if the value is outside 0 to 100
     */
    @Override
    public void adjust(int value) {
        if (value < MIN_BRIGHTNESS || value > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException(
                    "Brightness must be between " + MIN_BRIGHTNESS + " and " + MAX_BRIGHTNESS);
        }
        if (value != brightness) {
            brightness = value;
            notifyObservers("brightness set to " + brightness);
        }
    }

    /**
     * Returns the current brightness.
     *
     * @return brightness from 0 to 100
     */
    public int getBrightness() {
        return brightness;
    }

    @Override
    protected String details() {
        return "brightness " + brightness;
    }
}
