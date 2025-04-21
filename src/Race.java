import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.lang.Math;
import javax.swing.*;
import java.awt.*;

public class Race extends JPanel
{
    private ArrayList<Horse> horses = new ArrayList<>();
    private int raceLength;
    private int width = 900;
    public String track = "oval"; // "line", "oval", or "figure8"
    public String weather = "dry"; // "clear", "icy" or "muddy"
    boolean finished = false;
    private String winnerText = "";
    private JButton restartButton;

    public Race()
    {
        setPreferredSize(new Dimension(900, 600));
        setBackground(new Color(74, 138, 70));
        raceLength = 20;

        restartButton = new JButton("Restart Race");
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> startRace());
        this.setLayout(null);
        restartButton.setBounds(375, 520, 150, 30);
        add(restartButton);
    }

    public void addHorse(Horse theHorse)
    {
        horses.add(theHorse);
    }

    public void startRace()
    {
        finished = false;
        winnerText = "";
        restartButton.setVisible(false);
        for (Horse horse : horses) {
            horse.goBackToStart();
        }

        new Thread(() -> {
            while (!finished) {
                boolean allFallen = true;
                for (Horse theHorse : horses) {
                    moveHorse(theHorse);
                    if (!theHorse.hasFallen() && raceWonBy(theHorse)) {
                        winnerText = "Winner: " + theHorse.getName();
                        theHorse.setConfidence(theHorse.getConfidence() + 0.05);
                        finished = true;
                        break;
                    }
                    if (!theHorse.hasFallen()) {
                        allFallen = false;
                    }
                }
                if (allFallen && !finished) {
                    winnerText = "All horses have fallen!";
                    finished = true;
                }
                repaint();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception e) {}
            }
            restartButton.setVisible(true);
        }).start();
    }

    private void moveHorse(Horse theHorse)
    {
        if (!theHorse.hasFallen())
        {
            if (Math.random() < theHorse.getConfidence())
            {
                theHorse.moveForward();
            }
            if (Math.random() < (0.1 * theHorse.getConfidence() * theHorse.getConfidence()))
            {
                theHorse.fall();
            }
        }
    }
        
    private boolean raceWonBy(Horse theHorse)
    {
        return theHorse.getDistanceTravelled() >= raceLength;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

        // Draw winner text
        if (!winnerText.isEmpty()) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(winnerText);
            g.drawString(winnerText, (width - textWidth) / 2, 100);
        }
    }

    public void draw(Graphics g) {
        if (track.equals("line")) {
            drawLineTrack(g);
        } else if (track.equals("oval")) {
            drawOvalTrack(g);
        } else if (track.equals("figure8")) {
            drawFigure8Track(g);
        }
    }

    private void drawLineTrack(Graphics g) {
        int laneHeight = 80;
        int laneWidth = 700;
        int spacing = 20;
        int startX = 100;
        int totalHeight = horses.size() * (laneHeight + spacing);
        int startY = (600 - totalHeight) / 2;

        // Draw lanes
        for (int i = 0; i < horses.size(); i++) {
            int y = startY + i * (laneHeight + spacing);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(startX, y, laneWidth, laneHeight);

            // Start and finish lines
            g.setColor(Color.WHITE);
            g.fillRect(startX, y, 5, laneHeight);
            g.fillRect(startX + laneWidth - 5, y, 5, laneHeight);

            Horse horse = horses.get(i);
            if (horse != null) {
                double progress = (double) horse.getDistanceTravelled() / raceLength;
                int horseX = startX + (int)(progress * (laneWidth - 10));
                int horseY = y + laneHeight / 2;
                drawHorse(g, horse, horseX, horseY);
            }
        }
    }

    private void drawOvalTrack(Graphics g) {
        int centerX = 450;
        int centerY = 300;
        int radiusX = 300;
        int radiusY = 150;
        int laneSpacing = 30;

        for (int i = 0; i < horses.size(); i++) {
            int rX = radiusX - i * laneSpacing;
            int rY = radiusY - i * laneSpacing;
            g.setColor(Color.GRAY);
            g.fillOval(centerX - rX, centerY - rY, 2 * rX, 2 * rY);
            g.setColor(getBackground());
            g.fillOval(centerX - rX + 10, centerY - rY + 10, 2 * (rX - 10), 2 * (rY - 10));

            Horse horse = horses.get(i);
            if (horse != null) {
                double progress = (double) horse.getDistanceTravelled() / raceLength;
                double angle = progress * 2 * Math.PI;
                int x = (int) (centerX + rX * Math.cos(angle));
                int y = (int) (centerY + rY * Math.sin(angle));
                drawHorse(g, horse, x, y);
            }
        }

        // Finish line
        g.setColor(Color.WHITE);
       // g.fillRect(centerX + radiusX - 5, centerY - 100, 5, 200);
    }

    private void drawFigure8Track(Graphics g) {
        int centerX = 450;
        int centerY = 300;
        int radius = 100;
        int laneSpacing = 20;

        for (int i = 0; i < horses.size(); i++) {
            int r = radius - i * laneSpacing;

            // Thicker track
            g.setColor(Color.GRAY);
            g.fillOval(centerX - r - 75, centerY - r, 2 * r, 2 * r);
            g.fillOval(centerX - r + 75, centerY - r, 2 * r, 2 * r);

            g.setColor(getBackground());
            g.fillOval(centerX - r - 75 + 8, centerY - r + 8, 2 * (r - 8), 2 * (r - 8));
            g.fillOval(centerX - r + 75 + 8, centerY - r + 8, 2 * (r - 8), 2 * (r - 8));

            Horse horse = horses.get(i);
            if (horse != null) {
                double progress = (double) horse.getDistanceTravelled() / raceLength;
                double angle = 2 * Math.PI * progress;
                double centerOffsetX = 75 * Math.cos(angle);
                int x = (int) (centerX + centerOffsetX + r * Math.cos(angle));
                int y = (int) (centerY + r * Math.sin(angle));
                drawHorse(g, horse, x, y);
            }
        }

        // Finish line
        g.setColor(Color.WHITE);
       // g.fillRect(centerX + 75 - 5, centerY - 100, 5, 200);
    }

    private void drawHorse(Graphics g, Horse horse, int x, int y) {
        int size = 30; // bigger horse size
        g.setColor(horse.getCoatColor());
        g.fillOval(x - size/2, y - size/2, size, size);

        Font font = new Font("Segoe UI Emoji", Font.PLAIN, 18); 
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(horse.getSymbol());
        int textHeight = fm.getAscent();

        int drawX = x - textWidth / 2;
        int drawY = y + textHeight / 2 - 2;

        if (!horse.hasFallen()) {
            g.setColor(Color.WHITE);
            g.drawString(horse.getSymbol(), drawX, drawY);
        } else {
            g.setColor(Color.RED);
            g.drawString("❌", drawX, drawY);
        }
    }

    public ArrayList<Horse> getHorses() {
        return horses;
    }
    
}
