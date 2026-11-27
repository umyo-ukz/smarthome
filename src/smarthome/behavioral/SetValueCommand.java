package smarthome.behavioral;

import smarthome.model.Device;

public class SetValueCommand implements Command {
    private final Device device;
    private final int newValue;
    private Runnable previousState;

    public SetValueCommand(Device device, int newValue) {
        this.device = device;
        this.newValue = newValue;
    }

    @Override
    public void execute() {
        previousState = device.captureState();
        try {
            device.adjust(newValue);
        } catch (IllegalArgumentException e) {
            previousState.run();
            throw e;
        }
    }

    @Override
    public void undo() {
        previousState.run();
    }

    @Override
    public String describe() {
        return "Set " + device.getName() + " to " + newValue;
    }
}