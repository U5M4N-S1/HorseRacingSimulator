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

        masterPanel.switchTo("race");
    }
}

class MasterPanel extends JPanel {
    private CardLayout layout;

    public MasterPanel() {
        layout = new CardLayout();
        setLayout(layout);

        Race race = new Race(10);
        add(race, "race");
    }

    //method to switch panels
    public void switchTo(String name) {
        layout.show(this, name);
    }
}
