package smarthome.behavioral;

import smarthome.model.Device;

public class SetValueCommand implements Command {
    private final Device device;
    private final int newValue;
    private int oldValue;

    public SetValueCommand(Device device, int newValue) {
        this.device = device;
        this.newValue = newValue;
    }

    @Override
    public void execute() {
        oldValue = 0;
        device.adjust(newValue);
    }

    @Override
    public void undo() {
        device.adjust(oldValue);
    }

    @Override
    public String describe() {
        return "Set " + device.getName() + " to " + newValue;
    }
}