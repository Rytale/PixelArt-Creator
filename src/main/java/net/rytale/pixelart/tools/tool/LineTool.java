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
        GraphicsContext gc = canvas.getDrawingGraphicsContext();  // Use the new method to get the GraphicsContext
        gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5));  // Semi-transparent preview color

        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        while (true) {
            gc.fillRect(startX * canvas.getGridSize(), startY * canvas.getGridSize(), canvas.getGridSize(), canvas.getGridSize());
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

        while (true) {
            canvas.drawPixel(startX, startY, color);  // Use the drawPixel method to make the line permanent
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
    public VBox createOptionsPanel() {
        return null;
    }
}
