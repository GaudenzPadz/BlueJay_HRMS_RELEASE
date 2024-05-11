package bluejay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatClientProperties;

public class SplashScreen extends JFrame {
    private static final long serialVersionUID = 13123231231L;
	private JLabel statusLabel;
    private JProgressBar progressBar;
    private JPanel bottomPanel;

    public SplashScreen() {
        setTitle("Loading...");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);

        ImageIcon backgroundImage = new ImageIcon("resource/images/blueJCover.png");
      //  Image originalImage = backgroundImage.getImage().getScaledInstance(-1, 300, Image.SCALE_FAST); // Scaled height

        JPanel backgroundPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (backgroundImage != null) {
                    g.drawImage( backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
                }
            }
        };

        backgroundPanel.putClientProperty(FlatClientProperties.STYLE, "arc:20;");

        bottomPanel = new JPanel(new BorderLayout(0, 0));
        bottomPanel.setOpaque(false); // Transparency
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.GREEN);
        progressBar.setOpaque(false); // Transparent background
        progressBar.setBackground(null);
        progressBar.setBorderPainted(false); // Transparent border
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        statusLabel = new JLabel("Loading...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        statusLabel.setOpaque(false); // Transparent background
        statusLabel.setForeground(Color.BLACK); // Set text color
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        getContentPane().add(backgroundPanel);

        setSize(600, 300);
        setLocationRelativeTo(null);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void updateProgress(int value) {
        progressBar.setValue(value);
    }
}
