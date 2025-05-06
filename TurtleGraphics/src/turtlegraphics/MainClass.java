package turtlegraphics;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainClass {
    public static void main(String[] args) {
        TurtleGraphics turtleGraphics = new TurtleGraphics();

        JFrame mainFrame = new JFrame("Turtle Graphics");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new FlowLayout());
        contentPanel.add(turtleGraphics);

        mainFrame.add(contentPanel, BorderLayout.CENTER);
        mainFrame.add(turtleGraphics.getHistoryPanel(), BorderLayout.EAST);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                turtleGraphics.exitApplication();
            }
        });

        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}