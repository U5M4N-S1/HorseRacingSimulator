import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Horse Racing Simulator");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MasterPanel masterPanel = new MasterPanel();
        frame.add(masterPanel);
        frame.setVisible(true);
        frame.pack();

        masterPanel.switchTo("menu");
    }
}

class MasterPanel extends JPanel {
    private CardLayout layout;
    private Race race;
    private CustomisePanel customise;
    private  MainMenuPanel menu;

    public MasterPanel() {
        layout = new CardLayout();
        setLayout(layout);
        race = new Race();
        customise = new CustomisePanel(this);
        menu = new MainMenuPanel(this);
        add(race, "race");
        add(customise, "customise");
        add(menu, "menu");

  
        Horse horse1 = new Horse("🐴", "PIPPI LONGSTOCKING", 0.5, "Arbian", Color.BLUE);
        Horse horse2 = new Horse("🦓", "KOKOMO", 0.6, "Shire", Color.GREEN);
        Horse horse3 = new Horse("🦄","EL JEFE", 0.4, "Appaloosa", Color.ORANGE);
        race.addHorse(horse1);
        race.addHorse(horse2);
        race.addHorse(horse3);
    }

    //method to switch panels
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
