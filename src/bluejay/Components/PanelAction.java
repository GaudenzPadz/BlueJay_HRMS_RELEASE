package bluejay.Components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelAction extends JPanel {

    private final ActionButton cmdEdit;
    private final ActionButton cmdDelete;
    private final ActionButton cmdView;

    public PanelAction() {
        cmdEdit = new ActionButton("Edit");
        cmdDelete = new ActionButton("Delete");
        cmdView = new ActionButton("View");

        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        add(cmdEdit);
        add(cmdView);
        add(cmdDelete);
    }

    public void initEvent(TableActionEvent event, int row) {
        cmdEdit.addActionListener(ae -> event.onEdit(row));
        cmdDelete.addActionListener(ae -> event.onDelete(row));
        cmdView.addActionListener(ae -> event.onView(row));
    }
}

class ActionButton extends JButton {

    private boolean isHovered = false;
    private boolean isPressed = false;
    private static final Color DEFAULT_COLOR = Color.decode("#002C4B");
    private static final Color HOVER_COLOR = DEFAULT_COLOR.brighter();
    private static final Color PRESSED_COLOR = new Color(200, 200, 200);

    public ActionButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        
        setBackground(DEFAULT_COLOR);
        setForeground(Color.WHITE);
        
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

    @Override
    protected void paintComponent(Graphics g) {
        if (isPressed) {
            g.setColor(PRESSED_COLOR);
        } else if (isHovered) {
            g.setColor(HOVER_COLOR);
        } else {
            g.setColor(DEFAULT_COLOR);
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}