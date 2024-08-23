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
        GraphicsContext gc = canvas.getOverlayGraphicsContext();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5));

        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);
        int maxX = Math.max(startX, endX);
        int maxY = Math.max(startY, endY);

        int gridSize = canvas.getGridSize();
        double zoom = canvas.getZoomLevel();

        // Draw rectangle preview using grid coordinates and scaling by zoom
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                gc.fillRect(x * gridSize * zoom, y * gridSize * zoom, gridSize * zoom, gridSize * zoom);
            }
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

        canvas.clearOverlay();
    }

    @Override
    public VBox createOptionsPanel() {
        return null;
    }
}
