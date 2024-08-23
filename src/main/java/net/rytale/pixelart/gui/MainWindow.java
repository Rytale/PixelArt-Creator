package net.rytale.pixelart.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.tools.*;
import net.rytale.pixelart.utils.AppLogger;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Map;
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

    @Override
    public void start(Stage primaryStage) {
        try {
            pixelCanvas = new PixelCanvas(32, 32, 16);  // 32x32 grid with an initial grid size of 16 pixels
            colorPicker = new ColorPicker(Color.BLACK); // Start with a bright color

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
            toolBox.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);");
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

            Button importToolsButton = new Button("Import Tools");
            importToolsButton.setStyle("-fx-background-color: #555; -fx-text-fill: #fff;");
            importToolsButton.setOnAction(e -> openToolsDirectory());

            VBox mainMenu = new VBox(10, importToolsButton);
            mainMenu.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);");

            BorderPane root = new BorderPane();
            root.setTop(createTopMenu());
            root.setLeft(createRoundedContainer(toolBox));
            root.setCenter(createRoundedContainer(pixelCanvas));
            root.setRight(createRoundedContainer(toolOptionsPanel));
            root.setBottom(createRoundedContainer(coordinateLabel));
            BorderPane.setAlignment(coordinateLabel, Pos.CENTER);

            pixelCanvas.setOnMousePressed(this::handleMousePressed);
            pixelCanvas.setOnMouseDragged(this::handleMouseDragged);
            pixelCanvas.setOnMouseReleased(this::handleMouseReleased);
            pixelCanvas.setOnMouseMoved(this::handleMouseMoved);

            Scene scene = new Scene(root, 1200, 600);
            applyDarkTheme(scene);  // Apply dark theme initially

            // Resize listener to handle window resizing
            root.widthProperty().addListener((obs, oldVal, newVal) -> pixelCanvas.resizeCanvas((int) newVal, (int) root.getHeight()));
            root.heightProperty().addListener((obs, oldVal, newVal) -> pixelCanvas.resizeCanvas((int) root.getWidth(), (int) newVal));

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
            e.printStackTrace();
        }
    }

    private HBox createTopMenu() {
        HBox topMenu = new HBox(10, new Label("Pixel Art Creator"));
        topMenu.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10; -fx-text-fill: #fff;");
        return topMenu;
    }

    private void applyDarkTheme(Scene scene) {
        scene.getRoot().setStyle("-fx-background-color: #2C2C2C;");
    }

    private void openToolsDirectory() {
        try {
            File toolDir = new File(ToolLoader.TOOL_DIRECTORY);
            if (!toolDir.exists()) {
                toolDir.mkdirs();
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(toolDir);
            } else {
                logger.warning("Desktop operations are not supported on this system.");
            }
        } catch (IOException e) {
            logger.severe("Failed to open tools directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createToolOptionsPanel() {
        VBox toolOptionsPanel = new VBox(10);
        toolOptionsPanel.setPadding(new Insets(10));
        toolOptionsPanel.setStyle("-fx-background-color: #3A3A3A; -fx-background-radius: 10; -fx-border-color: #555;");

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
        Slider widthSlider = new Slider(16, 64, 32);
        widthSlider.setMajorTickUnit(8);
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.setStyle("-fx-control-inner-background: #333; -fx-text-fill: #fff;");

        Label heightLabel = new Label("Canvas Height:");
        heightLabel.setStyle("-fx-text-fill: #fff;");
        Slider heightSlider = new Slider(16, 64, 32);
        heightSlider.setMajorTickUnit(8);
        heightSlider.setShowTickMarks(true);
        heightSlider.setShowTickLabels(true);
        heightSlider.setStyle("-fx-control-inner-background: #333; -fx-text-fill: #fff;");

        Label gridSizeLabel = new Label("Grid Size:");
        gridSizeLabel.setStyle("-fx-text-fill: #fff;");
        Slider gridSizeSlider = new Slider(4, 32, 16);
        gridSizeSlider.setMajorTickUnit(4);
        gridSizeSlider.setShowTickMarks(true);
        gridSizeSlider.setShowTickLabels(true);
        gridSizeSlider.setStyle("-fx-control-inner-background: #333; -fx-text-fill: #fff;");

        Button applyButton = new Button("Apply");
        applyButton.setStyle("-fx-background-color: #555; -fx-text-fill: #fff;");
        applyButton.setOnAction(e -> {
            try {
                int newWidth = (int) widthSlider.getValue();
                int newHeight = (int) heightSlider.getValue();
                int newGridSize = (int) gridSizeSlider.getValue();

                pixelCanvas.setGridSize(newGridSize);
                pixelCanvas.resizeCanvas(newWidth * newGridSize, newHeight * newGridSize);
            } catch (Exception ex) {
                logger.severe("Failed to apply settings: " + ex.getMessage());
            }
        });

        VBox controls = new VBox(10, widthLabel, widthSlider, heightLabel, heightSlider, gridSizeLabel, gridSizeSlider, applyButton);
        controls.setStyle("-fx-padding: 10; -fx-background-color: #2C2C2C; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);");
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

        int canvasWidth = (int) pixelCanvas.getWidth();
        int canvasHeight = (int) pixelCanvas.getHeight();
        int gridSize = pixelCanvas.getGridSize();

        int x = gridCoords[0] + 1;
        int y = (int) (canvasHeight / gridSize) - gridCoords[1];

        coordinateLabel.setText(String.format("Coordinates: (%d, %d)", x, y));
    }

    public static void main(String[] args) {
        launch(args);
    }
}


