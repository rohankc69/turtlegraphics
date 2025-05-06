package turtlegraphics;

import java.awt.Color;

public class CommandParser {
    private TurtleGraphics turtleGraphics;

    public CommandParser(TurtleGraphics turtleGraphics) {
        this.turtleGraphics = turtleGraphics;
    }

    public boolean parseAndExecute(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) return false;

        String cmd = parts[0].toLowerCase();
        try {
            switch (cmd) {
                // Basic commands
                case "about": turtleGraphics.about(); return true;
                case "penup": turtleGraphics.setPenState(false); return true;
                case "pendown": turtleGraphics.setPenState(true); return true;
                case "reset": turtleGraphics.reset(); return true;
                case "clear": turtleGraphics.clear(); return true;
                case "save": FileHandler.saveCurrentState(turtleGraphics); return true;
                case "load": FileHandler.loadState(turtleGraphics); return true;
                case "exit": turtleGraphics.exitApplication(); return true;

                // Movement
                case "left": return parseRotation(parts, false);
                case "right": return parseRotation(parts, true);
                case "move": return parseMovement(parts, true);
                case "reverse": return parseMovement(parts, false);

                // Colors
                case "black": turtleGraphics.setPenColour(Color.BLACK); return true;
                case "red": turtleGraphics.setPenColour(Color.RED); return true;
                case "green": turtleGraphics.setPenColour(Color.GREEN); return true;
                case "white": turtleGraphics.setPenColour(Color.WHITE); return true;
                case "pencolour": return parsePenColor(parts);

                // Shapes
                case "square": return parseSquare(parts);
                case "triangle": return parseTriangle(parts);
                case "circle": return parseCircle(parts);
                case "rectangle": return parseRectangle(parts);

                // Animation
                case "record": turtleGraphics.startRecording(); return true;
                case "stoprecord": turtleGraphics.stopRecording(); return true;
                case "play": turtleGraphics.playRecording(); return true;
                case "speed": return parseSpeed(parts);

                // Collision
                case "addobstacle": return parseObstacle(parts);
                case "collision": turtleGraphics.enableCollisionDetection(true); return true;
                case "nocollision": turtleGraphics.enableCollisionDetection(false); return true;

                // Custom
                case "rohan": turtleGraphics.drawRohan(); return true;

                default:
                    turtleGraphics.displayMessage("Unknown command: " + cmd);
                    return false;
            }
        } catch (Exception e) {
            turtleGraphics.displayMessage("Error: " + e.getMessage());
            return false;
        }
    }

    private boolean parseRotation(String[] parts, boolean right) {
        if (parts.length < 2) return error("Missing degrees parameter");
        int degrees = validatePositive(parts[1]);
        if (degrees < 0) return false;
        if (right) turtleGraphics.right(degrees); else turtleGraphics.left(degrees);
        return true;
    }

    private boolean parseMovement(String[] parts, boolean forward) {
        if (parts.length < 2) return error("Missing distance parameter");
        int distance = validatePositive(parts[1]);
        if (distance < 0) return false;
        turtleGraphics.forward(forward ? distance : -distance);
        return true;
    }

    private boolean parsePenColor(String[] parts) {
        if (parts.length < 2) return error("Missing RGB values");
        String[] rgb = parts[1].split(",");
        if (rgb.length != 3) return error("Need 3 comma-separated RGB values");
        int r = validateInRange(rgb[0], 0, 255);
        int g = validateInRange(rgb[1], 0, 255);
        int b = validateInRange(rgb[2], 0, 255);
        if (r < 0 || g < 0 || b < 0) return false;
        turtleGraphics.setPenColour(new Color(r, g, b));
        return true;
    }

    private boolean parseSquare(String[] parts) {
        if (parts.length < 2) return error("Missing size");
        int size = validatePositive(parts[1]);
        if (size < 0) return false;
        turtleGraphics.drawSquare(size);
        return true;
    }

    private boolean parseTriangle(String[] parts) {
        if (parts.length < 2) return error("Missing size");
        int size = validatePositive(parts[1]);
        if (size < 0) return false;
        turtleGraphics.drawTriangle(size);
        return true;
    }

    private boolean parseCircle(String[] parts) {
        if (parts.length < 2) return error("Missing radius");
        int radius = validatePositive(parts[1]);
        if (radius < 0) return false;
        turtleGraphics.drawCircle(radius);
        return true;
    }

    private boolean parseRectangle(String[] parts) {
        if (parts.length < 3) return error("Missing width/height");
        int w = validatePositive(parts[1]);
        int h = validatePositive(parts[2]);
        if (w < 0 || h < 0) return false;
        turtleGraphics.drawRectangle(w, h);
        return true;
    }

    private boolean parseSpeed(String[] parts) {
        if (parts.length < 2) return error("Missing speed");
        int speed = validatePositive(parts[1]);
        if (speed < 0) return false;
        turtleGraphics.setTurtleSpeed(speed);
        return true;
    }

    private boolean parseObstacle(String[] parts) {
        if (parts.length < 3) return error("Missing x and y");
        int x = validatePositive(parts[1]);
        int y = validatePositive(parts[2]);
        if (x < 0 || y < 0) return false;
        turtleGraphics.addObstacle(x, y);
        return true;
    }

    private int validatePositive(String s) {
        try {
            int v = Integer.parseInt(s);
            return v > 0 ? v : -1;
        } catch (NumberFormatException e) { return -1; }
    }

    private int validateInRange(String s, int min, int max) {
        int v = validatePositive(s);
        return (v >= min && v <= max) ? v : -1;
    }

    private boolean error(String msg) {
        turtleGraphics.displayMessage("Error: " + msg);
        return false;
    }
}
