package net.rytale.pixelart.tools.tool;

import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.AbstractDrawingTool;

public class LineTool extends AbstractDrawingTool {
    // Existing fields
    private int lineWidth = 1;
    private boolean dashed = false;
    private double dashGap = 5.0;
    private Color secondaryColor = Color.TRANSPARENT;
    private boolean fillHollow = false;
    private boolean debugMode;

    // UI components for options panel
    private ToggleButton fillHollowToggle;
    private ColorPicker secondaryColorPicker;

    // Constructor
    public LineTool(PixelCanvas canvas, Color color) {
        super(canvas, color);
    }

    // Set line width
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        boolean canFillHollow = lineWidth > 1;
        fillHollowToggle.setDisable(!canFillHollow);
        secondaryColorPicker.setDisable(!canFillHollow);
        if (!canFillHollow) {
            setFillHollow(false);
        }
    }

    // Set dashed line option
    public void setDashed(boolean dashed) {
        this.dashed = dashed;
    }

    // Set dash gap
    public void setDashGap(double dashGap) {
        this.dashGap = dashGap;
    }

    // Set secondary color
    public void setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    // Set fill hollow option
    public void setFillHollow(boolean fillHollow) {
        this.fillHollow = fillHollow;
        updateToggleButton(fillHollowToggle, fillHollow);
    }

    // Draw the final line
    @Override
    protected void drawFinal(int startX, int startY, int endX, int endY) {
        // Draw the final line on the main canvas
        drawLine(startX, startY, endX, endY, this.color, this.lineWidth, this.dashed, this.dashGap, false);

        // If fillHollow is enabled and a secondary color is set, draw the inner line
        if (this.fillHollow && !this.secondaryColor.equals(Color.TRANSPARENT)) {
            drawLine(startX, startY, endX, endY, this.secondaryColor, this.lineWidth - 2, this.dashed, this.dashGap, false);
        }
    }

    // Handle mouse drag events to preview the line
    @Override
    public void onMouseDragged(int gridX, int gridY) {
        if (this.debugMode) {
            System.out.println("Dragging at: (" + gridX + ", " + gridY + ")");
        }
        // Clear the previous preview from the overlay canvas
        this.canvas.clearOverlay();

        // Draw a preview of the line on the overlay canvas
        drawPreview(this.startX, this.startY, gridX, gridY);
    }

    // Handle mouse press events to start drawing the line
    @Override
    public void onMousePressed(int gridX, int gridY) {
        super.onMousePressed(gridX, gridY);
        if (this.debugMode) {
            System.out.println("Pressed at: (" + gridX + ", " + gridY + ")");
        }
    }

    // Handle mouse release events to finish drawing the line
    @Override
    public void onMouseReleased(int gridX, int gridY) {
        if (this.debugMode) {
            System.out.println("Released at: (" + gridX + ", " + gridY + ")");
        }

        // Clear the preview when drawing is finished
        this.canvas.clearOverlay();

        // Draw the final line on the main canvas
        this.drawFinal(this.startX, this.startY, gridX, gridY);
    }

    @Override
    protected void drawPreview(int startX, int startY, int endX, int endY) {
        // Draw the preview of the main line with lower opacity
        Color previewColor = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 0.3); // 30% opacity
        drawLine(startX, startY, endX, endY, previewColor, this.lineWidth, this.dashed, this.dashGap, true);

        // If fillHollow is enabled, draw the secondary color line as a preview
        if (this.fillHollow && !this.secondaryColor.equals(Color.TRANSPARENT)) {
            Color previewSecondaryColor = new Color(this.secondaryColor.getRed(), this.secondaryColor.getGreen(), this.secondaryColor.getBlue(), 0.3); // 30% opacity
            drawLine(startX, startY, endX, endY, previewSecondaryColor, this.lineWidth - 2, this.dashed, this.dashGap, true);
        }
    }

    // Utility method to create a snapping slider with styles
    private Slider createSnappingSlider(int min, int max, double initialValue, int increment) {
        Slider slider = new Slider(min, max, initialValue);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(increment);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(increment);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            slider.setValue(Math.round(newVal.doubleValue()));  // Snap to closest value
        });
        return slider;
    }

    // Utility method to update the style of toggle buttons
    private void updateToggleButton(ToggleButton toggleButton, boolean isActive) {
        toggleButton.setText(isActive ? toggleButton.getText().replace("OFF", "ON") : toggleButton.getText().replace("ON", "OFF"));
        if (isActive) {
            toggleButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #4CAF50, #388E3C); " +
                            "-fx-text-fill: #FFFFFF; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-color: #3B6A40; " +
                            "-fx-border-width: 1; " +
                            "-fx-padding: 5 10;"
            );
        } else {
            toggleButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #D32F2F, #C62828); " +
                            "-fx-text-fill: #FFFFFF; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-color: #C62828; " +
                            "-fx-border-width: 1; " +
                            "-fx-padding: 5 10;"
            );
        }
    }

    // Create the options panel for this tool with added styles
    @Override
    public VBox createOptionsPanel() {
        VBox optionsPanel = new VBox(10);
        optionsPanel.setPadding(new Insets(10));
        optionsPanel.setStyle("-fx-background-color: #3A3A3A; -fx-background-radius: 10; -fx-border-color: #555;");

        // Line Width Slider
        Label lineWidthLabel = new Label("Line Width:");
        lineWidthLabel.setTextFill(Color.WHITE);
        Slider lineWidthSlider = createSnappingSlider(1, 10, this.lineWidth, 1);
        lineWidthSlider.valueProperty().addListener((obs, oldVal, newVal) -> setLineWidth(newVal.intValue()));

        // Dashed Line Toggle
        Label dashedLabel = new Label("Dashed Line:");
        dashedLabel.setTextFill(Color.WHITE);
        ToggleButton dashedToggle = new ToggleButton("Dashed Line: OFF");
        updateToggleButton(dashedToggle, this.dashed);
        dashedToggle.setOnAction(e -> {
            this.dashed = !this.dashed;
            updateToggleButton(dashedToggle, this.dashed);
        });

        // Dash Gap Slider
        Label dashGapLabel = new Label("Dash Gap:");
        dashGapLabel.setTextFill(Color.WHITE);
        Slider dashGapSlider = createSnappingSlider(1, 20, this.dashGap, 1);
        dashGapSlider.valueProperty().addListener((obs, oldVal, newVal) -> setDashGap(newVal.doubleValue()));

        // Secondary Color Picker
        Label secondaryColorLabel = new Label("Secondary Color:");
        secondaryColorLabel.setTextFill(Color.WHITE);
        secondaryColorPicker = new ColorPicker(this.secondaryColor);
        secondaryColorPicker.setDisable(this.lineWidth <= 1);
        secondaryColorPicker.setOnAction(e -> setSecondaryColor(secondaryColorPicker.getValue()));

        // Fill Hollow Toggle
        Label fillHollowLabel = new Label("Fill Hollow:");
        fillHollowLabel.setTextFill(Color.WHITE);
        fillHollowToggle = new ToggleButton("Fill Hollow: OFF");
        fillHollowToggle.setDisable(this.lineWidth <= 1);
        updateToggleButton(fillHollowToggle, this.fillHollow);
        fillHollowToggle.setOnAction(e -> setFillHollow(!this.fillHollow));

        optionsPanel.getChildren().addAll(
                lineWidthLabel, lineWidthSlider,
                dashedLabel, dashedToggle,
                dashGapLabel, dashGapSlider,
                secondaryColorLabel, secondaryColorPicker,
                fillHollowLabel, fillHollowToggle
        );

        return optionsPanel;
    }

    // Utility method to draw a line using Bresenham's algorithm, with support for thickness and dashed lines
    private void drawLine(int startX, int startY, int endX, int endY, Color color, int thickness, boolean dashed, double dashGap, boolean isPreview) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        // Convert dashGap to grid squares
        double gridSize = this.canvas.getGridSize();
        double effectiveDashLength = (dashGap + 2) * gridSize;  // Dash length in terms of grid squares

        boolean draw = true; // Flag to alternate between drawing and not drawing for dashed effect
        double distance = 0;

        while (true) {
            // Draw the thick pixel (dash)
            if (draw) {
                drawThickPixel(startX, startY, color, thickness, isPreview);
            }

            // Check if we've reached the end
            if (startX == endX && startY == endY) {
                break;
            }

            // Update the dash effect
            distance += gridSize; // Increment distance by grid size (per grid square)
            if (dashed) {
                // Switch between drawing and gap
                if (distance >= effectiveDashLength) {
                    draw = !draw; // Toggle drawing
                    distance = 0; // Reset distance counter
                }
            }

            // Bresenham's algorithm step
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

    // Utility method to draw a thick pixel (square) centered on (x, y)
    private void drawThickPixel(int x, int y, Color color, int thickness, boolean isPreview) {
        int radius = thickness / 2;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (isPreview) {
                    this.canvas.getOverlayGraphicsContext().setFill(color);
                    this.canvas.getOverlayGraphicsContext().fillRect((x + i) * this.canvas.getGridSize(), (y + j) * this.canvas.getGridSize(), this.canvas.getGridSize(), this.canvas.getGridSize());
                } else {
                    this.canvas.drawPixel(x + i, y + j, color);
                }
            }
        }
    }
}


