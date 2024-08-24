package net.rytale.pixelart.tools.tool;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.AbstractDrawingTool;

import java.util.LinkedList;
import java.util.Queue;

public class BucketFillTool extends AbstractDrawingTool {

    public BucketFillTool(PixelCanvas canvas, Color color) {
        super(canvas, color);
    }

    @Override
    protected void drawPreview(int startX, int startY, int endX, int endY) {
        // No preview needed for bucket fill.
    }

    @Override
    protected void drawFinal(int startX, int startY, int endX, int endY) {
        fillArea(startX, startY, color);
        if (canvas.isGridVisible()) {  // Ensure grid is drawn after the fill operation if grid is visible
            canvas.redrawGrid();  // Redraw the grid overlay
        }
    }

    @Override
    public VBox createOptionsPanel() {
        return null;
    }



    private void fillArea(int x, int y, Color newColor) {
        Color targetColor = canvas.getPixelColor(x, y);
        if (targetColor.equals(newColor)) {
            return; // No need to fill if the target color is the same as the fill color.
        }

        int width = (int) canvas.getCanvasWidth();
        int height = (int) canvas.getCanvasHeight();
        boolean[][] processed = new boolean[width][height]; // Track processed pixels

        Queue<int[]> pixelsQueue = new LinkedList<>();
        pixelsQueue.add(new int[]{x, y});

        while (!pixelsQueue.isEmpty()) {
            int[] pixel = pixelsQueue.poll();
            int px = pixel[0];
            int py = pixel[1];

            if (px < 0 || px >= width || py < 0 || py >= height || processed[px][py]) {
                continue; // Skip pixels that are out of bounds or already processed.
            }

            if (canvas.getPixelColor(px, py).equals(targetColor)) {
                canvas.drawPixel(px, py, newColor);
                processed[px][py] = true; // Mark as processed

                // Add neighboring pixels to the queue
                pixelsQueue.add(new int[]{px + 1, py});
                pixelsQueue.add(new int[]{px - 1, py});
                pixelsQueue.add(new int[]{px, py + 1});
                pixelsQueue.add(new int[]{px, py - 1});
            }
        }
    }
}

