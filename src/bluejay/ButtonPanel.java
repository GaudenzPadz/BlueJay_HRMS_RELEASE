package bluejay;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class ButtonPanel extends JPanel {

    private Color defaultColor;
    private Color hoverColor;
    private Color pressedColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    /**
     * @wbp.parser.constructor
     */
    public ButtonPanel(Color backgroundColor, Icon icon, String title, String description) {
        init(backgroundColor);
        setLayout(new MigLayout("center", "[grow,fill]", "[center][center][center]"));

        JLabel iconLabel = new JLabel("", JLabel.CENTER);
        iconLabel.setIcon(icon);
        add(iconLabel, "cell 0 0,growx,aligny center");

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titleLabel, "cell 0 1,growx,aligny top");

        JLabel descriptionLabel = new JLabel(description, JLabel.CENTER);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        add(descriptionLabel, "cell 0 2,growx,aligny top");

        setLabelTextColor(this, Color.WHITE); // Update all JLabel text colors
    }
  
    //custome clickable panel for side panel
    public ButtonPanel(Color bgColor, String buttonLabel, ImageIcon buttonIcon) {
        init(bgColor);
        setLayout(new MigLayout("", "[pref!][pref!,fill]", "[pref!,grow,fill]"));

        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setIcon(new ImageIcon(buttonIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH)));
        add(btn, "cell 0 0,alignx left,aligny top");

        JLabel label = new JLabel(buttonLabel);
        add(label, "cell 1 0 2 1");
    }

    private void init(Color backgroundColor) {
        this.defaultColor = backgroundColor;
        this.hoverColor = defaultColor.brighter();
        this.pressedColor = new Color(200, 200, 200);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    private static void setLabelTextColor(Container container, Color color) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setForeground(color);
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isPressed) {
            g.setColor(pressedColor);
        } else if (isHovered) {
            g.setColor(hoverColor);
        } else {
            g.setColor(defaultColor);
        }
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}