package smarthome.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

final class Theme {
    static final Color HEADER = new Color(35, 32, 32);
    static final Color ORANGE = new Color(255, 151, 31);
    static final Color DARK = new Color(70, 66, 66);
    static final Color SURFACE = new Color(249, 248, 248);
    static final Color PANEL = new Color(244, 242, 242);
    static final Color BORDER = new Color(222, 217, 217);
    static final Color TEXT = new Color(38, 34, 34);
    static final Color MUTED = new Color(98, 91, 91);
    static final Color ON = new Color(30, 122, 52);
    static final Color ON_SURFACE = new Color(240, 250, 241);
    static final Color ON_BORDER = new Color(140, 204, 150);
    private static final Color DISABLED = new Color(232, 229, 229);
    private static final Color FOCUS = new Color(33, 112, 214);

    enum Style { PRIMARY, DARK, OUTLINE, HEADER }

    enum Glyph { LIGHT, THERMOSTAT, SPEAKER, FAN, PLUS, UNDO }

    private Theme() {
    }

    static Font font(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    static Font heading(int size) {
        return new Font("Segoe UI Semibold", Font.PLAIN, size);
    }

    static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.putClientProperty("html.disable", true);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static JButton button(String text, Style style, Runnable action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                paintButton(this, style, g);
                super.paintComponent(g);
            }
        };
        prepare(button);
        button.setFont(heading(13));
        button.setForeground(foreground(style));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setIconTextGap(7);
        button.addPropertyChangeListener("enabled",
                e -> button.setForeground(button.isEnabled() ? foreground(style) : MUTED));
        button.addActionListener(e -> action.run());
        return button;
    }

    static JToggleButton tab(String text, Runnable action) {
        JToggleButton tab = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                paintButton(this, isSelected() ? Style.DARK : Style.OUTLINE, g);
                setForeground(isSelected() ? Color.WHITE : TEXT);
                super.paintComponent(g);
            }
        };
        prepare(tab);
        tab.setFont(heading(13));
        tab.setBorder(BorderFactory.createEmptyBorder(7, 4, 7, 4));
        tab.addActionListener(e -> action.run());
        return tab;
    }

    static JToggleButton toggle(boolean selected, String name, Runnable action) {
        JToggleButton toggle = new JToggleButton("", selected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = smooth(g);
                int y = (getHeight() - 24) / 2;
                g2.setColor(isSelected() ? ON : new Color(138, 131, 131));
                if (getModel().isRollover()) {
                    g2.setColor(g2.getColor().darker());
                }
                g2.fillRoundRect(3, y, 44, 24, 24, 24);
                g2.setColor(Color.WHITE);
                g2.fillOval(isSelected() ? 26 : 6, y + 3, 18, 18);
                if (isFocusOwner()) {
                    g2.setColor(FOCUS);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, y - 2, 48, 28, 28, 28);
                }
                g2.dispose();
            }
        };
        prepare(toggle);
        toggle.setBorder(null);
        toggle.setPreferredSize(new Dimension(50, 30));
        toggle.setToolTipText(name);
        toggle.getAccessibleContext().setAccessibleName(name);
        toggle.addActionListener(e -> action.run());
        return toggle;
    }

    static JPanel card(Color fill, Color border, boolean dashed) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = smooth(g);
                int width = getWidth() - 1;
                int height = getHeight() - 5;
                if (!dashed) {
                    g2.setColor(new Color(40, 25, 25, 9));
                    for (int spread = 0; spread < 4; spread++) {
                        g2.fillRoundRect(spread, 5 - spread, width - 2 * spread, height + 2 * spread - 2, 18, 18);
                    }
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, width, height, 16, 16);
                g2.setColor(border);
                if (dashed) {
                    g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                            1f, new float[] {6f, 5f}, 0f));
                }
                g2.drawRoundRect(0, 0, width, height, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 18, 16));
        return card;
    }

    static Icon icon(Glyph glyph, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = smooth(g);
                g2.translate(x, y);
                g2.scale(size / 24.0, size / 24.0);
                g2.setColor(c.isEnabled() ? color : MUTED);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                draw(glyph, g2);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    static BufferedImage logo(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = smooth(image.getGraphics());
        g2.scale(size / 24.0, size / 24.0);
        g2.setColor(ORANGE);
        g2.fillRoundRect(0, 0, 24, 24, 8, 8);
        g2.setColor(HEADER);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D house = new Path2D.Double();
        house.moveTo(5, 11.5);
        house.lineTo(12, 5.5);
        house.lineTo(19, 11.5);
        house.moveTo(7, 11);
        house.lineTo(7, 18);
        house.lineTo(17, 18);
        house.lineTo(17, 11);
        g2.draw(house);
        g2.fillOval(10, 12, 4, 4);
        g2.dispose();
        return image;
    }

    private static void draw(Glyph glyph, Graphics2D g2) {
        switch (glyph) {
            case LIGHT:
                g2.drawArc(5, 2, 14, 14, -55, 290);
                g2.drawLine(8, 14, 9, 18);
                g2.drawLine(16, 14, 15, 18);
                g2.drawLine(9, 18, 15, 18);
                g2.drawLine(10, 22, 14, 22);
                break;
            case THERMOSTAT:
                g2.drawRoundRect(9, 2, 6, 15, 6, 6);
                g2.drawOval(7, 13, 10, 9);
                g2.drawLine(12, 8, 12, 17);
                break;
            case SPEAKER:
                g2.drawRoundRect(5, 2, 14, 20, 5, 5);
                g2.drawOval(8, 11, 8, 8);
                g2.drawLine(12, 6, 12, 6);
                break;
            case FAN:
                g2.drawOval(10, 10, 4, 4);
                for (int blade = 0; blade < 3; blade++) {
                    g2.drawOval(9, 2, 6, 8);
                    g2.rotate(Math.PI * 2 / 3, 12, 12);
                }
                break;
            case PLUS:
                g2.drawLine(12, 5, 12, 19);
                g2.drawLine(5, 12, 19, 12);
                break;
            case UNDO:
                g2.drawLine(4, 9, 15, 9);
                g2.drawArc(10, 9, 10, 10, -90, 180);
                g2.drawLine(15, 19, 9, 19);
                g2.drawLine(4, 9, 9, 4);
                g2.drawLine(4, 9, 9, 14);
                break;
            default:
                break;
        }
    }

    private static void prepare(AbstractButton button) {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static Color foreground(Style style) {
        return style == Style.PRIMARY ? HEADER : style == Style.OUTLINE ? TEXT : Color.WHITE;
    }

    private static void paintButton(AbstractButton button, Style style, Graphics g) {
        Color fill = style == Style.PRIMARY ? ORANGE : style == Style.DARK ? DARK
                : style == Style.HEADER ? HEADER : Color.WHITE;
        if (button.getModel().isPressed()) {
            fill = mix(fill, style == Style.HEADER ? Color.WHITE : Color.BLACK, 0.18);
        } else if (button.getModel().isRollover()) {
            fill = mix(fill, style == Style.OUTLINE ? Color.BLACK : Color.WHITE, 0.08);
        }
        Graphics2D g2 = smooth(g);
        int width = button.getWidth() - 1;
        int height = button.getHeight() - 1;
        g2.setColor(button.isEnabled() ? fill : DISABLED);
        g2.fillRoundRect(0, 0, width, height, 12, 12);
        if (style == Style.OUTLINE || style == Style.HEADER) {
            g2.setColor(style == Style.HEADER ? new Color(130, 122, 122) : BORDER);
            g2.drawRoundRect(0, 0, width, height, 12, 12);
        }
        if (button.isFocusOwner()) {
            g2.setColor(FOCUS);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, width - 2, height - 2, 11, 11);
        }
        g2.dispose();
    }

    private static Color mix(Color a, Color b, double amount) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * amount),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * amount),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * amount));
    }

    private static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }
}
