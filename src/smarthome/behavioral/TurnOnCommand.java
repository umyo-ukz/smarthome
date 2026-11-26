package smarthome.behavioral;

import smarthome.model.Device;

public class TurnOnCommand implements Command {
    private final Device device;
    private boolean wasOn;

    public TurnOnCommand(Device device) { this.device = device; }

    @Override
    public void execute() {
        wasOn = device.isOn();
        device.turnOn();
    }

    @Override
    public void undo() {
        if (!wasOn) device.turnOff();
    }

    @Override
    public String describe() {
        return "Turn on " + device.getName();
    }
}