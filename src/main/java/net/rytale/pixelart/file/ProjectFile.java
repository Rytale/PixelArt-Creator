package net.rytale.pixelart.file;

import java.io.Serial;
import java.io.Serializable;
import net.rytale.pixelart.layers.PixelCanvas;

/**
 * Represents a project file, which includes the canvas and its layers.
 */
public record ProjectFile(PixelCanvas canvas) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
