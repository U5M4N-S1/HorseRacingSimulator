import java.util.concurrent.TimeUnit;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Race extends JPanel {
    private ArrayList<Horse> horses = new ArrayList<>();
    private int raceLength;
    private int width = 900;
    public String track = "oval"; // "line", "oval", "figure8"
    public String weather = "dry"; // "clear", "icy", "muddy"
    boolean finished = false;
    private String winnerText = "";
    private JButton restartButton;
    private JButton returnToMenuButton;

    private Horse betHorse = null;
    private double betAmount = 0.0;
    private JLabel bettingResultLabel;

    private MasterPanel masterPanel;

    public Race(MasterPanel masterPanel) {
        this.masterPanel = masterPanel;
        setPreferredSize(new Dimension(900, 600));
        setBackground(new Color(74, 138, 70));
        raceLength = 20;

        setLayout(null);
        //ui for the race
        restartButton = new JButton("Restart Race");
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        restartButton.setBounds(375, 520, 150, 30);
        restartButton.addActionListener(e -> startRace());
        add(restartButton);

        returnToMenuButton = new JButton("Return to Menu");
        returnToMenuButton.setFocusable(false);
        returnToMenuButton.setBounds(375, 460, 150, 30);
        returnToMenuButton.addActionListener(e -> masterPanel.switchTo("menu"));
        add(returnToMenuButton);

        bettingResultLabel = new JLabel("");
        bettingResultLabel.setBounds(10, 10, 400, 30);
        bettingResultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bettingResultLabel.setForeground(Color.BLACK);
        add(bettingResultLabel);
        
        setName("race");
    }

    public void addHorse(Horse theHorse) {
        horses.add(theHorse);
    }

    public void startRace() {
        finished = false;
        winnerText = "";
        bettingResultLabel.setText("");
        //hide the buttons during the race
        restartButton.setVisible(false);
        returnToMenuButton.setVisible(false); 

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
                        theHorse.addWin();
                        for (Horse h : horses) {
                            if (h != theHorse) {
                                h.addLoss();
                            }
                        }
                        finished = true;

                        if (betHorse != null) {
                            if (theHorse == betHorse) {
                                double odds = calculateOdds(betHorse);
                                double winnings = betAmount * odds;
                                String winMessage = String.format("You won £%.2f!", winnings);
                                bettingResultLabel.setText(winMessage);
                                
                                //ipdate the player's balance when they win
                                updateBettingPanelBalance(winnings);
                            } else {
                                //no need to update balance on loss as it was already deducted
                                String loseMessage = String.format("You lost £%.2f!", betAmount);
                                bettingResultLabel.setText(loseMessage);
                            }
                        }
                        break;
                    }
                    if (!theHorse.hasFallen()) {
                        allFallen = false;
                    }
                }
                if (allFallen && !finished) {
                    winnerText = "All horses have fallen!";
                    finished = true;
                    if (betHorse != null) {
                        bettingResultLabel.setText("Race cancelled - Bet refunded!");
                        // Return the bet amount to the player's balance
                        updateBettingPanelBalance(betAmount);
                    }
                }
                repaint();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception e) {}
            }
            restartButton.setVisible(true);
            returnToMenuButton.setVisible(true); // Show the "Return to Menu" button once the race is finished
        }).start();
    }

    /**
     * Updates the balance in the BettingPanel
     * @param amount Amount to add to the balance
     */
    private void updateBettingPanelBalance(double amount) {
        try {
            BettingPanel bettingPanel = (BettingPanel) findComponentByName(masterPanel, "betting");
            if (bettingPanel != null) {
                bettingPanel.adjustBalance(amount);
            }
        } catch (Exception e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }
    

    //method to find a component by name
    private Component findComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
        }
        return null;
    }

    /**
     * Calculate odds based on horse confidence and win loss data
     */
    public double calculateOdds(Horse horse) {
        double baseOdds = 1.0 / horse.getConfidence();
        
        //ajust odds based on win/loss record
        int totalRaces = horse.getWins() + horse.getLosses();
        if (totalRaces > 0) {
            double winRate = (double) horse.getWins() / totalRaces;
            //mix of base odds and win rate for more balanced odds
            return baseOdds * (1.0 + (0.5 - winRate) * 2);
        }
        return baseOdds;
    }

    private void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            if (Math.random() < theHorse.getConfidence()) {
                theHorse.moveForward();
            }
            double fallChance = 0.1 * theHorse.getConfidence() * theHorse.getConfidence();
            //change falling chance depending on weather
            if (weather.equals("icy")) {
                fallChance *= 2;
            } else if (weather.equals("muddy")) {
                fallChance *= 1.5;
            }

            if (Math.random() < fallChance) {
                theHorse.fall();
            }
        }
    }

    //check if race is won
    private boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() >= raceLength;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

        //winner text
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
        //draw each lane
        for (int i = 0; i < horses.size(); i++) {
            int y = startY + i * (laneHeight + spacing);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(startX, y, laneWidth, laneHeight);
            g.setColor(Color.WHITE);
            g.fillRect(startX + laneWidth - 5, y, 5, laneHeight);
            //draw horses
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
        // darw each oval lane
        for (int i = 0; i < horses.size(); i++) {
            int rX = radiusX - i * laneSpacing;
            int rY = radiusY - i * laneSpacing;
            g.setColor(Color.GRAY);
            g.fillOval(centerX - rX, centerY - rY, 2 * rX, 2 * rY);
            g.setColor(getBackground());
            g.fillOval(centerX - rX + 10, centerY - rY + 10, 2 * (rX - 10), 2 * (rY - 10));
            //draw horses
            Horse horse = horses.get(i);
            if (horse != null) {
                double progress = (double) horse.getDistanceTravelled() / raceLength;
                double angle = progress * 2 * Math.PI;
                int x = (int)(centerX + rX * Math.cos(angle));
                int y = (int)(centerY + rY * Math.sin(angle));
                drawHorse(g, horse, x, y);
            }
        }
    }

    private void drawFigure8Track(Graphics g) {
        int centerX = 450;
        int centerY = 300;
        int radius = 200;
        int laneSpacing = 30;
        int loopOffset = 150;
        //draw the firgure 8
        for (int i = 0; i < horses.size(); i++) {
            int r = radius - i * laneSpacing;
            g.setColor(Color.GRAY);
            g.fillOval(centerX - r - loopOffset, centerY - r, 2 * r, 2 * r);
            g.fillOval(centerX - r + loopOffset, centerY - r, 2 * r, 2 * r);
            g.setColor(getBackground());
            g.fillOval(centerX - r - loopOffset + 12, centerY - r + 12, 2 * (r - 12), 2 * (r - 12));
            g.fillOval(centerX - r + loopOffset + 12, centerY - r + 12, 2 * (r - 12), 2 * (r - 12));
        }
        //draw horses
        for (int i = 0; i < horses.size(); i++) {
            int r = radius - i * laneSpacing;
            Horse horse = horses.get(i);
            if (horse != null) {
                double progress = (double) horse.getDistanceTravelled() / raceLength;
                double angle = 2 * Math.PI * progress;
                double centerOffsetX = loopOffset * Math.cos(angle);
                int x = (int)(centerX + centerOffsetX + r * Math.cos(angle));
                int y = (int)(centerY + r * Math.sin(angle));
                drawHorse(g, horse, x, y);
            }
        }
    }

    private void drawHorse(Graphics g, Horse horse, int x, int y) {
        int size = 30;
        g.setColor(horse.getCoatColor());
        g.fillOval(x - size/2, y - size/2, size, size);

        Font font = new Font("Segoe UI Emoji", Font.PLAIN, 18);
        g.setFont(font);
        //set font to work with emojis
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(horse.getSymbol());
        int textHeight = fm.getAscent();

        int drawX = x - textWidth / 2;
        int drawY = y + textHeight / 2 - 2;

        if (!horse.hasFallen()) {
            g.setColor(Color.WHITE);
            g.drawString(horse.getSymbol(), drawX, drawY);
        } else {
            //change symbol if fallen
            g.setColor(Color.RED);
            g.drawString("❌", drawX, drawY);
        }
    }
    //methods to recive and send data
    public void setBetHorse(Horse horse) {
        this.betHorse = horse;
    }
    
    public void setBetAmount(double amount) {
        this.betAmount = amount;
    }
    
    public double getBetAmount() {
        return this.betAmount;
    }

    public ArrayList<Horse> getHorses() {
        return horses;
    }

    public void setRaceLength(int length) {
        this.raceLength = length;
    }
}