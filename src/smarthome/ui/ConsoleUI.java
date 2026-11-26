package smarthome.ui;

import smarthome.behavioral.Command;
import smarthome.behavioral.CommandInvoker;
import smarthome.behavioral.SetValueCommand;
import smarthome.behavioral.TurnOffCommand;
import smarthome.behavioral.TurnOnCommand;
import smarthome.creational.CentralController;
import smarthome.creational.DeviceFactory;
import smarthome.model.Device;
import smarthome.model.DeviceType;
import smarthome.structural.DeviceGroup;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final CentralController controller = CentralController.getInstance();
    private final CommandInvoker invoker = new CommandInvoker();

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": createDevice(); break;
                case "2": listDevices(); break;
                case "3": toggleDevice(); break;
                case "4": adjustDevice(); break;
                case "5": createRoom(); break;
                case "6": addToRoom(); break;
                case "7": roomToggle(); break;
                case "8": undoLast(); break;
                case "9": homeStatus(); break;
                case "0": running = false; break;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Smart Home Automation ===");
        System.out.println("1) Create device   2) List devices   3) On/Off");
        System.out.println("4) Adjust          5) Create room    6) Add to room");
        System.out.println("7) Room On/Off     8) Undo last      9) Home status");
        System.out.println("0) Exit");
        System.out.print("> ");
    }

    private void createDevice() {
        System.out.print("Type (LIGHT/THERMOSTAT/SPEAKER/FAN): ");
        String typeStr = scanner.nextLine().trim().toUpperCase();
        DeviceType type;
        try {
            type = DeviceType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown type");
            return;
        }
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name must not be blank");
            return;
        }
        try {
            Device d = DeviceFactory.createDevice(type, name);
            controller.registerDevice(d);
            System.out.println("Created " + type + " '" + name + "'");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listDevices() {
        if (controller.listDevices().isEmpty()) {
            System.out.println("No devices");
            return;
        }
        for (Device d : controller.listDevices()) {
            System.out.println(d.getStatus());
        }
    }

    private void toggleDevice() {
        System.out.print("Device name: ");
        String name = scanner.nextLine().trim();
        Device d = controller.getDevice(name);
        if (d == null) {
            System.out.println("Unknown device");
            return;
        }
        System.out.print("on/off: ");
        String v = scanner.nextLine().trim().toLowerCase();
        Command c;
        if (v.equals("on")) c = new TurnOnCommand(d);
        else if (v.equals("off")) c = new TurnOffCommand(d);
        else { System.out.println("Use on or off"); return; }
        invoker.run(c);
    }

    private void adjustDevice() {
        System.out.print("Device name: ");
        String name = scanner.nextLine().trim();
        Device d = controller.getDevice(name);
        if (d == null) {
            System.out.println("Unknown device");
            return;
        }
        System.out.print("Value: ");
        int v;
        try {
            v = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Not a number");
            return;
        }
        invoker.run(new SetValueCommand(d, v));
    }

    private void createRoom() {
        System.out.print("Room name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name must not be blank");
            return;
        }
        if (controller.getDevice(name) != null) {
            System.out.println("Name already used");
            return;
        }
        DeviceGroup g = new DeviceGroup(name);
        controller.registerDevice(g);
        System.out.println("Created room '" + name + "'");
    }

    private void addToRoom() {
        System.out.print("Room: ");
        String roomName = scanner.nextLine().trim();
        Device room = controller.getDevice(roomName);
        if (!(room instanceof DeviceGroup)) {
            System.out.println("Unknown room");
            return;
        }
        System.out.print("Device: ");
        String devName = scanner.nextLine().trim();
        Device dev = controller.getDevice(devName);
        if (dev == null) {
            System.out.println("Unknown device");
            return;
        }
        ((DeviceGroup) room).add(dev);
        System.out.println("Added.");
    }

    private void roomToggle() {
        System.out.print("Room: ");
        String roomName = scanner.nextLine().trim();
        Device room = controller.getDevice(roomName);
        if (!(room instanceof DeviceGroup)) {
            System.out.println("Unknown room");
            return;
        }
        System.out.print("(on/off): ");
        String v = scanner.nextLine().trim().toLowerCase();
        Command c;
        if (v.equals("on")) c = new TurnOnCommand(room);
        else if (v.equals("off")) c = new TurnOffCommand(room);
        else { System.out.println("Use on or off"); return; }
        invoker.run(c);
    }

    private void undoLast() {
        invoker.undoLast();
    }

    private void homeStatus() {
        if (controller.listDevices().isEmpty()) {
            System.out.println("No devices");
            return;
        }
        for (Device d : controller.listDevices()) {
            System.out.println(d.getStatus());
        }
    }
}