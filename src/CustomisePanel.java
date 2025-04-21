import javax.swing.*; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

class CustomisePanel extends JPanel {
    private MasterPanel master;
    private JComboBox<Horse> horseSelector;
    private JComboBox<String> symbolSelector;
    private JComboBox<String> breedSelector;
    private JComboBox<String> coatColorSelector;
    private JButton applyButton;
    private JButton returnButton;
    private Image backgroundImage;

    private HashMap<String, Color> coatColorMap;

    public CustomisePanel(MasterPanel master) {
        this.master = master;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        backgroundImage = new ImageIcon("assets/background.png").getImage();

        //create colour map
        coatColorMap = new HashMap<>();
        coatColorMap.put("Black", Color.BLACK);
        coatColorMap.put("Blue", Color.BLUE);
        coatColorMap.put("Red", Color.RED);
        coatColorMap.put("Green", Color.GREEN);
        coatColorMap.put("Cyan", Color.CYAN);
        coatColorMap.put("Pink", Color.PINK);
        coatColorMap.put("Orange", Color.ORANGE);

        //Horse selector
        horseSelector = new JComboBox<>();
        updateHorseList();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Select Horse:"), gbc);
        gbc.gridx = 1;
        add(horseSelector, gbc);

        //symbola selector
        symbolSelector = new JComboBox<>(new String[]{
            "🐴", "🦓", "🦄", "🐎", "🐆", "🐂", "🐃", "🦌"
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Select Symbol:"), gbc);
        gbc.gridx = 1;
        add(symbolSelector, gbc);

        //breed selector
        breedSelector = new JComboBox<>(new String[]{
            "Thoroughbred", "Arabian", "Quarter Horse", "Shire", "Appaloosa"
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Select Breed:"), gbc);
        gbc.gridx = 1;
        add(breedSelector, gbc);

        // Colour selector
        coatColorSelector = new JComboBox<>(coatColorMap.keySet().toArray(new String[0]));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Select Coat Color:"), gbc);
        gbc.gridx = 1;
        add(coatColorSelector, gbc);

        // Apply button
        applyButton = new JButton("Apply Changes");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(applyButton, gbc);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Horse selectedHorse = (Horse) horseSelector.getSelectedItem();
                if (selectedHorse != null) {
                    selectedHorse.setSymbol((String) symbolSelector.getSelectedItem());
                    selectedHorse.setBreed((String) breedSelector.getSelectedItem());

                    // Color from map
                    String selectedColorName = (String) coatColorSelector.getSelectedItem();
                    Color color = coatColorMap.get(selectedColorName);
                    selectedHorse.setCoatColor(color);

                    JOptionPane.showMessageDialog(CustomisePanel.this, "Horse updated!");

                    updateHorseList();
                }
            }
        });

        // Return to Menu button
        returnButton = new JButton("Return to Menu");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(returnButton, gbc);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                master.switchTo("menu");
            }
        });
    }

    public void updateHorseList() {
        Horse selected = (Horse) horseSelector.getSelectedItem();
        horseSelector.removeAllItems();
        for (Horse horse : master.getRace().getHorses()) {
            horseSelector.addItem(horse);
        }
        if (selected != null) {
            horseSelector.setSelectedItem(selected);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
