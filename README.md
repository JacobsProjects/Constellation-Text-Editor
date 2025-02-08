# Constellation Text Editor (Work in Progress)

Constellation is a modern and lightweight text editor designed for a clean and efficient writing experience.  It aims to provide users with a distraction-free environment while still offering essential features. This project is currently a work in progress, and you may encounter bugs.  Your feedback and contributions are welcome!

## Features

*   **Modern Interface:** Constellation boasts a clean and modern look and feel, designed to minimize distractions and maximize your focus on writing.  
    [![Constellation Text Editor Screenshot](https://i.imgur.com/TMPvhoy_d.webp?maxwidth=760&fidelity=grand)](https://i.imgur.com/TMPvhoy_d.webp?maxwidth=760&fidelity=grand)
    
    *A glimpse of the modern interface.*
*   **Syntax Highlighting:** Supports syntax highlighting for Java and Python files.  Basic auto-highlighting is also included for other file types.
*   **Line Numbering:** Displays line numbers for easy navigation and code referencing.  Handles text wrapping correctly.
*   **Find Functionality:**  Includes a basic "Find" feature to locate specific text within the document.
*   **Drag and Drop Support:** Easily open files by dragging and dropping them into the editor.
*   **File Handling:** Supports opening, saving, and saving as for both plain text files and encrypted `.ctxt` files.
*   **Status Bar:**  Provides information about the current file, cursor position (line and column), and file type.
*   **Customizable Opacity:** Adjust the window opacity to your preference.
*   **Text Wrapping:** Toggle text wrapping to control how lines are displayed.
*   **Encrypted File Support (.ctxt):**  Constellation supports reading and writing encrypted files in it's own `.ctxt` extension.
*   **Cross Platform:** Built with JavaFX, allowing for potential cross-platform support (Windows, macOS, Linux).

## Technology Used

*   [JavaFX](https://github.com/openjdk/jfx): UI development.
*   [Maven](https://maven.apache.org/): Build automation and dependency management.
*   [RichTextFX](https://github.com/FXMisc/RichTextFX): Syntax highlighting.
*   [Launch4J](https://launch4j.sourceforge.net/): For building into an exe
  

## Installation
* go to [releases](https://github.com/JacobsProjects/Constellation-Text-Editor/releases) and download the latest jar/exe


**Building from Source:**

To build to a jar from source simply:
*  Download the source code
*  Make sure you're using java 21 or up
*  Extract the downloaded source zip
*  run ```mvn package``` in command line
*  go to the 'target' folder and run the jar!
  

