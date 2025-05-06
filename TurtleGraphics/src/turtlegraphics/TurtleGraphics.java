package turtlegraphics;

import uk.ac.leedsbeckett.oop.LBUGraphics;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class TurtleGraphics extends LBUGraphics {
    private CommandParser commandParser;
    private FileHandler fileHandler;
    private ArrayList<String> commandHistory;
    private JTextArea historyArea;
    private JPanel historyPanel;
    private boolean savedState = true;

    private ArrayList<Movement> movementHistory = new ArrayList<>();
    private boolean isRecording = false;
    private int animationSpeed = 100;
    private Color trailColor = null;
    private ArrayList<Point> obstacles = new ArrayList<>();
    private boolean collisionDetection = false;

    private class Movement {
        int x, y; boolean penDown; Color color; int width;
        Movement(int x, int y, boolean penDown, Color color, int width) {
            this.x = x; this.y = y; this.penDown = penDown;
            this.color = color; this.width = width;
        }
    }

    public TurtleGraphics() {
        super();
        commandParser = new CommandParser(this);
        fileHandler = new FileHandler(this);
        commandHistory = new ArrayList<>();
        setupHistoryPanel();
        reset();
    }

    private void setupHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());
        historyArea = new JTextArea(10, 20);
        historyArea.setEditable(false);
        historyPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        historyPanel.add(new JLabel("Command History:"), BorderLayout.NORTH);
    }

    @Override
    public void about() {
        super.about();
        displayMessage("Enhanced Turtle Graphics by Rohan");
    }

    @Override
    public void processCommand(String command) {
        String fullCommand = command + " [Rohan Raj K C]";
        commandHistory.add(fullCommand);
        historyArea.append(fullCommand + "\n");
        savedState = false;

        if (!commandParser.parseAndExecute(command)) {
            displayMessage("Invalid command: " + command);
        }
    }

    // === Drawing Shapes and ROHAN horizontally ===

    public void drawRohan() {
        reset();
        setPenState(true);
        pointTurtle(0); // Face right
        int x = getxPos(), y = getyPos();

        // === R ===
        moveTo(x, y);
        pointTurtle(90);
        forward(50); right(90); forward(30); right(90); forward(50);
        left(135); forward(45);

        // === O ===
        x += 60;
        moveTo(x, y);
        pointTurtle(0);
        setPenState(true);
        forward(30); right(90); forward(50); right(90); forward(30); right(90); forward(50);

        // === H ===
        x += 60;
        moveTo(x, y);
        pointTurtle(90);
        forward(50); setPenState(false); forward(-25); setPenState(true); pointTurtle(0); forward(30);
        setPenState(false); pointTurtle(90); forward(-25); setPenState(true); forward(50);

        // === A ===
        x += 60;
        moveTo(x, y);
        pointTurtle(90);
        forward(50); right(135); forward(60);
        setPenState(false); forward(-30); left(90);
        setPenState(true); forward(30);

        // === N ===
        x += 60;
        moveTo(x, y);
        pointTurtle(90);
        forward(50); right(135); forward(70); left(135); forward(50);

        displayMessage("ROHAN drawn in a horizontal straight line.");
    }

    // === Other Drawing Methods (shapes, etc.) ===

    public void drawSquare(int size) {
        if (size <= 0) return;
        int startX = getxPos(), startY = getyPos(), dir = getDirection();
        setPenState(true);
        for (int i = 0; i < 4; i++) {
            forward(size);
            right(90);
        }
        setPenState(false);
        moveTo(startX, startY);
        pointTurtle(dir);
        setPenState(true);
    }

    public void drawTriangle(int size) {
        if (size <= 0) return;
        int startX = getxPos(), startY = getyPos(), dir = getDirection();
        setPenState(true);
        for (int i = 0; i < 3; i++) {
            forward(size);
            right(120);
        }
        setPenState(false);
        moveTo(startX, startY);
        pointTurtle(dir);
        setPenState(true);
    }

    public void drawRectangle(int width, int height) {
        if (width <= 0 || height <= 0) return;
        int startX = getxPos(), startY = getyPos(), dir = getDirection();
        setPenState(true);
        for (int i = 0; i < 2; i++) {
            forward(width);
            right(90);
            forward(height);
            right(90);
        }
        setPenState(false);
        moveTo(startX, startY);
        pointTurtle(dir);
        setPenState(true);
    }

    public void drawCircle(int radius) {
        if (radius <= 0) return;
        int startX = getxPos(), startY = getyPos(), dir = getDirection();
        setPenState(false);
        pointTurtle(0);
        forward(radius);
        setPenState(true);
        drawCircle(radius, getxPos(), getyPos());
        setPenState(false);
        moveTo(startX, startY);
        pointTurtle(dir);
        setPenState(true);
    }

    // === Animation ===

    public void startRecording() {
        movementHistory.clear();
        isRecording = true;
        displayMessage("Recording started");
    }

    public void stopRecording() {
        isRecording = false;
        displayMessage("Recording stopped. " + movementHistory.size() + " movements captured");
    }

    public void playRecording() {
        new Thread(() -> {
            Color originalColor = getPenColour();
            int originalWidth = (int) getStroke();

            for (Movement m : movementHistory) {
                setPenColour(m.color);
                setStroke(m.width);
                setPenState(m.penDown);
                moveTo(m.x, m.y);

                if (trailColor != null) {
                    Graphics2D g = (Graphics2D) getBufferedImage().getGraphics();
                    g.setColor(trailColor);
                    g.fillOval(m.x - 2, m.y - 2, 4, 4);
                    repaint();
                }

                try { Thread.sleep(animationSpeed); }
                catch (InterruptedException e) {}
            }

            setPenColour(originalColor);
            setStroke(originalWidth);
        }).start();
    }

    protected void moveTo(int newX, int newY) {
        if (collisionDetection) {
            for (Point p : obstacles) {
                if (Math.abs(newX - p.x) < 10 && Math.abs(newY - p.y) < 10) {
                    displayMessage("Collision detected!");
                    return;
                }
            }
        }

        if (isRecording) {
            movementHistory.add(new Movement(
                    newX, newY, getPenState(),
                    getPenColour(), (int) getStroke()));
        }

        super.forward((int) Math.hypot(newX - getxPos(), newY - getyPos()));
    }

    // === Misc ===

    public JPanel getHistoryPanel() { return historyPanel; }
    public ArrayList<String> getCommandHistory() { return commandHistory; }

    public void setCommandHistory(ArrayList<String> history) {
        this.commandHistory = history;
        updateHistoryDisplay();
    }

    public boolean isSavedState() { return savedState; }
    public void setSavedState(boolean saved) { this.savedState = saved; }

    public BufferedImage getCurrentImage() { return getBufferedImage(); }
    public void setCurrentImage(BufferedImage image) { setBufferedImage(image); }

    public void exitApplication() {
        if (!savedState) {
            int response = JOptionPane.showConfirmDialog(this,
                    "Save before exiting?", "Exit",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (response == JOptionPane.CANCEL_OPTION) return;
            if (response == JOptionPane.YES_OPTION) {
                FileHandler.saveCurrentState(this);
            }
        }
        System.exit(0);
    }

    private void updateHistoryDisplay() {
        historyArea.setText("");
        for (String cmd : commandHistory) {
            historyArea.append(cmd + "\n");
        }
    }

    public void enableCollisionDetection(boolean enable) {
        this.collisionDetection = enable;
    }

    public void addObstacle(int x, int y) {
        obstacles.add(new Point(x, y));
        displayMessage("Obstacle added at (" + x + "," + y + ")");
    }
}
