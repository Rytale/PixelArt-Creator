package net.rytale.pixelart.tools.tool;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.AbstractDrawingTool;

public class LineTool extends AbstractDrawingTool {

    public LineTool(PixelCanvas canvas, Color color) {
        super(canvas, color);
    }

    @Override
    protected void drawPreview(int startX, int startY, int endX, int endY) {
        GraphicsContext gc = canvas.getOverlayGraphicsContext();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());  // Clear previous preview

        gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5));  // Semi-transparent preview color

        int gridSize = canvas.getGridSize();

        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        // Draw the preview by filling the grid cells
        while (true) {
            // Calculate the logical position on the grid, ignoring zoom
            int xPos = startX * gridSize;
            int yPos = startY * gridSize;

            // Fill the grid cell at (startX, startY)
            gc.fillRect(xPos, yPos, gridSize, gridSize);

            if (startX == endX && startY == endY) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                startX += sx;
            }
            if (e2 < dx) {
                err += dx;
                startY += sy;
            }
        }
    }

    @Override
    protected void drawFinal(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        // Draw the final line by filling the grid cells
        while (true) {
            canvas.drawPixel(startX, startY, color);  // Fill the grid cell at (startX, startY)

            if (startX == endX && startY == endY) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                startX += sx;
            }
            if (e2 < dx) {
                err += dx;
                startY += sy;
            }
        }

        // Clear the overlay after the final line is drawn
        canvas.clearOverlay();
    }

    @Override
    public VBox createOptionsPanel() {
        // Implement any tool options UI here, if necessary
        return null;
    }
}
