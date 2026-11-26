package smarthome.behavioral;

import smarthome.model.Device;

/**
 * Receives notifications when a {@link Device} changes state.
 *
 * <p><b>Pattern role (Observer):</b> this is the <i>Observer</i> interface.
 * Devices are the <i>Subjects</i> and know only this interface, not who
 * implements it. {@code CentralController} implements it to log or react to
 * changes, so devices never reference the controller directly.
 *
 * @author Smart Home team
 * @version 1.0
 */
public interface DeviceObserver { 

    /**
     * Called by a device immediately after its state has changed.
     *
     * @param device the device that changed, never null
     * @param change a short description of what happened, for example
     *               {@code "turned ON"} or {@code "brightness set to 70"}
     */
    void onDeviceChanged(Device device, String change);
}
