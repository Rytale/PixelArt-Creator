package net.rytale.pixelart.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.*;
import net.rytale.pixelart.utils.AppLogger;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class MainWindow extends Application {
    private static final Logger logger = AppLogger.getLogger();
    private ToolLoader toolLoader;
    private AbstractDrawingTool activeTool;
    private ColorPicker colorPicker;
    private PixelCanvas pixelCanvas;
    private Label coordinateLabel;
    private VBox toolOptionsPanel;
    private VBox toolBox;

    private VBox settingsPanel;  // The settings panel to toggle visibility
    private BorderPane rootPane;  // Store reference to the root BorderPane

    @Override
    public void start(Stage primaryStage) {
        try {
            pixelCanvas = new PixelCanvas(32, 32, 16);  // 32x32 grid with an initial grid size of 16 pixels
            colorPicker = new ColorPicker(Color.BLACK); // Start with black color

            toolLoader = new ToolLoader();

            // Initialize with the first available tool, if any
            Map<String, Class<? extends AbstractDrawingTool>> tools = toolLoader.getTools();
            if (!tools.isEmpty()) {
                String firstToolName = tools.keySet().iterator().next();
                activeTool = toolLoader.createTool(firstToolName, pixelCanvas, colorPicker.getValue());
            }

            colorPicker.setOnAction(e -> {
                if (activeTool != null) {
                    activeTool.setColor(colorPicker.getValue());
                }
            });

            toolBox = new VBox(10);
            toolBox.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10;");
            updateToolSelector();

            VBox controls = createControls();

            CheckBox gridToggle = new CheckBox("Show Grid");
            gridToggle.setSelected(true);
            gridToggle.setStyle("-fx-text-fill: #fff;");
            gridToggle.setOnAction(e -> pixelCanvas.toggleGridVisibility());
            controls.getChildren().add(gridToggle);

            coordinateLabel = new Label("Coordinates: (0, 0)");
            coordinateLabel.setPadding(new Insets(5));
            coordinateLabel.setStyle("-fx-background-color: #333; -fx-text-fill: #ddd; -fx-border-color: #555;");

            toolOptionsPanel = createToolOptionsPanel();
            updateToolOptionsPanel();

            VBox mainMenu = new VBox(10, controls);
            mainMenu.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10;");

            // Set up the root pane
            rootPane = new BorderPane();
            rootPane.setTop(createTopMenu());
            rootPane.setLeft(createRoundedContainer(toolBox));
            rootPane.setCenter(createRoundedContainer(pixelCanvas));
            rootPane.setRight(createRoundedContainer(toolOptionsPanel));
            rootPane.setBottom(createRoundedContainer(coordinateLabel));
            BorderPane.setAlignment(coordinateLabel, Pos.CENTER);

            pixelCanvas.setOnMousePressed(this::handleMousePressed);
            pixelCanvas.setOnMouseDragged(this::handleMouseDragged);
            pixelCanvas.setOnMouseReleased(this::handleMouseReleased);
            pixelCanvas.setOnMouseMoved(this::handleMouseMoved);

            Scene scene = new Scene(rootPane, 1200, 600);
            applyDarkTheme(scene);  // Apply dark theme initially

            primaryStage.setTitle("Pixel Art Creator");
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest((WindowEvent event) -> {
                logger.info("Application is closing.");
                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();

            logger.info("Pixel Art Creator started successfully.");
        } catch (Exception e) {
            logger.severe("Failed to start application: " + e.getMessage());

        }
    }

    private HBox createTopMenu() {
        HBox topMenu = new HBox(10);
        topMenu.setAlignment(Pos.CENTER_LEFT);
        topMenu.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10; -fx-text-fill: #fff;");

        Label titleLabel = new Label("Pixel Art Creator");
        titleLabel.setStyle("-fx-text-fill: #fff;");

        // Add a spacer to push the icon to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Load the settings icon and add it to the menu
        ImageView settingsIcon = loadSettingsIcon();
        if (settingsIcon != null) {
            settingsIcon.setOnMouseClicked(event -> openSettingsSidebar());
            topMenu.getChildren().addAll(titleLabel, spacer, settingsIcon);  // Add spacer before the icon to keep it on the right
        } else {
            topMenu.getChildren().add(titleLabel);
        }

        return topMenu;
    }

    private ImageView loadSettingsIcon() {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/settings.png")));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            imageView.setStyle("-fx-cursor: hand;");  // Make it clear it's clickable
            return imageView;
        } catch (Exception e) {
            logger.warning("Failed to load settings icon: " + e.getMessage());
            return null;
        }
    }

    private void applyDarkTheme(Scene scene) {
        scene.getRoot().setStyle("-fx-background-color: #2C2C2C;");
    }

    private void openSettingsSidebar() {
        if (settingsPanel == null) {
            // Create the settings panel if it doesn't exist
            settingsPanel = createControls();
            settingsPanel.setStyle("-fx-background-color: #2C2C2C; -fx-padding: 10; -fx-background-radius: 10;");
            settingsPanel.setPrefWidth(300);
            settingsPanel.setVisible(false); // Start as hidden
        }

        if (settingsPanel.getParent() == null) {
            // Add the panel to the right side of the BorderPane if it's not already added
            rootPane.setRight(settingsPanel);
        }

        // Toggle visibility
        settingsPanel.setVisible(!settingsPanel.isVisible());
    }

    private VBox createToolOptionsPanel() {
        VBox toolOptionsPanel = new VBox(10);
        toolOptionsPanel.setPadding(new Insets(10));
        toolOptionsPanel.setStyle("-fx-background-color: #3A3A3A; -fx-background-radius: 10; -fx-border-color: #555;");
        toolOptionsPanel.setStyle("-fx-text-fill: #fff;");

        double fixedWidth = 300;
        toolOptionsPanel.setPrefWidth(fixedWidth);
        toolOptionsPanel.setMinWidth(fixedWidth);
        toolOptionsPanel.setMaxWidth(fixedWidth);

        return toolOptionsPanel;
    }

    private void updateToolOptionsPanel() {
        toolOptionsPanel.getChildren().clear();

        if (activeTool != null) {
            VBox optionsPanel = activeTool.createOptionsPanel();
            if (optionsPanel != null) {
                toolOptionsPanel.getChildren().add(optionsPanel);
            } else {
                logger.warning("No options panel provided for the active tool.");
            }
        } else {
            logger.warning("No active tool selected.");
        }
    }

    private void updateToolSelector() {
        toolBox.getChildren().clear();

        Map<String, Class<? extends AbstractDrawingTool>> tools = toolLoader.getTools();

        tools.forEach((toolName, toolClass) -> {
            Button toolButton = new Button(toolClass.getSimpleName());
            toolButton.setStyle("-fx-background-color: #555; -fx-text-fill: #fff;");
            toolButton.setOnAction(e -> {
                activeTool = toolLoader.createTool(toolName, pixelCanvas, colorPicker.getValue());
                if (activeTool != null) {
                    logger.info("Selected tool: " + toolName);
                    updateToolOptionsPanel();
                } else {
                    logger.warning("Failed to select tool: " + toolName);
                }
            });
            toolBox.getChildren().add(toolButton);
        });

        toolBox.getChildren().add(colorPicker);
    }

    private VBox createControls() {
        Label widthLabel = new Label("Canvas Width:");
        widthLabel.setStyle("-fx-text-fill: #fff;");
        TextField widthField = new TextField("32");
        widthField.setStyle("-fx-background-color: #333; -fx-text-fill: #fff;");

        Label heightLabel = new Label("Canvas Height:");
        heightLabel.setStyle("-fx-text-fill: #fff;");
        TextField heightField = new TextField("32");
        heightField.setStyle("-fx-background-color: #333; -fx-text-fill: #fff;");

        Label zoomLabel = new Label("Zoom:");
        zoomLabel.setStyle("-fx-text-fill: #fff;");
        Slider zoomSlider = new Slider(0.5, 2.0, 1.0); // Range from 50% to 300% zoom
        zoomSlider.setStyle("-fx-control-inner-background: #333; -fx-text-fill: #fff;");
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setMajorTickUnit(0.5);
        zoomSlider.setBlockIncrement(0.1);

        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            pixelCanvas.setZoomLevel(newValue.doubleValue());
        });

        Button applyButton = new Button("Apply");
        applyButton.setStyle("-fx-background-color: #555; -fx-text-fill: #fff;");
        applyButton.setOnAction(e -> {
            try {
                int newWidth = Integer.parseInt(widthField.getText());
                int newHeight = Integer.parseInt(heightField.getText());

                pixelCanvas.resizeCanvasTo(newWidth, newHeight);

            } catch (Exception ex) {
                logger.severe("Failed to apply settings: " + ex.getMessage());
            }
        });

        VBox controls = new VBox(10, widthLabel, widthField, heightLabel, heightField, zoomLabel, zoomSlider, applyButton);
        controls.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10;");
        return controls;
    }

    private VBox createRoundedContainer(javafx.scene.Node content) {
        VBox container = new VBox(content);
        container.setPadding(new Insets(10));
        container.setStyle(
                "-fx-background-color: #2C2C2C;" +               // Background color
                        "-fx-background-radius: 10;" +                   // Rounded corners
                        "-fx-border-color: #00BFFF;" +                   // Border color (Deep Sky Blue)
                        "-fx-border-width: 2;" +                         // Border width
                        "-fx-border-radius: 10;" +                       // Rounded border
                        "-fx-effect: dropshadow(gaussian, #00BFFF, 10, 0, 0, 0);" // Glowing blue shadow effect
        );
        return container;
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && activeTool != null) {
            int[] gridCoords = pixelCanvas.mapMouseToGrid(event);
            activeTool.onMousePressed(gridCoords[0], gridCoords[1]);
            handleMouseMoved(event); // Update coordinates on mouse press
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && activeTool != null) {
            int[] gridCoords = pixelCanvas.mapMouseToGrid(event);
            activeTool.onMouseDragged(gridCoords[0], gridCoords[1]);
            handleMouseMoved(event); // Update coordinates on mouse drag
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && activeTool != null) {
            int[] gridCoords = pixelCanvas.mapMouseToGrid(event);
            activeTool.onMouseReleased(gridCoords[0], gridCoords[1]);
            handleMouseMoved(event); // Update coordinates on mouse release
        }
    }

    private void handleMouseMoved(MouseEvent event) {
        int[] gridCoords = pixelCanvas.mapMouseToGrid(event);

        int x = gridCoords[0] + 1;
        int y = pixelCanvas.getCanvasHeight() - gridCoords[1]; // Updated to reflect new grid system

        coordinateLabel.setText(String.format("Coordinates: (%d, %d)", x, y));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
