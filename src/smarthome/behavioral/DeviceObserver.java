package smarthome.behavioral;

import smarthome.model.Device;

public interface DeviceObserver {
    void onDeviceChanged(Device device, String change);
}