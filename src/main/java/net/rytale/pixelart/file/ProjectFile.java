package net.rytale.pixelart.file;

import java.io.Serializable;
import net.rytale.pixelart.layers.PixelCanvas;

/**
 * Represents a project file, which includes the canvas and its layers.
 */
public class ProjectFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private final PixelCanvas canvas;

    public ProjectFile(PixelCanvas canvas) {
        this.canvas = canvas;
    }

    public PixelCanvas getCanvas() {
        return canvas;
    }
}
