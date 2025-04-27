import java.awt.*;
import javax.swing.*;

public class MasterPanel extends JPanel {
    private CardLayout layout;
    private Race race;
    private CustomisePanel customise;
    private MainMenuPanel menu;
    private BettingPanel bettingPanel;

    public MasterPanel() {
        layout = new CardLayout();
        setLayout(layout);
        race = new Race(this);
        
        // Add horses first
        Horse horse1 = new Horse("🐴", "PIPPI LONGSTOCKING", 0.5, "Arabian", Color.BLUE);
        Horse horse2 = new Horse("🦓", "KOKOMO", 0.6, "Shire", Color.GREEN);
        Horse horse3 = new Horse("🦄","EL JEFE", 0.4, "Appaloosa", Color.ORANGE);
        race.addHorse(horse1);
        race.addHorse(horse2);
        race.addHorse(horse3);
        
        // Create panels after horses are added
        customise = new CustomisePanel(this);
        menu = new MainMenuPanel(this);
        bettingPanel = new BettingPanel(this);
        
        add(race, "race");
        add(customise, "customise");
        add(menu, "menu");
        add(bettingPanel, "betting");
    }

    // method to switch panels
    public void switchTo(String name) {
        if (name.equals("customise")) {
            customise.updateHorseList();
        }
        layout.show(this, name);
    }

    public void addHorseToRace(Horse horse) {
        race.addHorse(horse);
    }

    public void startRace() {
        switchTo("race");
        new Thread(() -> race.startRace()).start();
    }

    public Race getRace() {
        return race;
    }
}
