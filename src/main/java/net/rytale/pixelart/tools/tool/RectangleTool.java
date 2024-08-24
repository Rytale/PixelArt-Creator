package net.rytale.pixelart.tools.tool;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.AbstractDrawingTool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class RectangleTool extends AbstractDrawingTool {

    private boolean debugMode;
    private String currentMode;
    private Color secondaryColor;

    private ColorPicker secondaryColorPicker;
    private final TextArea debugTextArea;
    private VBox customFillControls;
    private VBox debugBox;

    private ImageView modeIconView;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LocalDateTime lastPreviewLogTime = LocalDateTime.MIN;
    private static final int LOG_INTERVAL_SECONDS = 1; // Log every 1 second

    private static final double PREVIEW_TRANSPARENCY = 0.5; // Unified transparency level

    public RectangleTool(PixelCanvas canvas, Color color) {
        super(canvas, color);
        this.debugMode = false; // Default value
        this.currentMode = "Solid"; // Default mode
        this.secondaryColor = Color.LIGHTBLUE; // Default secondary color

        // Initialize and style the debug TextArea
        this.debugTextArea = createDebugTextArea();
    }

    private TextArea createDebugTextArea() {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(100); // Set a preferred height for the TextArea

        // Apply the dark theme styling and scrollbar customization
        textArea.setStyle(
                "-fx-control-inner-background: #1e1e1e; " +
                        "-fx-background-color: #1e1e1e; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-highlight-fill: #333; " +
                        "-fx-highlight-text-fill: #FFFFFF; " +
                        "-fx-border-color: #444; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        // Custom scrollbar styling
                        "-fx-scrollbar-thumb: #0078D7; " +  // Blue thumb
                        "-fx-scrollbar-track: #888888; " +  // Gray track
                        "-fx-scrollbar-width: 12px;"        // Wider scrollbar for easier interaction
        );

        // Ensure the background is explicitly set
        textArea.setBackground(new Background(new BackgroundFill(Color.web("#1e1e1e"), CornerRadii.EMPTY, Insets.EMPTY)));

        // Allow the TextArea to resize horizontally and vertically
        VBox.setVgrow(textArea, Priority.ALWAYS);
        textArea.setPrefWidth(400); // Set an initial width
        textArea.setPrefHeight(100); // Set an initial height
        textArea.setWrapText(true); // Wrap text so horizontal scrolling isn't needed as much

        return textArea;
    }

    @Override
    protected void drawPreview(int startX, int startY, int endX, int endY) {
        GraphicsContext gc = canvas.getOverlayGraphicsContext();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);
        int maxX = Math.max(startX, endX);
        int maxY = Math.max(startY, endY);

        int gridSize = canvas.getGridSize();
        double zoom = canvas.getZoomLevel();

        if (currentMode.equals("Hollow")) {
            // Draw the border as individual blocks with the primary color and consistent transparency
            gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), PREVIEW_TRANSPARENCY));
            for (int x = minX; x <= maxX; x++) {
                gc.fillRect(x * gridSize * zoom, minY * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Top edge
                gc.fillRect(x * gridSize * zoom, maxY * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Bottom edge
            }
            for (int y = minY; y <= maxY; y++) {
                gc.fillRect(minX * gridSize * zoom, y * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Left edge
                gc.fillRect(maxX * gridSize * zoom, y * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Right edge
            }

            logPreview("Drawing hollow rectangle preview with blocks as border from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
        } else if (currentMode.equals("Custom Fill")) {
            // Draw the border as individual blocks with the primary color and consistent transparency
            gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), PREVIEW_TRANSPARENCY));
            for (int x = minX; x <= maxX; x++) {
                gc.fillRect(x * gridSize * zoom, minY * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Top edge
                gc.fillRect(x * gridSize * zoom, maxY * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Bottom edge
            }
            for (int y = minY; y <= maxY; y++) {
                gc.fillRect(minX * gridSize * zoom, y * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Left edge
                gc.fillRect(maxX * gridSize * zoom, y * gridSize * zoom, gridSize * zoom, gridSize * zoom); // Right edge
            }

            // Fill the interior with the secondary color and consistent transparency
            gc.setFill(Color.color(secondaryColor.getRed(), secondaryColor.getGreen(), secondaryColor.getBlue(), PREVIEW_TRANSPARENCY));
            for (int x = minX + 1; x < maxX; x++) {
                for (int y = minY + 1; y < maxY; y++) {
                    gc.fillRect(x * gridSize * zoom, y * gridSize * zoom, gridSize * zoom, gridSize * zoom);
                }
            }

            logPreview("Drawing custom fill rectangle preview with blocks as border from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
        } else {
            // Solid Mode
            gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), PREVIEW_TRANSPARENCY));
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    gc.fillRect(x * gridSize * zoom, y * gridSize * zoom, gridSize * zoom, gridSize * zoom);
                }
            }
            logPreview("Drawing filled rectangle preview from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
        }
    }

    @Override
    protected void drawFinal(int startX, int startY, int endX, int endY) {
        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);
        int maxX = Math.max(startX, endX);
        int maxY = Math.max(startY, endY);

        String message = "Final Draw: ";
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (currentMode.equals("Hollow")) {
                    if (x == minX || x == maxX || y == minY || y == maxY) {
                        canvas.drawPixel(x, y, color); // Draw the border with the primary color as blocks
                    }
                } else if (currentMode.equals("Custom Fill")) {
                    if (x == minX || x == maxX || y == minY || y == maxY) {
                        canvas.drawPixel(x, y, color); // Draw the border with the primary color as blocks
                    } else {
                        canvas.drawPixel(x, y, secondaryColor); // Fill the interior with the secondary color
                    }
                } else {
                    // Solid Mode
                    canvas.drawPixel(x, y, color); // Draw a filled rectangle with the primary color
                }
            }
        }

        canvas.clearOverlay();
        message += currentMode + " rectangle with block border from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")";
        logDebug(message);
    }

    @Override
    public VBox createOptionsPanel() {
        VBox optionsPanel = new VBox(10); // Use 10 for spacing
        optionsPanel.setStyle("-fx-padding: 10; -fx-background-color: #2e2e2e; -fx-border-color: #444; -fx-border-width: 1;");

        // Modes Section
        Label modeLabel = new Label("Mode:");
        modeLabel.setTextFill(Color.WHITE);
        ToggleGroup modeGroup = new ToggleGroup();

        RadioButton solidModeButton = createStyledRadioButton("Solid", modeGroup);
        RadioButton hollowModeButton = createStyledRadioButton("Hollow", modeGroup);
        RadioButton customFillModeButton = createStyledRadioButton("Custom Fill", modeGroup);

        solidModeButton.setSelected(true); // Default mode

        modeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String selectedMode = ((RadioButton) newToggle).getText();
                currentMode = selectedMode;
                customFillControls.setVisible(selectedMode.equals("Custom Fill"));
                updateModeIcon();
            }
        });

        // ImageView for displaying mode icons
        modeIconView = new ImageView();
        modeIconView.setFitWidth(70);  // Set width for the icon
        modeIconView.setFitHeight(70); // Set height for the icon
        updateModeIcon();

        // Cycle through modes when the icon is clicked
        modeIconView.setOnMouseClicked(e -> cycleThroughModes(modeGroup));

        // Create a VBox for the buttons
        VBox modeButtons = new VBox(10, solidModeButton, hollowModeButton, customFillModeButton);

        // Create an HBox to combine buttons and icon
        HBox modeAndIcon = new HBox(20, modeButtons, modeIconView); // 20px spacing between buttons and icon
        modeAndIcon.setAlignment(Pos.CENTER_LEFT); // Align the HBox content to the left

        VBox modeSection = new VBox(10, modeLabel, modeAndIcon);
        modeSection.setStyle("-fx-padding: 5; -fx-background-color: #3e3e3e; -fx-border-color: #555; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Custom Fill Section
        customFillControls = new VBox(5); // VBox for custom fill controls
        Label secondaryColorLabel = new Label("Interior Color:");
        secondaryColorLabel.setTextFill(Color.WHITE);
        secondaryColorPicker = new ColorPicker(this.secondaryColor);
        secondaryColorPicker.setStyle(
                "-fx-background-color: #333; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-color-picker-background: #333; " +
                        "-fx-color-picker-border-color: #666; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        secondaryColorPicker.setOnAction(e -> {
            this.secondaryColor = secondaryColorPicker.getValue(); // Update secondaryColor on selection
            logDebug("Interior Color set to: " + secondaryColor);
        });
        secondaryColorPicker.setTooltip(new Tooltip("Choose the interior color for the custom fill mode."));

        customFillControls.getChildren().addAll(secondaryColorLabel, secondaryColorPicker);
        customFillControls.setVisible(false); // Initially hidden unless custom fill mode is selected

        // Debug Mode Section
        Label debugModeLabel = new Label("Debug Mode:");
        debugModeLabel.setTextFill(Color.WHITE);
        ToggleButton debugModeToggle = new ToggleButton("Off");
        debugModeToggle.setSelected(this.debugMode);
        updateToggleButtonStyle(debugModeToggle, this.debugMode);
        debugModeToggle.setOnAction(e -> {
            this.setDebugMode(!this.debugMode);
            debugModeToggle.setText(this.debugMode ? "On" : "Off");
            updateToggleButtonStyle(debugModeToggle, this.debugMode);
            debugBox.setVisible(this.debugMode); // Show/Hide debug logs based on debug mode
            logDebug("Debug Mode " + (this.debugMode ? "enabled" : "disabled"));
        });
        debugModeToggle.setTooltip(new Tooltip("Toggle debug mode for detailed logging."));

        // Add Sections to Options Panel
        optionsPanel.getChildren().addAll(
                createSection("Modes", modeLabel, modeSection),
                customFillControls, // Add custom fill controls
                new Separator(),
                createSection("Debug Mode", debugModeLabel, debugModeToggle),
                new Separator(),
                createDebugBoxSection() // The debug section will be shown or hidden based on debug mode
        );

        return optionsPanel;
    }

    private void updateModeIcon() {
        String iconName = switch (currentMode) {
            case "Solid" -> "solid_icon.png";
            case "Hollow" -> "hollow_icon.png";
            case "Custom Fill" -> "custom_fill_icon.png";
            default -> "";
        };
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/" + iconName)));
        modeIconView.setImage(icon);
    }

    private void cycleThroughModes(ToggleGroup modeGroup) {
        RadioButton currentButton = (RadioButton) modeGroup.getSelectedToggle();
        RadioButton nextButton;

        if (currentButton.getText().equals("Solid")) {
            nextButton = (RadioButton) modeGroup.getToggles().get(1);
        } else if (currentButton.getText().equals("Hollow")) {
            nextButton = (RadioButton) modeGroup.getToggles().get(2);
        } else {
            nextButton = (RadioButton) modeGroup.getToggles().get(0);
        }

        nextButton.setSelected(true); // Select the next button
    }

    private RadioButton createStyledRadioButton(String text, ToggleGroup group) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setTextFill(Color.WHITE);
        radioButton.setToggleGroup(group);
        radioButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #444, #333); " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-color: #666; " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 8 15;" +  // Increased padding for button-like appearance
                        "-fx-cursor: hand;" // Make the cursor a hand pointer when hovering
        );

        // Hide the default indicator (check mark or radio button circle)
        radioButton.getStyleClass().remove("radio-button");
        radioButton.getStyleClass().add("toggle-button");

        // Styling for selected state
        radioButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                radioButton.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #6a6a6a, #505050); " +
                                "-fx-text-fill: #FFFFFF; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-border-color: #888; " +
                                "-fx-border-width: 1; " +
                                "-fx-padding: 8 15;" +  // Increased padding for button-like appearance
                                "-fx-cursor: hand;");
            } else {
                radioButton.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #444, #333); " +
                                "-fx-text-fill: #FFFFFF; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-border-color: #666; " +
                                "-fx-border-width: 1; " +
                                "-fx-padding: 8 15;" +  // Increased padding for button-like appearance
                                "-fx-cursor: hand;");
            }
        });

        return radioButton;
    }

    private VBox createSection(String title, Label label, javafx.scene.Node... controls) {
        VBox section = new VBox(5);
        section.getChildren().addAll(label);
        section.getChildren().addAll(controls);
        section.setStyle("-fx-padding: 5; -fx-background-color: #3e3e3e; -fx-border-color: #555; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        return section;
    }

    private VBox createDebugBoxSection() {
        Label debugBoxLabel = new Label("Debug Output:");
        debugBoxLabel.setTextFill(Color.WHITE);
        debugBox = new VBox(5, debugBoxLabel, debugTextArea); // Reuse the existing styled TextArea
        debugBox.setStyle("-fx-padding: 10; -fx-background-color: #2e2e2e; -fx-border-color: #444; -fx-border-width: 1;");
        VBox.setVgrow(debugBox, Priority.ALWAYS); // Allow the debug box to grow and resize
        debugBox.setVisible(this.debugMode); // Initially hide the debug box if debug mode is off
        return debugBox;
    }

    private void updateToggleButtonStyle(ToggleButton toggleButton, boolean isActive) {
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

    private void logPreview(String message) {
        LocalDateTime now = LocalDateTime.now();
        if (debugMode && now.isAfter(lastPreviewLogTime.plusSeconds(LOG_INTERVAL_SECONDS))) {
            String timestampedMessage = "[" + now.format(formatter) + "] " + message;
            debugTextArea.appendText(timestampedMessage + "\n"); // GUI log
            lastPreviewLogTime = now; // Update the last log time
        }
    }

    private void logDebug(String message) {
        if (debugMode) {
            String timestampedMessage = "[" + LocalDateTime.now().format(formatter) + "] " + message;
            debugTextArea.appendText(timestampedMessage + "\n"); // GUI log
            System.out.println(timestampedMessage); // Console log
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        logDebug("Debug Mode " + (debugMode ? "enabled" : "disabled"));
    }
}