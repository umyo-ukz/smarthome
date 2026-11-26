package smarthome.creational;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import smarthome.behavioral.DeviceObserver;
import smarthome.model.Device;

/**
 * The single hub of the smart home. It keeps a registry of every device and
 * group by name, and it receives a notification whenever a registered device
 * changes state.
 *
 * <p><b>Pattern roles:</b>
 * <ul>
 *   <li><b>Singleton</b>: the constructor is private and {@link #getInstance()}
 *       always returns the same object, so only one controller exists.</li>
 *   <li><b>Observer</b>: it implements {@link DeviceObserver} and is the
 *       observer that devices notify. Devices never refer to it directly.</li>
 * </ul>
 *
 * @author Smart Home team
 * @version 1.0
 */
public final class CentralController implements DeviceObserver {

    private static CentralController instance;

    private final Map<String, Device> devices = new LinkedHashMap<>();
    private final List<String> eventLog = new ArrayList<>();

    private CentralController() {
        // Private: use getInstance().
    }

    /**
     * Returns the one and only controller, creating it on first use.
     *
     * @return the shared controller
     */
    public static synchronized CentralController getInstance() {
        if (instance == null) {
            instance = new CentralController();
        }
        return instance;
    }

    /**
     * Adds a device (or a group) to the registry and starts listening to it.
     * Names are matched ignoring case, so "Lamp" and "lamp" count as the same name.
     *
     * @param device the device to register, must not be null
     * @throws IllegalArgumentException if the device is null or its name is already used
     */
    public void registerDevice(Device device) {
        if (device == null) {
            throw new IllegalArgumentException("Device must not be null");
        }
        String key = keyOf(device.getName());
        if (devices.containsKey(key)) {
            throw new IllegalArgumentException("A device named '" + device.getName() + "' already exists");
        }
        devices.put(key, device);
        device.addObserver(this); // Observer: the controller now hears about this device
    }

    /**
     * Finds a registered device by name, ignoring case.
     *
     * @param name the name to look up
     * @return the device, or null if none has that name
     */
    public Device getDevice(String name) {
        return name == null ? null : devices.get(keyOf(name));
    }

    /**
     * Returns every registered device in the order they were added.
     *
     * @return a read-only view of the registered devices
     */
    public Collection<Device> listDevices() {
        return Collections.unmodifiableCollection(devices.values());
    }

    /**
     * Returns the notifications received so far, oldest first.
     *
     * @return a read-only copy of the event log
     */
    public List<String> getEventLog() {
        return Collections.unmodifiableList(new ArrayList<>(eventLog));
    }

    /**
     * Receives a state-change notification from a device, records it and
     * prints it to the console.
     *
     * @param device the device that changed
     * @param change a short description of the change
     */
    @Override
    public void onDeviceChanged(Device device, String change) {
        String line = device.getName() + ": " + change;
        eventLog.add(line);
        System.out.println("[Controller] " + line);
    }

    private static String keyOf(String name) {
        return name.trim().toLowerCase();
    }
}
