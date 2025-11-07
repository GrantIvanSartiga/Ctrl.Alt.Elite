package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.FilesDatabaseConnection;
import com.ctrlaltelite.ctrlaltelite.util.UserManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class FileChooserController {

    @FXML
    private Button chooseFileButton;

    @FXML
    private VBox uploadListContainer;

    @FXML
    private Button uploadButton;

    @FXML
    private VBox dragDropArea;

    @FXML
    public void initialize() {
        setupDragAndDrop();
        uploadButton.setDisable(true);
        uploadButton.setOnAction(this::uploadAllFiles);
    }

    @FXML
    void chooseFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Files to Upload");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported Files", "*.jpg", "*.jpeg", "*.png", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) chooseFileButton.getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                addFileToList(file);
            }
        }
    }

    private void setupDragAndDrop() {
        dragDropArea.setOnDragOver(this::handleDragOver);
        dragDropArea.setOnDragDropped(this::handleDragDropped);
        dragDropArea.setOnDragExited(event -> {
            dragDropArea.getStyleClass().removeAll("drag-area-hover", "drag-area-exit");
            dragDropArea.getStyleClass().add("drag-area-exit");
        });
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            dragDropArea.getStyleClass().removeAll("drag-area-default", "drag-area-exit");
            if (!dragDropArea.getStyleClass().contains("drag-area-hover")) {
                dragDropArea.getStyleClass().add("drag-area-hover");
            }
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            success = true;
            for (File file : db.getFiles()) {
                addFileToList(file);
            }
        }

        event.setDropCompleted(success);
        event.consume();

        dragDropArea.getStyleClass().removeAll("drag-area-hover");
        dragDropArea.getStyleClass().add("drag-area-exit");
    }

    private void addFileToList(File file) {
        HBox uploadItem = createUploadItem(file);
        uploadListContainer.getChildren().add(uploadItem);

        // Enable upload button when files are added
        uploadButton.setDisable(false);
    }

    private void uploadAllFiles(ActionEvent event) {
        String currentUserEmail = UserManager.getCurrentUser();

        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No user logged in");
            alert.showAndWait();
            return;
        }

        uploadButton.setDisable(true);

        for (int i = 0; i < uploadListContainer.getChildren().size(); i++) {
            HBox uploadItem = (HBox) uploadListContainer.getChildren().get(i);
            Label statusLabel = (Label) uploadItem.lookup(".status-label");
            Label speedLabel = (Label) uploadItem.lookup(".speed-label");
            ProgressBar progressBar = (ProgressBar) uploadItem.lookup(".progress-bar");

            // Get file reference from the container
            File file = (File) uploadItem.getUserData();

            if (file == null || !file.exists()) {
                statusLabel.setText("File not found");
                statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                continue;
            }

            Task<Void> uploadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    long fileSize = file.length();
                    long startTime = System.currentTimeMillis();

                    // Simulate file reading progress
                    long uploadedBytes = 0;
                    long simulatedBytes = 0;

                    while (simulatedBytes < fileSize) {
                        long bytesToSimulate = Math.min(8192, fileSize - simulatedBytes);
                        simulatedBytes += bytesToSimulate;
                        uploadedBytes += bytesToSimulate;

                        double progress = (double) uploadedBytes / fileSize;

                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime;
                        double speed = (uploadedBytes / 1024.0) / (elapsedTime / 1000.0);

                        updateProgress(progress, 1.0);

                        final double finalSpeed = speed;
                        Platform.runLater(() -> {
                            int percent = (int) (progress * 100);
                            statusLabel.setText(percent + "% done");
                            speedLabel.setText(String.format("%.0fKB/sec", finalSpeed));
                        });

                        Thread.sleep(50);
                    }

                    // Upload to database
                    String fileId = FilesDatabaseConnection.uploadFile(file, currentUserEmail);
                    System.out.println("File uploaded successfully with ID: " + fileId);

                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        statusLabel.setText("Completed");
                        statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        speedLabel.setText("");
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        statusLabel.setText("Failed");
                        statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                        speedLabel.setText("");
                        getException().printStackTrace();
                    });
                }
            };

            progressBar.progressProperty().bind(uploadTask.progressProperty());

            Thread uploadThread = new Thread(uploadTask);
            uploadThread.setDaemon(true);
            uploadThread.start();
        }
    }

    private HBox createUploadItem(File file) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(15, 10, 15, 10));
        container.setStyle("-fx-background-color: #F5F0E9; -fx-border-color: #112250; -fx-border-width: 0 0 1 0; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Store file reference in container
        container.setUserData(file);

        Label iconLabel = new Label(getFileIcon(file));
        iconLabel.setFont(Font.font(32));
        iconLabel.setStyle("-fx-text-fill: #112250;");
        iconLabel.setMinWidth(40);
        iconLabel.setAlignment(Pos.CENTER);

        VBox fileInfo = new VBox(8);
        fileInfo.setPrefWidth(350);

        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label fileName = new Label(file.getName());
        fileName.setFont(Font.font(13));
        fileName.setStyle("-fx-font-weight: bold; -fx-text-fill: #112250;");

        Label fileSize = new Label(formatFileSize(file.length()));
        fileSize.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");

        nameRow.getChildren().addAll(fileName, fileSize);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);
        progressBar.setPrefHeight(8);
        progressBar.getStyleClass().add("progress-bar");
        progressBar.setStyle("-fx-accent: #E0C58F;");

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(statusRow, javafx.scene.layout.Priority.ALWAYS);

        Label statusLabel = new Label("Ready to upload");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 11px;");

        Label speedLabel = new Label("");
        speedLabel.getStyleClass().add("speed-label");
        speedLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        statusRow.getChildren().addAll(statusLabel, spacer, speedLabel);

        fileInfo.getChildren().addAll(nameRow, progressBar, statusRow);

        Button cancelButton = new Button("✕");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 18; -fx-cursor: hand; -fx-padding: 5;");
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #f44336; -fx-font-size: 18; -fx-cursor: hand; -fx-padding: 5; -fx-background-radius: 3;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 18; -fx-cursor: hand; -fx-padding: 5;"));
        cancelButton.setOnAction(e -> {
            uploadListContainer.getChildren().remove(container);
            if (uploadListContainer.getChildren().isEmpty()) {
                uploadButton.setDisable(true);
            }
        });

        container.getChildren().addAll(iconLabel, fileInfo, cancelButton);

        return container;
    }

    private String getFileIcon(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
            return "🖼️";
        } else if (fileName.endsWith(".pdf")) {
            return "📄";
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return "📝";
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return "📊";
        } else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) {
            return "📦";
        } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov")) {
            return "🎥";
        } else if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")) {
            return "🎵";
        } else {
            return "📄";
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}