package net.rytale.pixelart.tools.tool;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.AbstractDrawingTool;

public class PencilTool extends AbstractDrawingTool {
    private boolean debugMode = false;

    public PencilTool(PixelCanvas canvas, Color color) {
        super(canvas, color);
    }

    // Set the debug mode
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    // The pencil tool does not need a preview, so this method is empty
    @Override
    protected void drawPreview(int startX, int startY, int endX, int endY) {
        // No preview needed for the pencil tool
    }

    // Draw the final pixel at the specified coordinates
    @Override
    protected void drawFinal(int startX, int startY, int endX, int endY) {
        this.canvas.drawPixel(endX, endY, this.color);
    }

    // Handle mouse drag events to draw pixels as the mouse moves
    @Override
    public void onMouseDragged(int gridX, int gridY) {
        if (this.debugMode) {
            System.out.println("Dragged at: (" + gridX + ", " + gridY + ")");
        }
        this.canvas.drawPixel(gridX, gridY, this.color);
    }

    // Handle mouse press events to start drawing
    @Override
    public void onMousePressed(int gridX, int gridY) {
        super.onMousePressed(gridX, gridY);
        if (this.debugMode) {
            System.out.println("Pressed at: (" + gridX + ", " + gridY + ")");
        }
        this.drawFinal(gridX, gridY, gridX, gridY); // Draw the initial point immediately
    }

    // Handle mouse release events to finish drawing
    @Override
    public void onMouseReleased(int gridX, int gridY) {
        if (this.debugMode) {
            System.out.println("Released at: (" + gridX + ", " + gridY + ")");
        }
        this.drawFinal(this.startX, this.startY, gridX, gridY); // Ensure the final point is drawn
    }

    // Create the options panel for this tool with a debug mode toggle
    @Override
    public VBox createOptionsPanel() {
        VBox optionsPanel = new VBox(10); // Use 10 for spacing
        Label debugModeLabel = new Label("Debug Mode:");
        CheckBox debugModeCheckBox = new CheckBox();
        debugModeCheckBox.setSelected(this.debugMode);
        debugModeCheckBox.setOnAction(e -> this.setDebugMode(debugModeCheckBox.isSelected()));
        optionsPanel.getChildren().addAll(debugModeLabel, debugModeCheckBox);
        return optionsPanel;
    }


}

