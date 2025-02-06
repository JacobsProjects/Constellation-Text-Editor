package org.constellationtext.constellationtexteditor;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;
import javafx.application.HostServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    private static final String CURRENT_VERSION = "1.0.0";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/JacobsProjects/Constellation-Text-Editor/releases/latest";
    private static final String REPO_URL = "https://github.com/JacobsProjects/Constellation-Text-Editor";
    private static HostServices hostServices;


    public static void setHostServices(HostServices services) {
        hostServices = services;
    }

    public static void checkForUpdates() {
        CompletableFuture.runAsync(() -> {
            try {
                String latestVersion = getLatestVersion();
                if (latestVersion != null && !latestVersion.equals(CURRENT_VERSION)) {
                    Platform.runLater(() -> showUpdateDialog(latestVersion));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String getLatestVersion() {
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseStr = response.toString();
                int tagStart = responseStr.indexOf("\"tag_name\":\"") + 12;
                int tagEnd = responseStr.indexOf("\"", tagStart);
                return responseStr.substring(tagStart, tagEnd).replace("v", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void showUpdateDialog(String newVersion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Update Available");
        alert.setHeaderText("New Version Available!");
        alert.setContentText(String.format("Current version: %s\nLatest version: %s\n\nWould you like to download the update?", 
            CURRENT_VERSION, newVersion));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("save-confirmation-dialog");
        
        ButtonType updateButton = new ButtonType("Update", ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Later", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(updateButton, cancelButton);

        Platform.runLater(() -> {
            for (Button button : dialogPane.lookupAll(".button").toArray(new Button[0])) {
                button.getStyleClass().add("dialog-button");
            }
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == updateButton && hostServices != null) {
            hostServices.showDocument(REPO_URL);
        }
    }
}