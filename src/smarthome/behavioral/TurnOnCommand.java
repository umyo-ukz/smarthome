package smarthome.behavioral;

import smarthome.model.Device;

public class TurnOnCommand implements Command {
    private final Device device;
    private Runnable previousState;

    public TurnOnCommand(Device device) { this.device = device; }

    @Override
    public void execute() {
        previousState = device.captureState();
        device.turnOn();
    }

    @Override
    public void undo() {
        previousState.run();
    }

    @Override
    public String describe() {
        return "Turn on " + device.getName();
    }
}