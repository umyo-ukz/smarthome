package smarthome.structural;

import smarthome.model.AbstractDevice;

public class OldFanAdapter extends AbstractDevice {
    private final OldFan fan = new OldFan();

    public OldFanAdapter(String name) { super(name); }

    @Override
    public void turnOn() {
        fan.startSpinning();
        setOn(true, "turned ON");
    }

    @Override
    public void turnOff() {
        fan.stopSpinning();
        setOn(false, "turned OFF");
    }

    @Override
    public void adjust(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Speed must be between 0 and 100");
        }
        int level = Math.min(value / 25, 3);
        if (level != fan.getSpeedLevel()) {
            fan.setSpeedLevel(level);
            notifyObservers("speed set to " + level);
        }
    }

    @Override
    public int getValue() {
        return fan.getSpeedLevel() * 25;
    }

    @Override
    public String getValueLabel() {
        return "Speed (0-100)";
    }

    @Override
    public String getStatus() {
        return getName() + " (Fan) " + (isOn() ? "ON" : "OFF") + ", speed " + fan.getSpeedLevel();
    }
}