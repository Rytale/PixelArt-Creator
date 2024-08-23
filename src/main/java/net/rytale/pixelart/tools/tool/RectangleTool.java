package net.rytale.pixelart.tools.tool;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.AbstractDrawingTool;

public class RectangleTool extends AbstractDrawingTool {

    public RectangleTool(PixelCanvas canvas, Color color) {
        super(canvas, color);
    }

    @Override
    protected void drawPreview(int startX, int startY, int endX, int endY) {
        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);
        int maxX = Math.max(startX, endX);
        int maxY = Math.max(startY, endY);

        GraphicsContext gc = canvas.getDrawingGraphicsContext();
        gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5));

        for (int x = minX; x <= maxX; x++) {
            gc.fillRect(x * canvas.getGridSize(), minY * canvas.getGridSize(), canvas.getGridSize(), canvas.getGridSize());  // Top border
            gc.fillRect(x * canvas.getGridSize(), maxY * canvas.getGridSize(), canvas.getGridSize(), canvas.getGridSize());  // Bottom border
        }

        for (int y = minY + 1; y < maxY; y++) {
            gc.fillRect(minX * canvas.getGridSize(), y * canvas.getGridSize(), canvas.getGridSize(), canvas.getGridSize());  // Left border
            gc.fillRect(maxX * canvas.getGridSize(), y * canvas.getGridSize(), canvas.getGridSize(), canvas.getGridSize());  // Right border
        }
    }

    @Override
    protected void drawFinal(int startX, int startY, int endX, int endY) {
        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);
        int maxX = Math.max(startX, endX);
        int maxY = Math.max(startY, endY);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                canvas.drawPixel(x, y, color);
            }
        }
    }

    @Override
    public VBox createOptionsPanel() {
        return null;
    }
}
