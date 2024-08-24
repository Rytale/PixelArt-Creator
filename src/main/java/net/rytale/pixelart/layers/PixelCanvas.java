package net.rytale.pixelart.layers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;

public class PixelCanvas extends StackPane {
    private int canvasWidth;  // Number of columns (width in grid units)
    private int canvasHeight; // Number of rows (height in grid units)
    private int gridSize;     // Size of each grid cell in pixels
    private double zoomLevel; // Zoom level, where 1.0 is 100% (no zoom)
    private final double minZoomLevel = 0.5;  // Minimum zoom level (50%)
    private final double maxZoomLevel = 2.0;  // Maximum zoom level (200%)

    private boolean showGrid; // Flag to control grid visibility
    private WritableImage canvasSnapshot; // To store canvas state

    private final Canvas canvas;       // Main drawing canvas
    private final Canvas gridCanvas;   // Canvas for the grid overlay
    private final Canvas overlayCanvas; // Canvas for the preview overlay
    private Color backgroundColor;     // Background color of the canvas

    public PixelCanvas(int width, int height, int gridSize) {
        this.canvasWidth = width;  // Number of columns
        this.canvasHeight = height; // Number of rows
        this.gridSize = gridSize;  // Size of each cell in pixels
        this.zoomLevel = 1.0;  // Start with no zoom (100%)

        this.showGrid = true;
        this.backgroundColor = Color.WHITE;

        this.canvas = new Canvas(width * gridSize, height * gridSize);
        this.gridCanvas = new Canvas(width * gridSize, height * gridSize);
        this.overlayCanvas = new Canvas(width * gridSize, height * gridSize);
        this.canvasSnapshot = new WritableImage(width * gridSize, height * gridSize);

        // Add the canvases to the stack pane
        this.getChildren().addAll(canvas, gridCanvas, overlayCanvas);
        clearCanvas();
        drawGrid();

        // Center the canvas within its parent
        setAlignment(Pos.CENTER);

        // Ensure the canvas stays centered and updates correctly
        widthProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize());
    }
    public WritableImage getCanvasSnapshot() {
        return canvasSnapshot;
    }
    private void updateCanvasSize() {
        double scaledWidth = canvasWidth * gridSize * zoomLevel;
        double scaledHeight = canvasHeight * gridSize * zoomLevel;

        canvas.setWidth(scaledWidth);
        canvas.setHeight(scaledHeight);
        gridCanvas.setWidth(scaledWidth);
        gridCanvas.setHeight(scaledHeight);
        overlayCanvas.setWidth(scaledWidth);
        overlayCanvas.setHeight(scaledHeight);

        redrawGrid(); // Ensure grid is redrawn after resizing
        restoreCanvasContent(); // Restore canvas content after resize
    }


    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = Math.max(minZoomLevel, Math.min(maxZoomLevel, zoomLevel));  // Constrain zoom level
        updateCanvasSize();  // Update the canvas size and redraw content based on zoom level
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        saveCanvasSnapshot();  // Save the cleared state
    }

    public void drawPixel(int x, int y, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);

        // Calculate the position and size based on the zoom level
        double zoomedGridSize = gridSize * zoomLevel;
        double xPos = x * zoomedGridSize;
        double yPos = y * zoomedGridSize;

        // Draw the pixel using logical grid positions
        gc.fillRect(xPos, yPos, zoomedGridSize, zoomedGridSize);
        saveCanvasSnapshot();
    }

    // Save the current state of the canvas
    public void saveCanvasSnapshot() {
        canvas.snapshot(null, canvasSnapshot);
    }

    // Restore the canvas content from the saved snapshot
    public void restoreCanvasContent() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(canvasSnapshot, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // Method to get the graphics context for drawing previews on the overlay canvas
    public GraphicsContext getOverlayGraphicsContext() {
        return overlayCanvas.getGraphicsContext2D();
    }

    // Method to clear the overlay canvas
    public void clearOverlay() {
        getOverlayGraphicsContext().clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
    }

    public int[] mapMouseToGrid(MouseEvent event) {
        // Get the local X and Y coordinates, adjusting for zoom
        double localX = canvas.sceneToLocal(event.getSceneX(), event.getSceneY()).getX() / (gridSize * zoomLevel);
        double localY = canvas.sceneToLocal(event.getSceneX(), event.getSceneY()).getY() / (gridSize * zoomLevel);

        int gridX = (int) localX;
        int gridY = (int) localY;

        // Ensure the coordinates stay within the canvas bounds
        gridX = Math.max(0, Math.min(gridX, canvasWidth - 1));
        gridY = Math.max(0, Math.min(gridY, canvasHeight - 1));

        return new int[]{gridX, gridY};
    }

    public void drawGrid() {
        if (!showGrid) return;

        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1 / zoomLevel);

        double zoomedGridSize = gridSize * zoomLevel;

        for (int x = 0; x <= canvasWidth; x++) {
            gc.strokeLine(x * zoomedGridSize, 0, x * zoomedGridSize, canvasHeight * zoomedGridSize);
        }

        for (int y = 0; y <= canvasHeight; y++) {
            gc.strokeLine(0, y * zoomedGridSize, canvasWidth * zoomedGridSize, y * zoomedGridSize);
        }
    }

    public int getGridSize() {
        return gridSize;
    }

    public void redrawGrid() {
        if (showGrid) {
            drawGrid();
        } else {
            gridCanvas.getGraphicsContext2D().clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
        }
    }

    public void toggleGridVisibility() {
        this.showGrid = !this.showGrid;
        redrawGrid();
    }

    public boolean isGridVisible() {
        return showGrid;
    }
    public void resizeCanvasTo(int newWidth, int newHeight) {
        this.canvasWidth = newWidth;
        this.canvasHeight = newHeight;

        double scaledWidth = canvasWidth * gridSize * zoomLevel;
        double scaledHeight = canvasHeight * gridSize * zoomLevel;

        canvas.setWidth(scaledWidth);
        canvas.setHeight(scaledHeight);
        gridCanvas.setWidth(scaledWidth);
        gridCanvas.setHeight(scaledHeight);
        overlayCanvas.setWidth(scaledWidth);
        overlayCanvas.setHeight(scaledHeight);

        // Clear the canvas and grid after resizing
        clearCanvas();
        redrawGrid();

        // Restore any previous canvas content if needed
        restoreCanvasContent();
    }

    public Color getPixelColor(int x, int y) {
        return canvasSnapshot.getPixelReader().getColor((int)(x * gridSize * zoomLevel), (int)(y * gridSize * zoomLevel));
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

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        clearCanvas();
        redrawGrid();
    }
}
