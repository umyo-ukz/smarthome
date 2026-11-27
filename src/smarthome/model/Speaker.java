package smarthome.model;

/**
 * A speaker with a volume from 0 to 100. It starts at 30.
 *
 * <p>Created by {@code DeviceFactory}; acts as a <i>leaf</i> in the Composite
 * pattern and as a <i>Subject</i> in the Observer pattern (through
 * {@link AbstractDevice}).
 *
 * @author Smart Home team
 * @version 1.0
 */
public class Speaker extends AbstractDevice {

    /** Lowest allowed volume. */
    public static final int MIN_VOLUME = 0;
    /** Highest allowed volume. */
    public static final int MAX_VOLUME = 100;

    private int volume = 30;

    /**
     * Creates a speaker that is off, at volume 30.
     *
     * @param name the display name, must not be blank
     */
    public Speaker(String name) {
        super(name);
    }

    /**
     * Sets the volume.
     *
     * @param value volume from 0 to 100
     * @throws IllegalArgumentException if the value is outside 0 to 100
     */
    @Override
    public void adjust(int value) {
        if (value < MIN_VOLUME || value > MAX_VOLUME) {
            throw new IllegalArgumentException(
                    "Volume must be between " + MIN_VOLUME + " and " + MAX_VOLUME);
        }
        if (value != volume) {
            volume = value;
            notifyObservers("volume set to " + volume);
        }
    }

    /**
     * Returns the current volume.
     *
     * @return volume from 0 to 100
     */
    public int getVolume() {
        return volume;
    }

    @Override
    public int getValue() {
        return volume;
    }

    @Override
    public String getValueLabel() {
        return "Volume (0-100)";
    }

    @Override
    protected String details() {
        return "volume " + volume;
    }
}
