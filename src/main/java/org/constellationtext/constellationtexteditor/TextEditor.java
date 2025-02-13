package org.constellationtext.constellationtexteditor;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;
import java.util.Collections;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;


import java.io.*;
import java.util.Collection;

// this code is basically unreadable i'm so sorry if you're reading this

// remember to fix textwrapping messing up line numbers
public class TextEditor extends VBox {
    private MenuBar menuBar;
    private CodeArea textArea;
    private VBox lineNumberBox;
    private HBox statusBar;
    private File currentFile;
    private String lastSavedText;
    private Label fileNameLabel;
    private int lastSearchIndex = -1;
    private CtxFiles ctxtHandler;
    private Label fileType;
    private boolean syntaxHighlightingEnabled = false;
    private boolean syntaxHighlightingOnAllFiles = false;

    public TextEditor() {
        menuBar = new MenuBar();
        textArea = new CodeArea();
        lineNumberBox = new VBox();
        ScrollPane lineNumberScrollPane = new ScrollPane(lineNumberBox);
        fileNameLabel = new Label("Untitled");
        fileNameLabel.setStyle("-fx-padding: 5 0 5 0;");
        ctxtHandler = new CtxFiles();

        setMinHeight(400);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
        VBox.setVgrow(this, Priority.ALWAYS);
        textArea.setMinHeight(200);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        setupMenuBar();
        handleDragAndDrop();


        lineNumberScrollPane.setFitToWidth(true);
        lineNumberScrollPane.setFitToHeight(true);
        lineNumberScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lineNumberScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lineNumberScrollPane.setPrefWidth(40); 
        lineNumberScrollPane.setMinWidth(40);
        lineNumberScrollPane.setMaxWidth(40);
        

        textArea.estimatedScrollYProperty().addListener((obs, oldVal, newVal) -> {
            lineNumberBox.setTranslateY(-newVal.doubleValue());
        });


        ScrollPane textAreaScrollPane = new ScrollPane(textArea);
        textAreaScrollPane.setFitToWidth(true);
        textAreaScrollPane.setFitToHeight(true);
        textAreaScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        textAreaScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        HBox.setHgrow(textAreaScrollPane, Priority.ALWAYS);


        HBox editorArea = new HBox(lineNumberScrollPane, textAreaScrollPane);
        HBox.setHgrow(editorArea, Priority.ALWAYS);
        VBox.setVgrow(editorArea, Priority.ALWAYS);


        textArea.textProperty().addListener((obs, oldText, newText) -> {
            updateLineNumbers();
            updateFileName();
        });

        addStatusBar();
        getChildren().addAll(menuBar, editorArea, statusBar);
        updateLineNumbers();
    }

    public boolean hasUnsavedChanges() {
        return lastSavedText != null && !textArea.getText().equals(lastSavedText);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @SuppressWarnings("exports")
    public ButtonType showSaveConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes");
        alert.setContentText("Do you want to save your changes before closing?\n\nIf you don't save, your recent work will be lost.");
    
        alert.getDialogPane().getStyleClass().add("save-confirmation-dialog");
    
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.YES);
        ButtonType dontSaveButton = new ButtonType("Don't Save", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    
        alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);
    
        alert.getDialogPane().setStyle(
            "-fx-font-family: 'Inter', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-background-color: #0a1e2b;" +
            "-fx-text-fill: #ecf0f1;"
        );
    
