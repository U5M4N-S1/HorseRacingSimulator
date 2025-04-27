import javax.swing.*;
import java.awt.*;

class MainMenuPanel extends JPanel {
    private MasterPanel master;
    private Image backgroundImage;

    public MainMenuPanel(MasterPanel master) {
        this.master = master;

        setLayout(new BorderLayout());
        backgroundImage = new ImageIcon("assets/background.png").getImage();
        // Top Panel (title text)
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.setColor(Color.BLACK); // Set text color to black
                g.drawString("Horse Racing Simulator", 270, 190);
            }
        };
        topPanel.setPreferredSize(new Dimension(900, 200));
        topPanel.setOpaque(false); // This panel will not cover the background
        add(topPanel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setPreferredSize(new Dimension(900, 400));
        buttonPanel.setOpaque(false); // Make this panel transparent to see the background

        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);

        JButton playBtn = new JButton("Play");
        JButton customiseBtn = new JButton("Customise");
        JButton bettingBtn = new JButton("Betting");
        JButton exitBtn = new JButton("Exit");

        // Set button sizes and positions
        playBtn.setBounds(350, 100, 200, 40);
        customiseBtn.setBounds(350, 150, 200, 40);
        bettingBtn.setBounds(350, 200, 200, 40);
        exitBtn.setBounds(350, 250, 200, 40);

        // Set font for buttons
        playBtn.setFont(buttonFont);
        customiseBtn.setFont(buttonFont);
        bettingBtn.setFont(buttonFont);
        exitBtn.setFont(buttonFont);

        // Remove focus from buttons
        playBtn.setFocusPainted(false);
        customiseBtn.setFocusPainted(false);
        bettingBtn.setFocusPainted(false);
        exitBtn.setFocusPainted(false);

        // Button actions
        playBtn.addActionListener(e -> master.startRace());
        customiseBtn.addActionListener(e -> master.switchTo("customise"));
        bettingBtn.addActionListener(e -> master.switchTo("betting")); // Switch to betting panel
        exitBtn.addActionListener(e -> System.exit(0));

        // Add buttons to button panel
        buttonPanel.add(playBtn);
        buttonPanel.add(customiseBtn);
        buttonPanel.add(bettingBtn); // Add the betting button here
        buttonPanel.add(exitBtn);

        // Add button panel to main panel
        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
