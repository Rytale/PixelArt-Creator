package net.rytale.pixelart.tools.tool;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.AbstractDrawingTool;

public class EraserTool extends AbstractDrawingTool {

    public EraserTool(PixelCanvas canvas) {
        super(canvas, canvas.getBackgroundColor());
    }

    // Add this constructor to match the expected signature
    public EraserTool(PixelCanvas canvas, Color color) {
        this(canvas);  // Call the existing constructor
    }

    @Override
    protected void drawPreview(int startX, int startY, int endX, int endY) {
        // No preview needed for the eraser
    }

    @Override
    protected void drawFinal(int startX, int startY, int endX, int endY) {
        canvas.drawPixel(endX, endY, canvas.getBackgroundColor());
    }

    @Override
    public void onMouseDragged(int gridX, int gridY) {
        canvas.drawPixel(gridX, gridY, canvas.getBackgroundColor());
    }

    @Override
    public void onMousePressed(int gridX, int gridY) {
        super.onMousePressed(gridX, gridY);
        drawFinal(gridX, gridY, gridX, gridY);
    }

    @Override
    public void onMouseReleased(int gridX, int gridY) {
        drawFinal(startX, startY, gridX, gridY);
    }

    @Override
    public VBox createOptionsPanel() {
        return null;
    }


}

