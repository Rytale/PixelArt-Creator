package net.rytale.pixelart.file;

import net.rytale.pixelart.layers.PixelCanvas;
import java.io.*;

/**
 * Handles file operations such as saving and loading project files.
 */
public class FileHandler {

    /**
     * Saves the current project to a file.
     *
     * @param file    The file to save the project to.
     * @param canvas  The canvas to save.
     * @throws IOException If an error occurs during saving.
     */
    public void saveProject(File file, PixelCanvas canvas) throws IOException {
        ProjectFile projectFile = new ProjectFile(canvas);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(projectFile);
        }
    }

    /**
     * Loads a project from a file.
     *
     * @param file The file to load the project from.
     * @return The loaded canvas.
     * @throws IOException If an error occurs during loading.
     * @throws ClassNotFoundException If the class is not found.
     */
    public PixelCanvas loadProject(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ProjectFile projectFile = (ProjectFile) in.readObject();
            return projectFile.canvas();
        }
    }
}
