package org.constellationtext.constellationtexteditor;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import java.util.Random;

public class ConstellationText extends Application {
    private VBox root;
    private TextEditor editor;


    @Override
    public void start(Stage primaryStage) {
        root = new VBox();
        StackPane mainContainer = new StackPane();
        Pane stars = new Pane();
        mainContainer.setPrefSize(1200, 800);
        mainContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);


        Font.loadFont(getClass().getResourceAsStream("/fonts/SourceCodePro-Regular.ttf"), 16);
        Font.loadFont(getClass().getResourceAsStream("/fonts/SourceCodePro-Bold.ttf"), 16);
        Font.loadFont(getClass().getResourceAsStream("/fonts/SourceCodePro-Medium.ttf"), 16);

        
        stars.setPrefSize(1200, 800);
        stars.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        stars.setMouseTransparent(true);

        editor = new TextEditor();

        mainContainer.getChildren().addAll(stars, editor);
        root.getChildren().add(mainContainer);

        Random starAmount = new Random();
        for (int i = 0; i < 50; i++){
            double x = starAmount.nextDouble() * 1200;
            double y = starAmount.nextDouble() * 800;
            createStarShape(stars, x, y);
        }


        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(Color.web("#0b1e29"));
        primaryStage.setOpacity(0.9); // remember to change this later, 1.0 is just for debugging

        Image icon = new Image(getClass().getResource("/icon.png").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());


        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Constellation Text");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> handleWindowClose(event));

        primaryStage.show();
    }

    private void handleWindowClose(WindowEvent event) {
        if (editor.hasUnsavedChanges()) {
            ButtonType result = editor.showSaveConfirmation();
            if (result == ButtonType.YES) {
                editor.handleSave();
            } else if (result == ButtonType.CANCEL) {
                event.consume();
                return;
            }
        }
    }
    private void createStarShape(Pane starsPane, double x, double y) {
        SVGPath star = new SVGPath();
        star.setContent("M 0 -4 L 1 -1 L 4 0 L 1 1 L 0 4 L -1 1 L -4 0 L -1 -1 Z");
        star.setTranslateX(x);
        star.setTranslateY(y);
        star.setFill(Color.WHITE);
        star.getStyleClass().addAll("star", "star-animation");

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), star);
        fadeTransition.setFromValue(0.05);
        fadeTransition.setToValue(0.2);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);
        fadeTransition.setAutoReverse(true);

        Random twinkleAmount = new Random();
        fadeTransition.setDelay(Duration.seconds(twinkleAmount.nextDouble() * 3));

        fadeTransition.play();

        starsPane.getChildren().add(star);
    }
}