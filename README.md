# PixelArt Tool - Early Development Stage

## Overview
Welcome to the **PixelArt Tool**! This Java-based application, built with JavaFX, is designed for creating and editing pixel art. The tool is in its early development stages, and while it offers some basic features, there is still much to be done. Your contributions and feedback are greatly appreciated as we work to develop this tool.

## Features (So Far)
- **Canvas**: Create and edit pixel art on a simple grid.
- **Basic Tools**:
  - **Line Tool**: Draw lines.
  - **Pen Tool**: Draw individual pixels.
  - **Eraser Tool**: Remove pixels by erasing them from the canvas.
  - **Rectangle Tool**: Draw Rectangles.
  - **Fill Tool**: Fill areas with a selected color.

## Known Issues & Help Needed
- **Zoom Functionality**: Currently facing challenges with implementing zoom functionality. Any assistance or suggestions on how to effectively handle zoom in JavaFX would be greatly appreciated.
- I want to rewrite how the pixels are stored on the canvas for more dynamic usage down the line open to suggestions on this. 

## Planned Features
- **Grid Toggle**: This feature has not yet been implemented but is planned for future updates.
- **Zoom and Grid Toggle**: Improved control over the canvas for detailed editing.
- **Layer Support**: Work with multiple layers to create more complex pixel art.
- **Advanced Tools**: 
  - Shape tools (lines, rectangles, circles, etc.).
  - Selection tools (rectangular, lasso, etc.).
  - Gradient fills and blending options.
- **Custom Palettes**: Create and save custom color palettes.
- **Export Options**: Export your work to various image formats like PNG, GIF, etc.
- **Undo/Redo Functionality**: Implement multi-level undo/redo.
- **Animation Support**: Create and preview pixel art animations.

## Installation
### Prerequisites
- **Java 17 or higher**: Ensure you have the latest version of Java installed.
- **JavaFX SDK**: Required for running JavaFX applications.

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Rytale/PixelArtTool.git
   ```

2. **Build the project**:
   - Use Maven or your preferred build tool to manage dependencies.
   - Ensure that the JavaFX SDK is correctly configured in your build path.

3. **Run the application**:
   - Execute the main class `net.rytale.pixelart.gui.MainWindow` to launch the application.

## Usage

- **Starting a New Project**: Open the application, set your canvas size, and begin drawing using the available tools.
- **Saving Your Work**: Save your project in the tool's native format (to be implemented) or export it as an image file (future feature).

## Contributing

As this project is still in its early stages, contributions are highly encouraged! If you have experience with JavaFX, especially in areas like zooming functionality or grid management, your help would be invaluable. Please check the `CONTRIBUTING.md` file for guidelines on how to contribute.

## Reporting Issues

If you encounter any bugs or have suggestions, please open an issue on the GitHub repository.

## Roadmap

- [ ] Implement zoom functionality (currently in progress, help needed).
- [ ] Add shape tools (rectangles, circles, etc.).
- [ ] Introduce layer support.
- [ ] Enable custom color palettes.
- [ ] Optimize performance for large canvases.

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

## Contact

For any questions, feedback, or support, feel free to contact me at discord: sheet_of_paper.

---

Thank you for trying out the PixelArt Tool. Your feedback is invaluable as we work to improve and expand the tool. Happy pixeling!
