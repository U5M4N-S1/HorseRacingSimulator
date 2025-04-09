import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Horse Racing Simulator");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Race race = new Race(10);
        frame.add(race);
        frame.pack();
        frame.setVisible(true);
    }
}