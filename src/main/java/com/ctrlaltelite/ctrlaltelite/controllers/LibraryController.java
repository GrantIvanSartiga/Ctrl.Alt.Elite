package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.FilesDatabaseConnection;
import com.ctrlaltelite.ctrlaltelite.util.UserManager;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");


    @FXML
    private FlowPane bookContainer1;

    @FXML
    private FlowPane bookContainer11;
    @FXML
    private JFXButton profileButton;

    @FXML
    private JFXButton homeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("LibraryController initializing...");


        if (profileButton != null) {
            profileButton.setOnAction(event -> {
                System.out.println("Profile/Logout button clicked!");
                goToLoginPage();
            });
        } else {
            System.err.println("ERROR: profileButton is null!");
        }

        if (homeButton != null) {
            homeButton.setOnAction(event -> {
                System.out.println("Home button clicked!");
                goToUserOverview();
            });
        } else {
            System.err.println("ERROR: homeButton is null!");
        }

        // Load files
        loadPurchasedFiles();
        loadUploadedFiles();
    }


    private void loadPurchasedFiles() {
        String currentUser = UserManager.getCurrentUser();

        if (currentUser == null) {
            System.err.println("No user logged in!");
            return;
        }

        System.out.println("=== Loading Purchased Files for: " + currentUser + " ===");

        Task<List<Document>> loadTask = new Task<List<Document>>() {
            @Override
            protected List<Document> call() throws Exception {
                return FilesDatabaseConnection.getUserPurchasedFilesDetails(currentUser);
            }
        };

        loadTask.setOnSucceeded(e -> {
            List<Document> purchasedFiles = loadTask.getValue();

            Platform.runLater(() -> {
                if (bookContainer1 != null) {
                    bookContainer1.getChildren().clear();

                    if (purchasedFiles == null || purchasedFiles.isEmpty()) {
                        Label noFiles = new Label("No purchased files yet");
                        noFiles.setStyle(
                                "-fx-font-size: 16px; " +
                                        "-fx-text-fill: #757575; " +
                                        "-fx-font-family: 'Alte Haas Grotesk';"
                        );
                        bookContainer1.getChildren().add(noFiles);
                        System.out.println("No purchased files to display");
                    } else {
                        System.out.println("Loading " + purchasedFiles.size() + " purchased files");
                        for (Document file : purchasedFiles) {
                            try {
                                VBox fileCard = createPurchasedFileCard(file);
                                bookContainer1.getChildren().add(fileCard);
                            } catch (Exception ex) {
                                System.err.println("Error creating card for: " + file.getString("filename"));
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
        });

        loadTask.setOnFailed(e -> {
            System.err.println("Failed to load purchased files: " + loadTask.getException().getMessage());
            loadTask.getException().printStackTrace();
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }


    private void loadUploadedFiles() {
        String currentUser = UserManager.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        System.out.println("=== Loading Uploaded Files for: " + currentUser + " ===");

        Task<List<Document>> loadTask = new Task<List<Document>>() {
            @Override
            protected List<Document> call() throws Exception {
                return FilesDatabaseConnection.getUserUploadedFiles(currentUser);
            }
        };

        loadTask.setOnSucceeded(e -> {
            List<Document> uploadedFiles = loadTask.getValue();

            Platform.runLater(() -> {
                if (bookContainer11 != null) {
                    bookContainer11.getChildren().clear();

                    if (uploadedFiles == null || uploadedFiles.isEmpty()) {
                        Label noFiles = new Label("No uploaded files yet");
                        noFiles.setStyle(
                                "-fx-font-size: 16px; " +
                                        "-fx-text-fill: #757575; " +
                                        "-fx-font-family: 'Alte Haas Grotesk';"
                        );
                        bookContainer11.getChildren().add(noFiles);
                        System.out.println("No uploaded files to display");
                    } else {
                        System.out.println("Loading " + uploadedFiles.size() + " uploaded files");
                        for (Document file : uploadedFiles) {
                            try {
                                VBox fileCard = createUploadedFileCard(file);
                                bookContainer11.getChildren().add(fileCard);
                            } catch (Exception ex) {
                                System.err.println("Error creating card for: " + file.getString("filename"));
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
        });

        loadTask.setOnFailed(e -> {
            System.err.println("Failed to load uploaded files: " + loadTask.getException().getMessage());
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }


    private VBox createPurchasedFileCard(Document fileDoc) {
        VBox card = new VBox(10);
        card.getStyleClass().add("library-file-card");
        card.setPadding(new Insets(15));

        String title = fileDoc.getString("title");
        String filename = fileDoc.getString("filename");
        Double purchasePrice = fileDoc.getDouble("purchase_price");


        Label titleLabel = new Label(title != null ? title : "Untitled");
        titleLabel.setStyle(
                "-fx-font-family: 'Bison Bold'; " +
                        "-fx-font-size: 14; " +
                        "-fx-text-fill: #112250;"
        );


        Label filenameLabel = new Label(filename);
        filenameLabel.setStyle(
                "-fx-font-family: 'Alte Haas Grotesk'; " +
                        "-fx-font-size: 11; " +
                        "-fx-text-fill: #757575;"
        );


        Label priceLabel = new Label("Php" + String.format("%.2f", purchasePrice != null ? purchasePrice : 0.0));
        priceLabel.setStyle(
                "-fx-font-family: 'Bison Bold'; " +
                        "-fx-font-size: 12; " +
                        "-fx-text-fill: #E0C58F;"
        );


        Button openBtn = new Button("Open");
        openBtn.getStyleClass().add("library-open-button");
        openBtn.setOnAction(e -> openPdfFile(fileDoc));

        card.getChildren().addAll(titleLabel, filenameLabel, priceLabel, openBtn);

        return card;
    }


    private VBox createUploadedFileCard(Document fileDoc) {
        VBox card = new VBox(10);
        card.getStyleClass().add("library-file-card");
        card.setPadding(new Insets(15));

        String title = fileDoc.getString("title");
        String filename = fileDoc.getString("filename");
        Double price = fileDoc.getDouble("price");


        Label titleLabel = new Label(title != null ? title : "Untitled");
        titleLabel.setStyle(
                "-fx-font-family: 'Bison Bold'; " +
                        "-fx-font-size: 14; " +
                        "-fx-text-fill: #112250;"
        );


        Label filenameLabel = new Label(filename);
        filenameLabel.setStyle(
                "-fx-font-family: 'Alte Haas Grotesk'; " +
                        "-fx-font-size: 11; " +
                        "-fx-text-fill: #757575;"
        );


        Label priceLabel = new Label("Php" + String.format("%.2f", price != null ? price : 0.0));
        priceLabel.setStyle(
                "-fx-font-family: 'Bison Bold'; " +
                        "-fx-font-size: 12; " +
                        "-fx-text-fill: #E0C58F;"
        );


        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("library-edit-button");
        editBtn.setOnAction(e -> editUploadedFile(fileDoc));

        card.getChildren().addAll(titleLabel, filenameLabel, priceLabel, editBtn);

        return card;
    }

    private void openPdfFile(Document fileDoc) {
        String filename = fileDoc.getString("filename");
        String fileId = fileDoc.getObjectId("_id").toString();
        System.out.println("Opening: " + filename + " (ID: " + fileId + ")");

        try {
            Document dbFileDoc = FilesDatabaseConnection.getFile(fileId.toString());
            if (dbFileDoc == null) {
                System.err.println("File not found in database.");
                return;
            }

            Binary fileData = dbFileDoc.get("file_data", Binary.class);
            if (fileData == null) {
                System.err.println("No file data found for: " + filename);
                return;
            }

            File tempFile = File.createTempFile("temp_", "_" + filename);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
                fos.write(fileData.getData());
            }

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(tempFile);
                System.out.println("File opened: " + tempFile.getAbsolutePath());
            } else {
                System.err.println("Desktop is not supported. Cannot open file.");
            }

            tempFile.deleteOnExit();

        } catch (Exception e) {
            System.err.println("Error opening file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editUploadedFile(Document fileDoc) {
        String filename = fileDoc.getString("filename");
        System.out.println("Editing: " + filename);

        // Implement file editing here
    }

    public void refreshFiles() {
        loadPurchasedFiles();
        loadUploadedFiles();
    }

    private void goToUserOverview() {
        try {
            System.out.println("Navigating to user-overview-view.fxml...");

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

            // Close current library window
            ((Stage) profileButton.getScene().getWindow()).close();
            System.out.println("Successfully navigated to user overview!");

        } catch (IOException e) {
            System.err.println("Error navigating to user overview: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void goToLoginPage() {
        try {
            System.out.println("Navigating to login page...");

            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);

            stage.setScene(scene);
            stage.setTitle("CtrlAltElite - Login");
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();

            // Close current library window
            ((Stage) profileButton.getScene().getWindow()).close();
            System.out.println("Successfully navigated to login page!");

        } catch (IOException e) {
            System.err.println("Error navigating to login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}