        return alert.showAndWait().orElse(cancelButton);
    }

    private void handleNew() {
        if (hasUnsavedChanges()) {
            ButtonType result = showSaveConfirmation();
            if (result == ButtonType.YES) {
                handleSave();
            } else if (result == ButtonType.CANCEL) {
                return;
            }
        }
        textArea.clear();
        currentFile = null;
        updateFileName();
        updateFileTypeLabel();
    }

    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("CTXT Files", "*.ctxt"),
            new FileChooser.ExtensionFilter("Java Files", "*.java"),
            new FileChooser.ExtensionFilter("Python Files", "*.py" , "*.python")

        );
        File file = fileChooser.showOpenDialog(getScene().getWindow());

        if (file != null) {
            try {
                String content;
                if (ctxtHandler.isCTXTFile(file)) {
                    content = ctxtHandler.readEncryptedFile(file);
                    updateFileTypeLabel();
                } else {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder contentBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line).append("\n");
                    }
                    content = contentBuilder.toString();
                    reader.close();
                }
                textArea.replaceText(content.toString());
                lastSavedText = content.toString();
                currentFile = file;
                updateFileName();
                updateFileTypeLabel();
                String fileName = currentFile.getName();
                int lastDotIndex = fileName.lastIndexOf('.');
                if (syntaxHighlightingEnabled){
                if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                    String extension = fileName.substring(lastDotIndex + 1).toUpperCase();
                    fileType.setText(extension);
                    if (extension.equals("java")) {
                        SyntaxHighlighting.applyJavaHighlighting(textArea);
                    } else if (extension.equals("py") || extension.equals("python")) {
                        SyntaxHighlighting.applyPythonHighlighting(textArea);
                    } else {
                        SyntaxHighlighting.applyAutoHighlighting(textArea); //this isn't done, will come back later and finish
                    }
                }
            }
            } catch (IOException e) {
                showError("Error opening file", e.getMessage());
            }
        }
    }
    
    private void handleDragAndDrop() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 157, 255, 0.4)); 
        dropShadow.setSpread(0.2);
        dropShadow.setRadius(6);
        dropShadow.setOffsetY(0);
        dropShadow.setOffsetX(0);
        
        Timeline pulseAnimation = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(dropShadow.radiusProperty(), 6, Interpolator.EASE_OUT),
                new KeyValue(dropShadow.spreadProperty(), 0.2, Interpolator.EASE_OUT),
                new KeyValue(dropShadow.colorProperty(), Color.rgb(0, 157, 255, 0.4), Interpolator.EASE_OUT)
            ),
            new KeyFrame(Duration.seconds(1.2),
                new KeyValue(dropShadow.radiusProperty(), 12, Interpolator.EASE_IN),
                new KeyValue(dropShadow.spreadProperty(), 0.5, Interpolator.EASE_IN),
                new KeyValue(dropShadow.colorProperty(), Color.rgb(0, 157, 255, 0.7), Interpolator.EASE_IN)
            )
        );
        pulseAnimation.setAutoReverse(true);
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
    
        textArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
    
        textArea.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles()) {
                textArea.setStyle("-fx-border-color:rgb(6, 86, 135); -fx-border-width: 1.5; -fx-border-style: solid;");
                textArea.setEffect(dropShadow);
                pulseAnimation.play();
            }
            event.consume();
        });
    
        textArea.setOnDragExited(event -> {
            pulseAnimation.stop();
            textArea.setEffect(null);
            textArea.setStyle("");
            event.consume();
        });
    
        textArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
        
            if (db.hasFiles() && !db.getFiles().isEmpty()) {
                File file = db.getFiles().get(0);
                
                try {
                    String content;
                    if (ctxtHandler.isCTXTFile(file)) {
                        content = ctxtHandler.readEncryptedFile(file);
                        updateFileTypeLabel();
                    } else {    
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            StringBuilder contentBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                contentBuilder.append(line).append("\n");
                            }
                            content = contentBuilder.toString();
                        }
                    }
                    
                    textArea.replaceText(content);
                    lastSavedText = content;
                    currentFile = file;
                    updateFileName();
                    updateFileTypeLabel();
                    success = true;
                    
                } catch (IOException e) {
                    showError("Error opening file", e.getMessage());
                }
            }
        
            event.setDropCompleted(success);
            event.consume();
        });
    }
                

    public void handleSave() {
        if (currentFile == null) {
            handleSaveAs();
        } else {
            saveFile(currentFile);
        }
    }


    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as");
        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            saveFile(file);
            currentFile = file;
            updateFileName();
            updateFileTypeLabel();      /// hgawk tuah
        }
    }

    private void saveFile(File file) {
        try {
            if (ctxtHandler.isCTXTFile(file)) {
                ctxtHandler.writeEncryptedFile(file, textArea.getText());
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textArea.getText());
                writer.close();
            }
            lastSavedText = textArea.getText();
            updateFileName();
        } catch (IOException e) {
            showError("Error saving file", e.getMessage());
        }
    }

    private void updateLineNumbers() {
        String text = textArea.getText();
        String[] lines = text.split("\n", -1);

        Font font = Font.font(textArea.getStyle());
        double lineHeight = font.getSize() * 1.8;
        double viewportHeight = textArea.getHeight();
        int visibleLines = (int) Math.ceil(viewportHeight / lineHeight);

        lineNumberBox.getChildren().clear();

        int totalVisualLines = 0;
        for (String line : lines) {
            if (textArea.isWrapText() && !line.isEmpty()) {
                double textWidth = line.length() * font.getSize() * 0.6; 
                double availableWidth = textArea.getWidth() - 20; 
                int wrappedLines = Math.max(1, (int) Math.ceil(textWidth / availableWidth));
                totalVisualLines += wrappedLines;
            } else {
                totalVisualLines++;
            }
        }

        int maxLines = Math.max(totalVisualLines, lines.length);
        for (int i = 1; i <= maxLines; i++) {
            Text lineNumber = new Text(String.valueOf(i));
            lineNumber.getStyleClass().add("line-number-text");
            lineNumberBox.getChildren().add(lineNumber);
        }
    }

    private void updateFileTypeLabel(){
        if (currentFile != null) {
            String fileName = currentFile.getName();
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                String extension = fileName.substring(lastDotIndex + 1).toUpperCase();
                fileType.setText(extension);
            } else {
                fileType.setText("No Extension");
            }
        } else {
            fileType.setText("No Extension");
        }
    }
    private void applySyntaxHighlighting() {
        if (syntaxHighlightingEnabled) {
            if (syntaxHighlightingOnAllFiles) {
                SyntaxHighlighting.applyAutoHighlighting(textArea);
            } else {
                if (currentFile != null) {
                    String fileName = currentFile.getName();
                    int lastDotIndex = fileName.lastIndexOf('.');
                    if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                        String extension = fileName.substring(lastDotIndex + 1);
                        if (extension.equals("java")) {
                            SyntaxHighlighting.applyJavaHighlighting(textArea);
                        } else if (extension.equals("py") || extension.equals("python")) {
                            SyntaxHighlighting.applyPythonHighlighting(textArea);
                        }
                    }
                }
            }
        } else {
            disableSyntaxHighlighting();
        }
    }
   /*  private void applySyntaxHighlighting() { // old version of code, being rewritten
        if (syntaxHighlightingEnabled) {
            SyntaxHighlighting.applyHighlighting(textArea);
        } else {
            disableSyntaxHighlighting();
        }
    }*/
    private void disableSyntaxHighlighting() {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(Collections.emptyList(), textArea.getText().length());
        textArea.setStyleSpans(0, spansBuilder.create());
    }

    private void addStatusBar() {
        statusBar = new HBox(10);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setMinHeight(25);
        statusBar.setStyle("-fx-padding: 5px;");

        Label position = new Label("Line: 1, Column: 1");
        Label encoding = new Label("UTF-8");

        fileType = new Label("No Extension");
 
        position.getStyleClass().add("status-label");
        fileType.getStyleClass().add("status-label");
        encoding.getStyleClass().add("status-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBar.getChildren().addAll(position, spacer, fileType, encoding);

        textArea.caretPositionProperty().addListener((obs, old, newVal) -> {
            int caretPosition = newVal.intValue();
            String text = textArea.getText();

            int line = 1;
            int column = 1;

            for (int i = 0; i < caretPosition; i++) {
                if (i < text.length()) {
                    if (text.charAt(i) == '\n') {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                }
            }
        // Add this in your constructor after creating the lineNumberScrollPane

            position.setText(String.format("Line: %d, Column: %d", line, column));
        });

        textArea.textProperty().addListener((obs, old, newVal) -> {
            if (currentFile != null) {
                String fileName = currentFile.getName();
                int lastDotIndex = fileName.lastIndexOf('.');
                if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                    String extension = fileName.substring(lastDotIndex + 1).toUpperCase();
                    fileType.setText(extension);
                } else {
                    fileType.setText("No Extension");
                }
            } else {
                fileType.setText("No Extension");
            }
        });
    }

    private void updateFileName() { //change out for new unicode soon
        if (currentFile == null) {
            fileNameLabel.setText(hasUnsavedChanges() ? "Untitled\u2022" : "Untitled");
        } else {
            fileNameLabel.setText(hasUnsavedChanges() ? currentFile.getName() + "\u2022" : currentFile.getName());
        }
    }

    private void showFind() { // add replace soon
        Dialog<String> find = new Dialog<>();
        find.setTitle("Search");

        GridPane grid = new GridPane();
        TextField searchField = new TextField();
        grid.add(new Label("Find:"), 0, 0);
        grid.add(searchField, 1, 0);

        find.getDialogPane().setContent(grid);

        find.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        find.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return searchField.getText();
            }
            return null;
        });

        find.showAndWait().ifPresent(searchText -> {
            if (!searchText.isEmpty()) {
                String text = textArea.getText();
                int startIndex = (lastSearchIndex == -1) ? 0 : lastSearchIndex + 1;
                int index = text.indexOf(searchText, startIndex);

                if (index == -1 && startIndex > 0) {
                    index = text.indexOf(searchText);
                }

                if (index >= 0) {
                    textArea.selectRange(index, index + searchText.length());
                    textArea.requestFocus();
                    lastSearchIndex = index;
                } else {
                    lastSearchIndex = -1;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Search");
                    alert.setHeaderText(null);
                    alert.setContentText("Text not found.");
                    alert.showAndWait();
                }
            }
        });
    }
    private void setupMenuBar() {
        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem find = new MenuItem("Find");

        Menu viewMenu = new Menu("View");
        CustomMenuItem opacityItem = new CustomMenuItem();
        VBox opacityControl = new VBox(5);
        Label opacityLabel = new Label("Window Opacity");
        CheckMenuItem textWrap = new CheckMenuItem("Line Wrap");
        CheckMenuItem syntaxHighlighting = new CheckMenuItem("Syntax Highlight");
        CheckMenuItem highlightingOnAllFiles = new CheckMenuItem("Highlighting on all files");
        highlightingOnAllFiles.setVisible(false);
    
        //opacity things
        Slider opacitySlider = new Slider(0.1, 1.0, 0.8); // min, max, default (incase i forget lmao)
        opacitySlider.setShowTickLabels(true);
        opacitySlider.setShowTickMarks(true);
        opacitySlider.setMajorTickUnit(0.1);
        opacitySlider.setBlockIncrement(0.1);

        opacitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (getScene() != null && getScene().getWindow() != null) {
                getScene().getWindow().setOpacity(newVal.doubleValue());
            }
        });

        opacityControl.getChildren().addAll(opacityLabel, opacitySlider);
        opacityItem.setContent(opacityControl);
        opacityItem.setHideOnClick(false);

        // end of opacity things

        textWrap.setOnAction(e -> {
            textArea.setWrapText(textWrap.isSelected());
            updateLineNumbers();
        });

        syntaxHighlighting.setOnAction(e -> {
            syntaxHighlightingEnabled = syntaxHighlighting.isSelected();
            highlightingOnAllFiles.setVisible(syntaxHighlighting.isSelected());
            applySyntaxHighlighting();
        });

        // not done yet (not working at all lmao don;t fool yourself future jagob)
        highlightingOnAllFiles.setOnAction(e -> {
            applySyntaxHighlighting();
        });

        viewMenu.getItems().addAll(opacityItem, textWrap, syntaxHighlighting, highlightingOnAllFiles);

        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        find.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));

        newFile.setOnAction(e -> handleNew());
        open.setOnAction(e -> handleOpen());
        save.setOnAction(e -> handleSave());
        saveAs.setOnAction(e -> handleSaveAs());
        find.setOnAction(e -> showFind());

        fileMenu.getItems().addAll(newFile, find, open, save, saveAs);

        Menu fileNameMenu = new Menu();
        fileNameMenu.setGraphic(fileNameLabel);

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        leftSpacer.setMinWidth(Region.USE_PREF_SIZE);
        rightSpacer.setMinWidth(Region.USE_PREF_SIZE);

        Menu leftSpacerMenu = new Menu("");
        Menu rightSpacerMenu = new Menu("");

        leftSpacerMenu.setGraphic(leftSpacer);
        rightSpacerMenu.setGraphic(rightSpacer);

        leftSpacerMenu.setDisable(true);
        rightSpacerMenu.setDisable(true);

        fileNameMenu.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        menuBar.getMenus().addAll(fileMenu, viewMenu, leftSpacerMenu, fileNameMenu, rightSpacerMenu);
    }
}