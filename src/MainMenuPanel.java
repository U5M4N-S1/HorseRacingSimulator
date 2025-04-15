import javax.swing.*;
import java.awt.*;

class MainMenuPanel extends JPanel {
    private MasterPanel master;
    private Image backgroundImage;

    public MainMenuPanel(MasterPanel master) {
        this.master = master;

        backgroundImage = new ImageIcon("assets/background.png").getImage();

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Horse Racing Simulator", 270, 190);
            }
        };
        topPanel.setPreferredSize(new Dimension(900, 200));
        topPanel.setOpaque(false);
        add(topPanel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setPreferredSize(new Dimension(900, 400));
        buttonPanel.setOpaque(false);

        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);

        JButton playBtn = new JButton("Play");
        JButton customiseBtn = new JButton("Customise");
        JButton exitBtn = new JButton("Exit");

        playBtn.setBounds(350, 100, 200, 40);
        customiseBtn.setBounds(350, 150, 200, 40);
        exitBtn.setBounds(350, 200, 200, 40);

        playBtn.setFont(buttonFont);
        customiseBtn.setFont(buttonFont);
        exitBtn.setFont(buttonFont);

        playBtn.setFocusPainted(false);
        customiseBtn.setFocusPainted(false);
        exitBtn.setFocusPainted(false);

        playBtn.addActionListener(e -> master.startRace());
        customiseBtn.addActionListener(e -> master.switchTo("customise"));
        exitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(playBtn);
        buttonPanel.add(customiseBtn);
        buttonPanel.add(exitBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
