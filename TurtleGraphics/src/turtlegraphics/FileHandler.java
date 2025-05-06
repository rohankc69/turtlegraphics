package turtlegraphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileHandler {
    private static TurtleGraphics turtleGraphics;

    public FileHandler(TurtleGraphics graphics) {
        turtleGraphics = graphics;
    }

    public static boolean checkUnsavedChanges(TurtleGraphics graphics) {
        if (!graphics.isSavedState()) {
            int response = JOptionPane.showConfirmDialog(
                    graphics,
                    "You have unsaved changes. Save before proceeding?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.CANCEL_OPTION) return false;
            if (response == JOptionPane.YES_OPTION) saveCurrentState(graphics);
        }
        return true;
    }

    public static void saveCurrentState(TurtleGraphics graphics) {
        int response = JOptionPane.showConfirmDialog(
                graphics,
                "Save both image and commands?",
                "Save State",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (response == JOptionPane.CANCEL_OPTION) return;
        if (response == JOptionPane.YES_OPTION) {
            saveImage(graphics);
            saveCommands(graphics);
        }
        graphics.setSavedState(true);
    }

    private static void saveImage(TurtleGraphics graphics) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Image Files (*.png, *.jpg)", "png", "jpg"));

        if (fileChooser.showSaveDialog(graphics) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String format = file.getName().toLowerCase().endsWith(".jpg") ? "jpg" : "png";
                ImageIO.write(graphics.getCurrentImage(), format, file);
                graphics.displayMessage("Image saved successfully");
            } catch (IOException e) {
                showError(graphics, "Error saving image: " + e.getMessage());
            }
        }
    }

    private static void saveCommands(TurtleGraphics graphics) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Text Files (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(graphics) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                for (String cmd : graphics.getCommandHistory()) {
                    writer.println(cmd.replaceAll(" \\[.*\\]", ""));
                }
                graphics.displayMessage("Commands saved successfully");
            } catch (IOException e) {
                showError(graphics, "Error saving commands: " + e.getMessage());
            }
        }
    }

    public static void loadState(TurtleGraphics graphics) {
        if (!checkUnsavedChanges(graphics)) return;

        String[] options = {"Load Image", "Load Commands"};
        int choice = JOptionPane.showOptionDialog(graphics,
                "Select load type:", "Load",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) loadImage(graphics);
        else if (choice == 1) loadCommands(graphics);
    }

    private static void loadImage(TurtleGraphics graphics) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Image Files (*.png, *.jpg)", "png", "jpg"));

        if (fileChooser.showOpenDialog(graphics) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fileChooser.getSelectedFile());
                graphics.setCurrentImage(img);
                graphics.displayMessage("Image loaded successfully");
            } catch (IOException e) {
                showError(graphics, "Error loading image: " + e.getMessage());
            }
        }
    }

    private static void loadCommands(TurtleGraphics graphics) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Text Files (*.txt)", "txt"));

        if (fileChooser.showOpenDialog(graphics) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(fileChooser.getSelectedFile()))) {
                graphics.reset();
                String line;
                while ((line = reader.readLine()) != null) {
                    graphics.processCommand(line);
                }
                graphics.displayMessage("Commands loaded successfully");
            } catch (IOException e) {
                showError(graphics, "Error loading commands: " + e.getMessage());
            }
        }
    }

    private static void showError(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}