import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class BettingPanel extends JPanel {
    private MasterPanel masterPanel;
    private JLabel betLabel;
    private JButton[] horseButtons;
    private JTextField betAmountField;
    private JLabel balanceLabel;
    private JLabel[] oddsLabels;
    private JLabel potentialWinningsLabel;
    
    private double balance = 100.0;
    private double currentBetAmount = 0.0;
    private int selectedHorseIndex = -1;
    
    public BettingPanel(MasterPanel masterPanel) {
        this.masterPanel = masterPanel;
        setLayout(new BorderLayout(10, 10));
        
        // Main betting header
        betLabel = new JLabel("Place your bet!", SwingConstants.CENTER);
        betLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(betLabel, BorderLayout.NORTH);
        
        // Balance panel
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balanceLabel = new JLabel("Balance: £" + balance);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balancePanel.add(balanceLabel);
        add(balancePanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Horse selection panel
        JPanel horseSelectionPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        horseSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select a Horse"));
        
        System.out.println("Number of horses: " + masterPanel.getRace().getHorses().size());
        
        horseButtons = new JButton[masterPanel.getRace().getHorses().size()];
        oddsLabels = new JLabel[masterPanel.getRace().getHorses().size()];
        
        for (int i = 0; i < masterPanel.getRace().getHorses().size(); i++) {
            final int index = i;
            Horse horse = masterPanel.getRace().getHorses().get(i);
            
            //create panel for each horse entry
            JPanel horseEntryPanel = new JPanel(new BorderLayout(5, 0));
            
            //calculate odds based on horse confidence
            double odds = calculateOdds(horse);
            
            //horse button
            horseButtons[i] = new JButton(horse.getName());
            horseButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            
            //win/loss data
            String record = " (W: " + horse.getWins() + " L: " + horse.getLosses() + ")";
            //odds label
            oddsLabels[i] = new JLabel(String.format("%.1f:1%s", odds, record), SwingConstants.RIGHT);
            oddsLabels[i].setFont(new Font("Arial", Font.ITALIC, 14));
            
            horseEntryPanel.add(horseButtons[i], BorderLayout.CENTER);
            horseEntryPanel.add(oddsLabels[i], BorderLayout.EAST);
            
            horseSelectionPanel.add(horseEntryPanel);
            
            horseButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedHorseIndex = index;
                    highlightSelectedHorse();
                    updatePotentialWinnings();
                }
            });
        }
        
        centerPanel.add(horseSelectionPanel, BorderLayout.CENTER);
        
        // Betting controls panel
        JPanel bettingControlsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        bettingControlsPanel.setBorder(BorderFactory.createTitledBorder("Place Your Bet"));
        
        JPanel betAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel betAmountLabel = new JLabel("Bet Amount: £");
        betAmountField = new JTextField(10);
        betAmountField.setText("10.0");
        betAmountField.addActionListener(e -> updatePotentialWinnings());
        betAmountPanel.add(betAmountLabel);
        betAmountPanel.add(betAmountField);
        
        JPanel potentialWinningsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        potentialWinningsLabel = new JLabel("Potential Winnings: £0.00");
        potentialWinningsPanel.add(potentialWinningsLabel);
        
        JPanel confirmBetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton placeBetButton = new JButton("Place Bet & Start Race");
        placeBetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedHorseIndex >= 0) {
                    try {
                        currentBetAmount = Double.parseDouble(betAmountField.getText().trim());
                        if (currentBetAmount <= 0) {
                            JOptionPane.showMessageDialog(BettingPanel.this, 
                                "Please enter a positive bet amount.", 
                                "Invalid Bet", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        if (currentBetAmount > balance) {
                            JOptionPane.showMessageDialog(BettingPanel.this, 
                                "You don't have enough balance for this bet.", 
                                "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        //deduct bet amount from balance
                        adjustBalance(-currentBetAmount);
                        //set the selected horse and start race
                        Horse selectedHorse = masterPanel.getRace().getHorses().get(selectedHorseIndex);
                        masterPanel.getRace().setBetHorse(selectedHorse);
                        masterPanel.getRace().setBetAmount(currentBetAmount);
                        masterPanel.startRace();
                        masterPanel.switchTo("race");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(BettingPanel.this, 
                            "Please enter a valid number for the bet amount.", 
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);}
                } else {
                    JOptionPane.showMessageDialog(BettingPanel.this, 
                        "Please select a horse first.", 
                        "No Horse Selected", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        confirmBetPanel.add(placeBetButton);
        
        bettingControlsPanel.add(betAmountPanel);
        bettingControlsPanel.add(potentialWinningsPanel);
        bettingControlsPanel.add(confirmBetPanel);
        
        centerPanel.add(bettingControlsPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Initialize potential winnings display
        betAmountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updatePotentialWinnings();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updatePotentialWinnings();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updatePotentialWinnings();
            }
        });
    }
    
    /**
     * Calculate odds based on horse confidence and win/loss record
     * Lower confidence = higher odds
     */
    private double calculateOdds(Horse horse) {
        double baseOdds = 1.0 / horse.getConfidence();
        
        // Adjust odds based on win/loss record
        int totalRaces = horse.getWins() + horse.getLosses();
        if (totalRaces > 0) {
            double winRate = (double) horse.getWins() / totalRaces;
            // Mix of base odds and win rate for more balanced odds
            return baseOdds * (1.0 + (0.5 - winRate) * 2);
        }
        
        return baseOdds;
    }
    
    /**
     * Update the potential winnings display
     */
    private void updatePotentialWinnings() {
        if (selectedHorseIndex >= 0) {
            try {
                double betAmount = Double.parseDouble(betAmountField.getText().trim());
                Horse selectedHorse = masterPanel.getRace().getHorses().get(selectedHorseIndex);
                double odds = calculateOdds(selectedHorse);
                double potentialWin = betAmount * odds;
                
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                potentialWinningsLabel.setText("Potential Winnings: " + formatter.format(potentialWin));
            } catch (NumberFormatException e) {
                potentialWinningsLabel.setText("Potential Winnings: £0.00");
            }
        } else {
            potentialWinningsLabel.setText("Potential Winnings: £0.00");
        }
    }
    
    /**
     * Highlight the selected horse button
     */
    private void highlightSelectedHorse() {
        for (int i = 0; i < horseButtons.length; i++) {
            if (i == selectedHorseIndex) {
                horseButtons[i].setBackground(new Color(173, 216, 230)); // Light blue
                horseButtons[i].setForeground(Color.BLACK);
            } else {
                horseButtons[i].setBackground(null);
                horseButtons[i].setForeground(null);
            }
        }
    }
    
    /**
     * Adjust the player's balance
     * @param amount Positive to add, negative to subtract
     */
    public void adjustBalance(double amount) {
        balance += amount;
        balanceLabel.setText("Balance: £" + String.format("%.2f", balance));
    }
    
    /**
     * Get the current balance
     * @return Current balance
     */
    public double getBalance() {
        return balance;
    }
    
    /**
     * Set the balance to a specific amount
     * @param newBalance The new balance
     */
    public void setBalance(double newBalance) {
        balance = newBalance;
        balanceLabel.setText("Balance: £" + String.format("%.2f", balance));
    }
    
    /**
     * Get the current bet amount
     * @return Current bet amount
     */
    public double getCurrentBetAmount() {
        return currentBetAmount;
    }
    
    /**
     * Get the selected horse index
     * @return The index of the selected horse or -1 if none selected
     */
    public int getSelectedHorseIndex() {
        return selectedHorseIndex;
    }
}