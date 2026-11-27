package smarthome.behavioral;

import smarthome.model.Device;

public class TurnOffCommand implements Command {
    private final Device device;
    private Runnable previousState;

    public TurnOffCommand(Device device) { this.device = device; }

    @Override
    public void execute() {
        previousState = device.captureState();
        device.turnOff();
    }

    @Override
    public void undo() {
        previousState.run();
    }

    @Override
    public String describe() {
        return "Turn off " + device.getName();
    }
}