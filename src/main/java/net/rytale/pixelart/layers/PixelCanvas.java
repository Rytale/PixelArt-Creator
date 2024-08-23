package net.rytale.pixelart.layers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PixelCanvas extends StackPane {
    private int canvasWidth;  // Width in grid units
    private int canvasHeight; // Height in grid units
    private int gridSize;     // Size of each grid cell in pixels
    private boolean showGrid; // Flag to control grid visibility
    private boolean debugMode; // Flag to control debug logging

    private final Canvas canvas;       // Main drawing canvas
    private final Canvas gridCanvas;   // Canvas for the grid overlay
    private WritableImage canvasSnapshot;

    private Color backgroundColor;     // Background color of the canvas

    public PixelCanvas(int width, int height, int gridSize) {
        this.canvasWidth = width; // Width in grid units
        this.canvasHeight = height; // Height in grid units
        this.gridSize = gridSize; // Size of each grid cell in pixels
        this.showGrid = true; // Grid is visible by default
        this.debugMode = false; // Debug mode is off by default
        this.backgroundColor = Color.WHITE; // Default background color

        // Set up the canvases with the appropriate pixel dimensions
        this.canvas = new Canvas(width * gridSize, height * gridSize);
        this.gridCanvas = new Canvas(width * gridSize, height * gridSize);
        this.canvasSnapshot = new WritableImage(width * gridSize, height * gridSize);

        // Add both canvases to the StackPane
        this.getChildren().addAll(canvas, gridCanvas);
        clearCanvas();
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        saveCanvasSnapshot();
        restoreCanvasContent();
        redrawGrid(); // Ensure the grid is drawn as an overlay after clearing the canvas
    }

    public void drawPixel(int x, int y, Color color) {
        // Draw a pixel in the correct grid position
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
        saveCanvasSnapshot(); // Save the canvas state after drawing a pixel
        redrawGrid(); // Redraw the grid as an overlay
    }

    public Color getPixelColor(int x, int y) {
        // Return the color of a pixel at the specified grid position
        return canvasSnapshot.getPixelReader().getColor(x * gridSize, y * gridSize);
    }

    public void saveCanvasSnapshot() {
        // Save the current canvas content (without the grid)
        canvas.snapshot(null, canvasSnapshot);
    }

    public void restoreCanvasContent() {
        // Restore the canvas content from the snapshot
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(canvasSnapshot, 0, 0);
    }

    public void drawGrid() {
        if (!showGrid) return; // Skip drawing the grid if it's disabled

        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight()); // Clear previous grid

        gc.setStroke(Color.LIGHTGRAY); // Adjust grid color for better appearance
        gc.setLineWidth(1); // Set a suitable line width

        // Draw vertical grid lines
        for (int x = 0; x <= canvasWidth * gridSize; x += gridSize) {
            gc.strokeLine(x, 0, x, canvasHeight * gridSize);
        }

        // Draw horizontal grid lines
        for (int y = 0; y <= canvasHeight * gridSize; y += gridSize) {
            gc.strokeLine(0, y, canvasWidth * gridSize, y);
        }
    }

    public void redrawGrid() {
        if (showGrid) {
            drawGrid(); // Draw the grid if it should be visible
        } else {
            gridCanvas.getGraphicsContext2D().clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight()); // Clear grid canvas
        }
    }

    public void toggleGridVisibility() {
        this.showGrid = !this.showGrid;
        redrawGrid(); // Redraw the grid based on its new visibility
    }

    public boolean isGridVisible() {
        return showGrid;
    }

    public GraphicsContext getDrawingGraphicsContext() {
        return canvas.getGraphicsContext2D();
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
        resizeCanvas(canvasWidth, canvasHeight); // Adjust the canvas size accordingly
    }

    public void resizeCanvas(int newWidth, int newHeight) {
        this.canvasWidth = newWidth;
        this.canvasHeight = newHeight;

        // Adjust the size of both canvases based on the new grid and canvas sizes
        canvas.setWidth(newWidth * gridSize);
        canvas.setHeight(newHeight * gridSize);
        gridCanvas.setWidth(newWidth * gridSize);
        gridCanvas.setHeight(newHeight * gridSize);

        // Clear and redraw the canvas with the updated dimensions
        clearCanvas();
    }

    // Updated method to accurately map mouse coordinates to grid positions using sceneToLocal conversion
    public int[] mapMouseToGrid(MouseEvent event) {
        // Convert scene coordinates to local coordinates relative to the canvas
        double localX = canvas.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
        double localY = canvas.sceneToLocal(event.getSceneX(), event.getSceneY()).getY();

        // Convert the local coordinates to grid coordinates
        int gridX = (int) (localX / gridSize);
        int gridY = (int) (localY / gridSize);

        // Ensure the coordinates are within the canvas bounds
        gridX = Math.max(0, Math.min(gridX, canvasWidth - 1));
        gridY = Math.max(0, Math.min(gridY, canvasHeight - 1));

        // Debugging output controlled by the debugMode flag
        if (debugMode) {
            System.out.println("Local X: " + localX + ", Local Y: " + localY);
            System.out.println("Grid X: " + gridX + ", Grid Y: " + gridY);
        }

        return new int[]{gridX, gridY};
    }

    // New methods for getting and setting the background color

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        clearCanvas();  // Redraw the canvas with the new background color
    }
}
