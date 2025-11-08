package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.FilesDatabaseConnection;
import com.ctrlaltelite.ctrlaltelite.util.UserManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import java.io.IOException;
import java.util.List;

public class FileChooserController {

    @FXML
    private Button chooseFileButton;

    @FXML
    private VBox uploadListContainer;

    @FXML
    private Button cancelButton;

    @FXML
    private Button uploadButton;

    @FXML
    private VBox dragDropArea;

    @FXML
    private TextField TitleTextField;

    @FXML
    private TextArea DiscriptionTextArea;

    @FXML
    private TextField PriceTextField;

    private File currentFile;
    private boolean isUploading = false;

    @FXML
    public void initialize() {
        setupDragAndDrop();
        uploadButton.setDisable(true);
        uploadButton.setOnAction(this::uploadFile);
        cancelButton.setOnAction(actionEvent -> cancelButtonOnAction());
    }

    private File getInitialDirectory() {

        File desktopDir = new File(System.getProperty("user.home") + "/Desktop");
        if (desktopDir.exists() && desktopDir.isDirectory()) {
            return desktopDir;
        }


        File documentsDir = new File(System.getProperty("user.home") + "/Documents");
        if (documentsDir.exists() && documentsDir.isDirectory()) {
            return documentsDir;
        }


        return new File(System.getProperty("user.home"));
    }


