package net.rytale.pixelart.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.rytale.pixelart.layers.PixelCanvas;
import net.rytale.pixelart.utils.AppLogger;

public class ToolLoader {
    private static final Logger logger = AppLogger.getLogger();
    public static final String APP_DATA_DIRECTORY = System.getProperty("user.home") + File.separator + "RytalePixels";
    public static final String TOOL_DIRECTORY = APP_DATA_DIRECTORY + File.separator + "icons";
    private static final String TOOL_PACKAGE = "net.rytale.pixelart.tools.tool";
    private Map<String, Class<? extends AbstractDrawingTool>> tools = new HashMap<>();

    public ToolLoader() {
        createDirectories();
        copyDefaultTools();
        loadTools();
    }

    private void createDirectories() {
        File toolDir = new File(TOOL_DIRECTORY);

        if (!toolDir.exists() && toolDir.mkdirs()) {
            logger.info("Created directory: " + TOOL_DIRECTORY);
        }
    }

    private void copyDefaultTools() {
        String[] toolNames = {"BucketFillTool", "EraserTool", "LineTool", "PencilTool", "RectangleTool"};

        for (String toolName : toolNames) {
            String classFilePath = TOOL_DIRECTORY + File.separator + TOOL_PACKAGE.replace(".", File.separator) + File.separator + toolName + ".class";
            File classFile = new File(classFilePath);

            if (!classFile.exists()) {
                try {
                    copyClassFileFromResources(toolName + ".class", classFilePath);
                } catch (IOException e) {
                    logger.severe("Failed to copy class file for " + toolName + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void copyClassFileFromResources(String resourceName, String destinationPath) throws IOException {
        String resourcePath = "/" + TOOL_PACKAGE.replace(".", "/") + "/" + resourceName;
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);

        if (inputStream != null) {
            File destinationFile = new File(destinationPath);
            destinationFile.getParentFile().mkdirs(); // Ensure the directories exist

            if (!destinationFile.exists()) {
                try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
                    Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    logger.info("Copied " + resourceName + " to " + destinationPath);
                }
            } else {
                logger.info("File already exists: " + destinationPath + " - Skipping copy.");
            }
        } else {
            logger.severe("Resource not found: " + resourcePath);
        }
    }

    private void loadTools() {
        try {
            File toolDir = new File(TOOL_DIRECTORY);

            if (toolDir.exists() && toolDir.isDirectory()) {
                URL[] urls = {toolDir.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);

                File baseDir = new File(toolDir, TOOL_PACKAGE.replace(".", File.separator));
                if (baseDir.exists() && baseDir.isDirectory()) {
                    File[] toolFiles = baseDir.listFiles((dir, name) -> name.endsWith(".class"));
                    if (toolFiles != null) {
                        for (File file : toolFiles) {
                            String className = TOOL_PACKAGE + "." + file.getName().replace(".class", "");
                            logger.info("Attempting to load class: " + className);
                            Class<?> clazz = Class.forName(className, true, classLoader);
                            if (AbstractDrawingTool.class.isAssignableFrom(clazz)) {
                                tools.put(className, (Class<? extends AbstractDrawingTool>) clazz);
                                logger.info("Loaded tool: " + className);
                            }
                        }
                    } else {
                        logger.warning("No tool files found in directory: " + baseDir.getAbsolutePath());
                    }
                } else {
                    logger.warning("Tool package directory does not exist: " + baseDir.getAbsolutePath());
                }
            } else {
                logger.warning("Tool directory does not exist: " + TOOL_DIRECTORY);
            }
        } catch (Exception e) {
            logger.severe("Failed to load tools: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public AbstractDrawingTool createTool(String fullyQualifiedName, PixelCanvas canvas, javafx.scene.paint.Color color) {
        logger.info("Attempting to create tool: " + fullyQualifiedName);
        Class<? extends AbstractDrawingTool> toolClass = tools.get(fullyQualifiedName);
        if (toolClass != null) {
            try {
                return toolClass.getConstructor(PixelCanvas.class, javafx.scene.paint.Color.class).newInstance(canvas, color);
            } catch (Exception e) {
                logger.severe("Failed to create tool: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            logger.warning("Tool not found: " + fullyQualifiedName);
        }
        return null;
    }

    public Map<String, Class<? extends AbstractDrawingTool>> getTools() {
        return tools;
    }
}

