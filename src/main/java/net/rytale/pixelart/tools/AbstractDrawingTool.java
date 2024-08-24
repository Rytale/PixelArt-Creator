package net.rytale.pixelart.tools;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rytale.pixelart.layers.PixelCanvas;

public abstract class AbstractDrawingTool {
    protected PixelCanvas canvas;
    protected Color color;
    protected int startX, startY;

    public AbstractDrawingTool(PixelCanvas canvas, Color color) {
        this.canvas = canvas;
        this.color = color;
    }

    public void onMousePressed(int gridX, int gridY) {
        startX = gridX;
        startY = gridY;
        canvas.saveCanvasSnapshot();
    }

    public void onMouseDragged(int gridX, int gridY) {

        canvas.restoreCanvasContent();
        drawPreview(startX, startY, gridX, gridY);
    }

    public void onMouseReleased(int gridX, int gridY) {

        canvas.restoreCanvasContent();
        drawFinal(startX, startY, gridX, gridY);
    }

    protected abstract void drawPreview(int startX, int startY, int endX, int endY);

    protected abstract void drawFinal(int startX, int startY, int endX, int endY);

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract VBox createOptionsPanel(); // Add this method to create tool-specific options panel


}


