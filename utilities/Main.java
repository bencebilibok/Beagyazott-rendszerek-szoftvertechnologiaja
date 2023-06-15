package utilities;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {

        Player player;
        Collisions collisions;
        add(new Track());

        setResizable(false);
        setBounds(0, 0, 800, 800);

        setTitle("SNAKE");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame ex = new Main();
            ex.setVisible(true);
        });
    }
}
