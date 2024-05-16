package bluejay.Components;

import javax.swing.*;
import java.awt.*;

public class ToastMessage extends JWindow {
    int miliseconds;

    public ToastMessage(String toastString, int time) {
        this.miliseconds = time;
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(0,0,0,0));
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        JLabel toastLabel = new JLabel(toastString);
        toastLabel.setForeground(Color.WHITE);
        setBounds(100, 100, 300, 50);
        add(toastLabel);
        setVisible(false);
        new Thread(() -> {
            try {
                Thread.sleep(time);
                dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void showToast() {
        setOpacity(1);
        setVisible(true);
    }
}