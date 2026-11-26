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
        int level = value / 25;
        if (level > 3) level = 3;
        fan.setSpeedLevel(level);
        notifyObservers("speed set to " + fan.getSpeedLevel());
    }

    @Override
    public String getStatus() {
        return getName() + " (Fan) " + (isOn() ? "ON" : "OFF") + ", speed " + fan.getSpeedLevel();
    }
}