    @FXML
    void chooseFile(ActionEvent e) {
        if (isUploading) {
            showAlert("Upload in Progress", "Please wait for the current upload to complete before selecting a new file.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose PDF File to Upload");
        fileChooser.setInitialDirectory(getInitialDirectory());  // Use the new method

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) chooseFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                showAlert("Invalid File Type", "Please select a PDF file.");
                return;
            }
            setFileForUpload(selectedFile);
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
        if (isUploading) {
            showAlert("Upload in Progress", "Please wait for the current upload to complete before selecting a new file.");
            event.consume();
            return;
        }

        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            if (files.size() == 1) {
                File file = files.get(0);
                if (file.getName().toLowerCase().endsWith(".pdf")) {
                    setFileForUpload(file);
                    success = true;
                } else {
                    showAlert("Invalid File Type", "Please drop a PDF file.");
                }
            } else {
                showAlert("Single File Only", "Please drop only one file at a time.");
            }
        }

        event.setDropCompleted(success);
        event.consume();

        dragDropArea.getStyleClass().removeAll("drag-area-hover");
        dragDropArea.getStyleClass().add("drag-area-exit");
    }

    private void setFileForUpload(File file) {
        currentFile = file;
        uploadListContainer.getChildren().clear();
        HBox uploadItem = createUploadItem(file);
        uploadListContainer.getChildren().add(uploadItem);
        uploadButton.setDisable(false);
    }

    private void uploadFile(ActionEvent event) {
        String currentUserEmail = UserManager.getCurrentUser();

        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            showAlert("Error", "No user logged in");
            return;
        }

        if (currentFile == null || !currentFile.exists()) {
            showAlert("Error", "File not found");
            return;
        }

        // Validate form fields
        String title = TitleTextField.getText().trim();
        String description = DiscriptionTextArea.getText().trim();
        String priceText = PriceTextField.getText().trim();

        if (title.isEmpty()) {
            showAlert("Validation Error", "Please enter a title for the document.");
            return;
        }

        if (description.isEmpty()) {
            showAlert("Validation Error", "Please enter a description for the document.");
            return;
        }

        if (priceText.isEmpty()) {
            showAlert("Validation Error", "Please enter a price for the document.");
            return;
        }

        // Validate price is a valid number
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                showAlert("Validation Error", "Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid price (numbers only).");
            return;
        }

        isUploading = true;
        uploadButton.setDisable(true);
        chooseFileButton.setDisable(true);
        TitleTextField.setDisable(true);
        DiscriptionTextArea.setDisable(true);
        PriceTextField.setDisable(true);

        HBox uploadItem = (HBox) uploadListContainer.getChildren().get(0);
        Label statusLabel = (Label) uploadItem.lookup(".status-label");
        Label speedLabel = (Label) uploadItem.lookup(".speed-label");
        ProgressBar progressBar = (ProgressBar) uploadItem.lookup(".upload-progress-bar");

        // Capture values before starting the background thread
        final String finalTitle = title;
        final String finalDescription = description;
        final double finalPrice = price;

        Task<Void> uploadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long fileSize = currentFile.length();
                long startTime = System.currentTimeMillis();

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

                // Upload to database with metadata
                String fileId = FilesDatabaseConnection.uploadFile(
                        currentFile,
                        currentUserEmail,
                        finalTitle,
                        finalDescription,
                        finalPrice
                );
                System.out.println("File uploaded successfully with ID: " + fileId);
                System.out.println("Title: " + finalTitle);
                System.out.println("Description: " + finalDescription);
                System.out.println("Price: $" + finalPrice);

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    statusLabel.setText("Completed");
                    statusLabel.getStyleClass().add("completed");
                    speedLabel.setText("");
                    isUploading = false;
                    chooseFileButton.setDisable(false);
                    TitleTextField.setDisable(false);
                    DiscriptionTextArea.setDisable(false);
                    PriceTextField.setDisable(false);

                    // Clear form after successful upload
                    currentFile = null;
                    TitleTextField.clear();
                    DiscriptionTextArea.clear();
                    PriceTextField.clear();
                    uploadListContainer.getChildren().clear();
                    uploadButton.setDisable(true);

                    showAlert("Success", "File uploaded successfully!");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    statusLabel.setText("Failed");
                    statusLabel.getStyleClass().add("failed");
                    speedLabel.setText("");
                    isUploading = false;
                    chooseFileButton.setDisable(false);
                    TitleTextField.setDisable(false);
                    DiscriptionTextArea.setDisable(false);
                    PriceTextField.setDisable(false);
                    getException().printStackTrace();
                    showAlert("Upload Failed", "An error occurred during upload: " + getException().getMessage());
                });
            }
        };

        progressBar.progressProperty().bind(uploadTask.progressProperty());

        Thread uploadThread = new Thread(uploadTask);
        uploadThread.setDaemon(true);
        uploadThread.start();
    }

    private HBox createUploadItem(File file) {
        HBox container = new HBox();
        container.getStyleClass().add("upload-item");
        container.setUserData(file);

        Label iconLabel = new Label("📄");
        iconLabel.getStyleClass().add("upload-icon");

        VBox fileInfo = new VBox();
        fileInfo.getStyleClass().add("file-info-container");

        HBox nameRow = new HBox();
        nameRow.getStyleClass().add("file-name-row");

        Label fileName = new Label(file.getName());
        fileName.getStyleClass().add("file-name-label");

        Label fileSize = new Label(formatFileSize(file.length()));
        fileSize.getStyleClass().add("file-size-label");

        nameRow.getChildren().addAll(fileName, fileSize);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.getStyleClass().add("upload-progress-bar");

        HBox statusRow = new HBox();
        statusRow.getStyleClass().add("status-row");
        HBox.setHgrow(statusRow, javafx.scene.layout.Priority.ALWAYS);

        Label statusLabel = new Label("Ready to upload");
        statusLabel.getStyleClass().add("status-label");

        Label speedLabel = new Label("");
        speedLabel.getStyleClass().add("speed-label");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        statusRow.getChildren().addAll(statusLabel, spacer, speedLabel);

        fileInfo.getChildren().addAll(nameRow, progressBar, statusRow);

        Button cancelBtn = new Button("✕");
        cancelBtn.getStyleClass().add("upload-cancel-button");
        cancelBtn.setOnAction(e -> {
            if (!isUploading) {
                uploadListContainer.getChildren().clear();
                uploadButton.setDisable(true);
                currentFile = null;
            } else {
                showAlert("Upload in Progress", "Cannot cancel while upload is in progress.");
            }
        });

        container.getChildren().addAll(iconLabel, fileInfo, cancelBtn);

        return container;
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void cancelButtonOnAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("user-overview-view.fxml"));

            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);

            stage.setScene(scene);
            stage.setTitle("CtrlAltElite");
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();

            ((Stage) cancelButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}