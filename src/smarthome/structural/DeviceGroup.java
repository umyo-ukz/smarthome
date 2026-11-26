package smarthome.structural;

import smarthome.model.Device;
import smarthome.behavioral.DeviceObserver;
import java.util.ArrayList;
import java.util.List;

public class DeviceGroup implements Device {
    private final String name;
    private final List<Device> children = new ArrayList<>();

    public DeviceGroup(String name) {
        this.name = name;
    }

    public void add(Device d) {
        if (d == this) throw new IllegalArgumentException("A group cannot contain itself");
        children.add(d);
    }

    public void remove(Device d) {
        children.remove(d);
    }

    public List<Device> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public String getName() { return name; }

    @Override
    public void turnOn() {
        for (Device d : children) d.turnOn();
    }

    @Override
    public void turnOff() {
        for (Device d : children) d.turnOff();
    }

    @Override
    public void adjust(int value) {
        for (Device d : children) d.adjust(value);
    }

    @Override
    public boolean isOn() {
        return children.stream().anyMatch(Device::isOn);
    }

    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [").append(isOn() ? "ON" : "OFF").append("]\n");
        for (Device d : children) {
            String childStatus = d.getStatus();
            for (String line : childStatus.split("\n")) {
                sb.append("  ").append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public void addObserver(DeviceObserver o) {
        for (Device d : children) d.addObserver(o);
    }
}