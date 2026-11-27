package smarthome.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import smarthome.behavioral.Command;
import smarthome.behavioral.CommandInvoker;
import smarthome.behavioral.SetValueCommand;
import smarthome.behavioral.TurnOffCommand;
import smarthome.behavioral.TurnOnCommand;
import smarthome.creational.CentralController;
import smarthome.creational.DeviceFactory;
import smarthome.model.AbstractDevice;
import smarthome.model.Device;
import smarthome.model.DeviceType;
import smarthome.model.Light;
import smarthome.model.Speaker;
import smarthome.model.Thermostat;
import smarthome.structural.DeviceGroup;
import smarthome.structural.OldFanAdapter;

public class SmartHomeWindow extends JFrame {
    private static final int CARD_WIDTH = 250;
    private static final int GAP = 14;

    private enum Tab { ACTIVITY, HISTORY, TREE }

    private final CentralController controller = CentralController.getInstance();
    private final CommandInvoker invoker = new CommandInvoker();
    private final Set<Device> expanded = new HashSet<>();
    private final ContentPanel content = new ContentPanel();
    private final JTextArea log = new JTextArea();
    private final JButton sampleButton = Theme.button("Load sample home", Theme.Style.HEADER, this::loadSampleHome);
    private final JButton undoButton = Theme.button("Undo last", Theme.Style.DARK, this::undo);
    private final JLabel undoHint = Theme.label(" ", Theme.font(Font.PLAIN, 12), Theme.MUTED);
    private Tab tab = Tab.ACTIVITY;
    private int columns = 3;

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new SmartHomeWindow().setVisible(true);
        });
    }

    SmartHomeWindow() {
        super("Smart Home");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(Theme.logo(64));
        setSize(1200, 780);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(null);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int fit = Math.max(1, Math.min(4, (e.getComponent().getWidth() - 56) / CARD_WIDTH));
                if (fit != columns) {
                    columns = fit;
                    refresh();
                }
            }
        });

        add(header(), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(sidebar(), BorderLayout.EAST);
        refresh();
    }

    private JPanel header() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 16));

        JLabel title = Theme.label("Smart Home", Theme.heading(21), Color.WHITE);
        title.setIcon(new ImageIcon(Theme.logo(36)));
        title.setIconTextGap(14);
        header.add(title, BorderLayout.WEST);

        JButton addGroup = Theme.button("Add group", Theme.Style.PRIMARY, this::addGroup);
        addGroup.setIcon(Theme.icon(Theme.Glyph.PLUS, 14, Theme.HEADER));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 1));
        actions.setOpaque(false);
        actions.add(sampleButton);
        actions.add(addGroup);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel sidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 12));
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBackground(Theme.PANEL);
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 16, 16, 16));

        JPanel tabs = new JPanel(new GridLayout(1, 3, 6, 0));
        tabs.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (Tab each : Tab.values()) {
            String text = each.name().charAt(0) + each.name().substring(1).toLowerCase();
            JToggleButton button = Theme.tab(text, () -> {
                tab = each;
                refreshLog();
            });
            button.setSelected(each == tab);
            group.add(button);
            tabs.add(button);
        }
        sidebar.add(tabs, BorderLayout.NORTH);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(Theme.font(Font.PLAIN, 13));
        log.setForeground(Theme.TEXT);
        log.setSelectionColor(Theme.ORANGE);
        log.setSelectedTextColor(Theme.HEADER);
        log.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        sidebar.add(scroll, BorderLayout.CENTER);

        undoButton.setIcon(Theme.icon(Theme.Glyph.UNDO, 15, Color.WHITE));
        boolean single = controller == CentralController.getInstance();
        undoButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        undoButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, undoButton.getPreferredSize().height));
        JPanel bottom = section();
        bottom.add(undoButton);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(undoHint);
        bottom.add(Box.createVerticalStrut(18));
        bottom.add(Theme.label("One controller: CentralController @"
                + Integer.toHexString(System.identityHashCode(controller)),
                Theme.font(Font.PLAIN, 12), Theme.MUTED));
        bottom.add(Theme.label("getInstance() == getInstance() is " + single,
                Theme.font(Font.PLAIN, 12), Theme.MUTED));
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private void refresh() {
        Component focused = getFocusOwner();
        String focusName = focused == null ? null : focused.getName();

        content.removeAll();
        Set<Device> grouped = grouped();
        List<Device> loose = new ArrayList<>();
        for (Device device : controller.listDevices()) {
            if (grouped.contains(device)) {
                continue;
            }
            if (device instanceof DeviceGroup) {
                content.add(groupSection((DeviceGroup) device, null));
                content.add(Box.createVerticalStrut(30));
            } else {
                loose.add(device);
            }
        }
        if (controller.listDevices().isEmpty()) {
            content.add(emptyState());
        } else {
            content.add(looseSection(loose));
        }
        content.add(Box.createVerticalGlue());

        sampleButton.setEnabled(controller.listDevices().isEmpty());
        sampleButton.setToolTipText(sampleButton.isEnabled() ? null : "Available while the home is empty");
        refreshLog();

        content.revalidate();
        content.repaint();
        if (focusName != null) {
            restoreFocus(content, focusName);
        }
    }

    private void refreshLog() {
        List<String> lines = new ArrayList<>();
        String empty;
        if (tab == Tab.ACTIVITY) {
            lines.addAll(controller.getEventLog());
            Collections.reverse(lines);
            empty = "No device changes yet.\n\nSwitch a device on and the controller's notifications appear here, newest first.";
        } else if (tab == Tab.HISTORY) {
            for (Command command : invoker.getHistory()) {
                lines.add(command.describe());
            }
            empty = "No commands yet.\n\nEvery switch and level change is recorded here, newest first.";
        } else {
            Set<Device> grouped = grouped();
            for (Device device : controller.listDevices()) {
                if (!grouped.contains(device)) {
                    lines.add(device.getStatus());
                }
            }
            empty = "No devices yet.\n\nRooms and the devices inside them appear here as a tree.";
        }
        log.setText(lines.isEmpty() ? empty : String.join(tab == Tab.TREE ? "\n\n" : "\n", lines));
        log.setCaretPosition(0);

        Command last = invoker.getHistory().peek();
        undoButton.setEnabled(last != null);
        undoHint.setText(last == null ? "Nothing to undo" : "Next undo: " + last.describe());
        undoHint.setToolTipText(last == null ? null : last.describe());
    }

    private Set<Device> grouped() {
        Set<Device> grouped = new HashSet<>();
        for (Device device : controller.listDevices()) {
            if (device instanceof DeviceGroup) {
                grouped.addAll(((DeviceGroup) device).getChildren());
            }
        }
        return grouped;
    }

    private JPanel emptyState() {
        JPanel section = section();
        section.add(Theme.label("Your home is empty", Theme.heading(20), Theme.TEXT));
        section.add(Box.createVerticalStrut(6));
        section.add(Theme.label("Add a device or a group, or load a sample home to try every control.",
                Theme.font(Font.PLAIN, 14), Theme.MUTED));
        section.add(Box.createVerticalStrut(18));
        JButton add = Theme.button("Add device", Theme.Style.PRIMARY, () -> addDevice(null));
        add.setIcon(Theme.icon(Theme.Glyph.PLUS, 14, Theme.HEADER));
        add.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(add);
        return section;
    }

    private JPanel groupSection(DeviceGroup group, DeviceGroup parent) {
        List<Device> devices = new ArrayList<>();
        List<DeviceGroup> subgroups = new ArrayList<>();
        for (Device child : group.getChildren()) {
            if (child instanceof DeviceGroup) {
                subgroups.add((DeviceGroup) child);
            } else {
                devices.add(child);
            }
        }
        List<Device> leaves = new ArrayList<>();
        collect(group, leaves);
        long on = leaves.stream().filter(Device::isOn).count();
        String summary = leaves.isEmpty() ? "No devices" : on + " of " + leaves.size() + " on";
        if (parent != null) {
            summary = "Inside " + parent.getName() + ", " + summary.toLowerCase();
        }

        JPanel titles = section();
        titles.add(Theme.label(group.getName(), Theme.heading(parent == null ? 20 : 17), Theme.TEXT));
        titles.add(Theme.label(summary, Theme.font(Font.PLAIN, 13), on > 0 ? Theme.ON : Theme.MUTED));

        JPanel actions = row(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton allOn = Theme.button("All on", Theme.Style.OUTLINE, () -> execute(new TurnOnCommand(group)));
        JButton allOff = Theme.button("All off", Theme.Style.OUTLINE, () -> execute(new TurnOffCommand(group)));
        allOn.setName("on:" + group.getName());
        allOff.setName("off:" + group.getName());
        allOn.setEnabled(on < leaves.size());
        allOff.setEnabled(on > 0);
        actions.add(allOn);
        actions.add(allOff);

        JPanel bar = row(new BorderLayout(12, 0));
        bar.add(titles, BorderLayout.CENTER);
        bar.add(actions, BorderLayout.EAST);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, GAP - 8));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, bar.getPreferredSize().height));

        JPanel section = section();
        section.add(bar);
        section.add(Box.createVerticalStrut(12));
        section.add(grid(devices, addCard(group)));
        for (DeviceGroup subgroup : subgroups) {
            JPanel nested = groupSection(subgroup, group);
            nested.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 0));
            section.add(Box.createVerticalStrut(10));
            section.add(nested);
        }
        return section;
    }

    private void collect(DeviceGroup group, List<Device> leaves) {
        for (Device child : group.getChildren()) {
            if (child instanceof DeviceGroup) {
                collect((DeviceGroup) child, leaves);
            } else {
                leaves.add(child);
            }
        }
    }

    private JPanel looseSection(List<Device> devices) {
        JPanel section = section();
        section.add(Theme.label("No group", Theme.heading(20), Theme.TEXT));
        section.add(Box.createVerticalStrut(12));
        section.add(grid(devices, addCard(null)));
        return section;
    }

    private JPanel section() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        return section;
    }

    private JPanel row(LayoutManager layout) {
        JPanel row = new JPanel(layout);
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    private JPanel grid(List<Device> devices, JComponent last) {
        JPanel grid = row(new GridBagLayout());
        List<JComponent> cards = new ArrayList<>();
        for (Device device : devices) {
            cards.add(deviceCard((AbstractDevice) device));
        }
        cards.add(last);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, GAP, GAP);
        c.weightx = 1;
        for (int i = 0; i < Math.max(cards.size(), columns); i++) {
            c.gridx = i % columns;
            c.gridy = i / columns;
            Component cell = i < cards.size() ? cards.get(i) : Box.createHorizontalGlue();
            c.fill = cell == last ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
            cell.setPreferredSize(new Dimension(200, cell.getPreferredSize().height));
            grid.add(cell, c);
        }
        return grid;
    }

    private JPanel deviceCard(AbstractDevice device) {
        boolean on = device.isOn();
        JPanel card = Theme.card(on ? Theme.ON_SURFACE : Theme.SURFACE, on ? Theme.ON_BORDER : Theme.BORDER, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel names = section();
        JLabel name = Theme.label(device.getName(), Theme.heading(15), Theme.TEXT);
        name.setToolTipText(device.getName());
        names.add(name);
        names.add(Theme.label(typeName(device), Theme.font(Font.PLAIN, 12), Theme.MUTED));

        JToggleButton power = Theme.toggle(on, (on ? "Turn off " : "Turn on ") + device.getName(),
                () -> execute(on ? new TurnOffCommand(device) : new TurnOnCommand(device)));
        power.setName("power:" + device.getName());

        JPanel top = row(new BorderLayout(10, 0));
        top.add(new JLabel(Theme.icon(glyph(device), 30, on ? Theme.ON : Theme.MUTED)), BorderLayout.WEST);
        top.add(names, BorderLayout.CENTER);
        top.add(power, BorderLayout.EAST);
        card.add(top);
        card.add(Box.createVerticalStrut(14));

        boolean open = expanded.contains(device);
        JButton adjustButton = Theme.button(open ? "Close" : "Adjust", Theme.Style.DARK, () -> {
            if (!expanded.remove(device)) {
                expanded.add(device);
            }
            refresh();
        });
        adjustButton.setName("adjust:" + device.getName());

        JPanel readings = section();
        readings.add(Theme.label(on ? "On" : "Off", Theme.heading(13), on ? Theme.ON : Theme.MUTED));
        readings.add(Theme.label(reading(device), Theme.font(Font.PLAIN, 13), Theme.TEXT));
        JPanel middle = row(new BorderLayout(10, 0));
        middle.add(readings, BorderLayout.CENTER);
        middle.add(adjustButton, BorderLayout.EAST);
        card.add(middle);

        if (open) {
            JTextField field = new JTextField(String.valueOf(device.getValue()));
            field.setName("value:" + device.getName());
            field.setFont(Theme.font(Font.PLAIN, 14));
            field.setForeground(Theme.TEXT);
            field.setCaretColor(Theme.TEXT);
            field.setSelectionColor(Theme.ORANGE);
            field.setSelectedTextColor(Theme.HEADER);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(138, 131, 131)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));
            field.getAccessibleContext().setAccessibleName(device.getValueLabel());
            Runnable apply = () -> setValue(device, field.getText());
            field.addActionListener(e -> apply.run());

            card.add(Box.createVerticalStrut(14));
            card.add(Theme.label(device.getValueLabel(), Theme.font(Font.PLAIN, 12), Theme.MUTED));
            card.add(Box.createVerticalStrut(5));
            JPanel adjust = row(new BorderLayout(8, 0));
            adjust.add(field, BorderLayout.CENTER);
            adjust.add(Theme.button("Set", Theme.Style.PRIMARY, apply), BorderLayout.EAST);
            card.add(adjust);
        }
        return card;
    }

    private String typeName(Device device) {
        if (device instanceof OldFanAdapter) {
            return "Fan (adapted)";
        }
        return device.getClass().getSimpleName();
    }

    private Theme.Glyph glyph(Device device) {
        if (device instanceof Light) {
            return Theme.Glyph.LIGHT;
        }
        if (device instanceof Thermostat) {
            return Theme.Glyph.THERMOSTAT;
        }
        return device instanceof Speaker ? Theme.Glyph.SPEAKER : Theme.Glyph.FAN;
    }

    private String reading(AbstractDevice device) {
        if (device instanceof Thermostat) {
            return "Target " + device.getValue() + " °C";
        }
        if (device instanceof OldFanAdapter) {
            return "Speed " + device.getValue() / 25 + " of 3";
        }
        return (device instanceof Light ? "Brightness " : "Volume ") + device.getValue() + "%";
    }

    private JPanel addCard(DeviceGroup group) {
        JPanel card = Theme.card(Color.WHITE, new Color(176, 169, 169), true);
        card.setLayout(new GridBagLayout());
        JPanel buttons = row(new GridLayout(0, 1, 0, 8));
        JButton add = Theme.button("Add device", Theme.Style.PRIMARY, () -> addDevice(group));
        add.setIcon(Theme.icon(Theme.Glyph.PLUS, 14, Theme.HEADER));
        buttons.add(add);
        if (group != null) {
            buttons.add(Theme.button("Import existing", Theme.Style.OUTLINE, () -> importInto(group)));
        }
        card.add(buttons);
        card.setMinimumSize(new Dimension(200, 118));
        card.setPreferredSize(new Dimension(200, 118));
        return card;
    }

    private void restoreFocus(Container parent, String name) {
        for (Component child : parent.getComponents()) {
            if (name.equals(child.getName())) {
                child.requestFocusInWindow();
            } else if (child instanceof Container) {
                restoreFocus((Container) child, name);
            }
        }
    }

    private void execute(Command command) {
        invoker.run(command);
        refresh();
    }

    private void undo() {
        invoker.undoLast();
        refresh();
    }

    private void setValue(Device device, String text) {
        try {
            execute(new SetValueCommand(device, Integer.parseInt(text.trim())));
        } catch (NumberFormatException e) {
            showError("Enter a whole number.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void addDevice(DeviceGroup group) {
        JTextField name = new JTextField(18);
        JComboBox<DeviceType> type = new JComboBox<>(DeviceType.values());
        type.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean selected, boolean focused) {
                String text = value.toString().charAt(0) + value.toString().substring(1).toLowerCase();
                return super.getListCellRendererComponent(list, text, index, selected, focused);
            }
        });
        Object[] form = {"Name", name, "Type", type};
        String title = group == null ? "Add device" : "Add device to " + group.getName();
        if (!confirm(form, title)) {
            return;
        }
        try {
            Device device = DeviceFactory.createDevice((DeviceType) type.getSelectedItem(), name.getText());
            controller.registerDevice(device);
            if (group != null) {
                group.add(device);
            }
            refresh();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void addGroup() {
        JTextField name = new JTextField(18);
        if (!confirm(new Object[] {"Room or zone name", name}, "Add group")) {
            return;
        }
        try {
            controller.registerDevice(new DeviceGroup(name.getText()));
            refresh();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void importInto(DeviceGroup group) {
        Set<Device> grouped = grouped();
        JComboBox<Device> choices = new JComboBox<>();
        for (Device device : controller.listDevices()) {
            boolean cycle = device == group
                    || device instanceof DeviceGroup && ((DeviceGroup) device).contains(group);
            if (!grouped.contains(device) && !cycle) {
                choices.addItem(device);
            }
        }
        if (choices.getItemCount() == 0) {
            showError("Everything is already in a group. Add a new device or group first.");
            return;
        }
        choices.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean selected, boolean focused) {
                String text = ((Device) value).getName() + (value instanceof DeviceGroup ? " (group)" : "");
                return super.getListCellRendererComponent(list, text, index, selected, focused);
            }
        });
        if (confirm(new Object[] {"Device or group", choices}, "Import into " + group.getName())) {
            group.add((Device) choices.getSelectedItem());
            refresh();
        }
    }

    private void loadSampleHome() {
        try {
            Device lamp = create(DeviceType.LIGHT, "Lamp");
            Device speaker = create(DeviceType.SPEAKER, "Speaker");
            Device thermostat = create(DeviceType.THERMOSTAT, "Thermostat");
            create(DeviceType.FAN, "Old Fan");
            DeviceGroup living = new DeviceGroup("Living Room");
            living.add(lamp);
            living.add(speaker);
            controller.registerDevice(living);
            DeviceGroup floor = new DeviceGroup("Ground Floor");
            floor.add(living);
            floor.add(thermostat);
            controller.registerDevice(floor);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
        refresh();
    }

    private Device create(DeviceType type, String name) {
        Device device = DeviceFactory.createDevice(type, name);
        controller.registerDevice(device);
        return device;
    }

    private boolean confirm(Object[] form, String title) {
        return JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Smart Home", JOptionPane.WARNING_MESSAGE);
    }

    private static class ContentPanel extends JPanel implements Scrollable {
        ContentPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24 - GAP));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return visible.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
