package com.ctrlaltelite.ctrlaltelite.controllers;

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
import java.io.FileInputStream;
import java.util.List;

public class FileChooserController {

    @FXML
    private Button chooseFileButton;

    @FXML
    private VBox uploadListContainer;

    @FXML
    private VBox dragDropArea;

    @FXML
    public void initialize() {
        setupDragAndDrop();
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
                uploadFile(file);
            }
        }
    }

    private void setupDragAndDrop() {
        dragDropArea.setOnDragOver(event -> handleDragOver(event));
        dragDropArea.setOnDragDropped(event -> handleDragDropped(event));
        dragDropArea.setOnDragExited(event -> {
            dragDropArea.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 10; -fx-background-color: white; -fx-background-radius: 10;");
        });
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            dragDropArea.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 3; -fx-border-style: dashed; -fx-border-radius: 10; -fx-background-color: #e8f5e9; -fx-background-radius: 10;");
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            success = true;
            List<File> files = db.getFiles();
            for (File file : files) {
                uploadFile(file);
            }
        }

        event.setDropCompleted(success);
        event.consume();
        dragDropArea.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 10; -fx-background-color: white; -fx-background-radius: 10;");
    }

    private void uploadFile(File file) {
        // Create upload item UI
        HBox uploadItem = createUploadItem(file);
        uploadListContainer.getChildren().add(uploadItem);

        // Find the progress bar and labels in the upload item
        ProgressBar progressBar = (ProgressBar) uploadItem.lookup(".progress-bar");
        Label statusLabel = (Label) uploadItem.lookup(".status-label");
        Label speedLabel = (Label) uploadItem.lookup(".speed-label");

        // Process file upload
        Task<Void> uploadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // TODO: Replace this section with actual database upload logic
                // For now, this just simulates the upload process

                long fileSize = file.length();
                long uploadedBytes = 0;
                long startTime = System.currentTimeMillis();

                // Simulate upload by reading the file in chunks
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[8192]; // 8KB chunks
                    int bytesRead;

                    while ((bytesRead = fis.read(buffer)) != -1) {
                        uploadedBytes += bytesRead;
                        double progress = (double) uploadedBytes / fileSize;

                        // Calculate upload speed
                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime;
                        double speed = (uploadedBytes / 1024.0) / (elapsedTime / 1000.0); // KB/sec

                        updateProgress(progress, 1.0);

                        final long finalUploadedBytes = uploadedBytes;
                        final double finalSpeed = speed;
                        Platform.runLater(() -> {
                            int percent = (int) (progress * 100);
                            statusLabel.setText(percent + "% done");
                            speedLabel.setText(String.format("%.0fKB/sec", finalSpeed));
                        });

                        // Simulate network delay
                        Thread.sleep(50);
                    }
                }

                // When ready to connect to database, replace above code with:
                /*
                // Example API upload (adjust based on your backend API)
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://your-api-url/upload"))
                    .header("Content-Type", "multipart/form-data")
                    .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                    .build();

                HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new Exception("Upload failed: " + response.body());
                }
                */

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    statusLabel.setText("Completed");
                    statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    speedLabel.setText("");

                    // Store file reference for later use
                    System.out.println("File ready for upload: " + file.getAbsolutePath());
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    statusLabel.setText("Failed");
                    statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                    speedLabel.setText("");
                });
            }
        };

        progressBar.progressProperty().bind(uploadTask.progressProperty());

        Thread uploadThread = new Thread(uploadTask);
        uploadThread.setDaemon(true);
        uploadThread.start();
    }

    private HBox createUploadItem(File file) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(15, 10, 15, 10));
        container.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-border-radius: 5; -fx-background-radius: 5;");

        // File icon based on file type
        Label iconLabel = new Label(getFileIcon(file));
        iconLabel.setFont(Font.font(32));
        iconLabel.setStyle("-fx-text-fill: #757575;");
        iconLabel.setMinWidth(40);
        iconLabel.setAlignment(Pos.CENTER);

        // File info container
        VBox fileInfo = new VBox(8);
        fileInfo.setPrefWidth(350);

        // File name and size
        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label fileName = new Label(file.getName());
        fileName.setFont(Font.font(13));
        fileName.setStyle("-fx-font-weight: bold; -fx-text-fill: #112250;");

        Label fileSize = new Label(formatFileSize(file.length()));
        fileSize.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");

        nameRow.getChildren().addAll(fileName, fileSize);

        // Progress bar
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);
        progressBar.setPrefHeight(8);
        progressBar.getStyleClass().add("progress-bar");
        progressBar.setStyle("-fx-accent: #2196F3;");

        // Status row
        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(statusRow, javafx.scene.layout.Priority.ALWAYS);

        Label statusLabel = new Label("0% done");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");

        Label speedLabel = new Label("");
        speedLabel.getStyleClass().add("speed-label");
        speedLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        statusRow.getChildren().addAll(statusLabel, spacer, speedLabel);

        fileInfo.getChildren().addAll(nameRow, progressBar, statusRow);

        // Cancel button
        Button cancelButton = new Button("✕");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 18; -fx-cursor: hand; -fx-padding: 5;");
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #f44336; -fx-font-size: 18; -fx-cursor: hand; -fx-padding: 5; -fx-background-radius: 3;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 18; -fx-cursor: hand; -fx-padding: 5;"));
        cancelButton.setOnAction(e -> {
            uploadListContainer.getChildren().remove(container);
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