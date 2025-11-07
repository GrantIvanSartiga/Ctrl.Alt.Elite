package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.FilesDatabaseConnection;
import com.ctrlaltelite.ctrlaltelite.util.UserManager;
import com.jfoenix.controls.JFXButton;
import com.mongodb.client.FindIterable;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserCtrlAltEliteController {
    private static final DateTimeFormatter DATE_FORMATTER =DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    @FXML
    private JFXButton profileButton;
    @FXML
    private Text welcomeText;
    @FXML
    private ImageView ICON;
    @FXML
    private Pane Logo;
    @FXML
    private Text Text2;
    @FXML
    private HBox SearchIcon;
    @FXML
    private HBox SearchAndButtons;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane heroSection;
    @FXML
    private AnchorPane contentSection;
    @FXML
    private Button fileUploadButton;
    @FXML
    private VBox fileListContainer;

    private boolean heroVisible = false;
    private boolean contentVisible = false;

    @FXML
    public void initialize() {

        welcomeText.setOpacity(0);
        Text2.setOpacity(0);
        ICON.setOpacity(0);
        SearchIcon.setOpacity(0);
        contentSection.setOpacity(0);

        Logo.setTranslateX(-50);
        SearchAndButtons.setTranslateX(50);
        ICON.setTranslateY(80);
        SearchIcon.setTranslateY(80);
        contentSection.setTranslateY(40);


        TranslateTransition slideSearchAndButtons = new TranslateTransition(Duration.seconds(1), SearchAndButtons);
        slideSearchAndButtons.setFromX(50);
        slideSearchAndButtons.setToX(0);

        FadeTransition fadeSearchAndButtons = new FadeTransition(Duration.seconds(1), SearchAndButtons);
        fadeSearchAndButtons.setFromValue(0);
        fadeSearchAndButtons.setToValue(1);

        TranslateTransition slideLogo = new TranslateTransition(Duration.seconds(1), Logo);
        slideLogo.setFromX(-50);
        slideLogo.setToX(0);

        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(1), Logo);
        fadeLogo.setFromValue(0);
        fadeLogo.setToValue(1);

        ParallelTransition logoAnim = new ParallelTransition(slideLogo, fadeLogo);
        ParallelTransition buttonsAnim = new ParallelTransition(slideSearchAndButtons, fadeSearchAndButtons);

        logoAnim.play();
        buttonsAnim.play();

        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> checkVisibility());


        profileButton.setOnAction(actionEvent -> LoginUser());
        fileUploadButton.setOnAction(actionEvent -> openFileUpload());


        checkVisibility();

        if (scrollPane != null) {
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }

        // Set alignment for the container to top
        fileListContainer.setAlignment(Pos.TOP_CENTER);
        fileListContainer.setSpacing(15);
        fileListContainer.setPadding(new Insets(20));

        // Load files from database
        loadUserFiles();
    }

    private void checkVisibility() {
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double scrollY = scrollPane.getVvalue() * (contentHeight - viewportHeight);


        double heroY = heroSection.getBoundsInParent().getMinY();
        double heroBottom = heroSection.getBoundsInParent().getMaxY();
        boolean heroInView = heroBottom > scrollY && heroY < scrollY + viewportHeight;

        if (heroInView && !heroVisible) {
            playHeroAnimation();
            heroVisible = true;
        } else if (!heroInView && heroVisible) {
            resetHeroElements();
            heroVisible = false;
        }


        double contentY = contentSection.getBoundsInParent().getMinY();
        boolean contentInView = contentY < scrollY + viewportHeight - 150;

        if (contentInView && !contentVisible) {
            playContentAnimation();
            contentVisible = true;
        } else if (!contentInView && contentVisible) {
            resetContentElements();
            contentVisible = false;
        }
    }


    private void playHeroAnimation() {

        FadeTransition fadeWelcome = new FadeTransition(Duration.seconds(2), welcomeText);
        fadeWelcome.setFromValue(0);
        fadeWelcome.setToValue(1);

        FadeTransition fadeText2 = new FadeTransition(Duration.seconds(2), Text2);
        fadeText2.setFromValue(0);
        fadeText2.setToValue(1);

        TranslateTransition slideIcon = new TranslateTransition(Duration.seconds(1), ICON);
        slideIcon.setFromY(80);
        slideIcon.setToY(0);

        FadeTransition fadeIcon = new FadeTransition(Duration.seconds(2), ICON);
        fadeIcon.setFromValue(0);
        fadeIcon.setToValue(1);

        TranslateTransition slideSearchIcon = new TranslateTransition(Duration.seconds(2), SearchIcon);
        slideSearchIcon.setFromY(80);
        slideSearchIcon.setToY(0);

        FadeTransition fadeSearchIcon = new FadeTransition(Duration.seconds(2), SearchIcon);
        fadeSearchIcon.setFromValue(0);
        fadeSearchIcon.setToValue(1);

        ParallelTransition iconAnim = new ParallelTransition(slideIcon, fadeIcon);
        ParallelTransition searchIconAnim = new ParallelTransition(slideSearchIcon, fadeSearchIcon);

        fadeWelcome.play();
        fadeText2.play();
        iconAnim.play();
        searchIconAnim.play();
    }

    private void resetHeroElements() {

        welcomeText.setOpacity(0);
        Text2.setOpacity(0);
        ICON.setOpacity(0);
        ICON.setTranslateY(80);
        SearchIcon.setOpacity(0);
        SearchIcon.setTranslateY(80);
    }

    private void playContentAnimation() {
        FadeTransition fade = new FadeTransition(Duration.millis(800), contentSection);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), contentSection);
        slide.setFromY(40);
        slide.setToY(0);

        fade.play();
        slide.play();
    }

    private void resetContentElements() {
        contentSection.setOpacity(0);
        contentSection.setTranslateY(40);
    }

    @FXML
    private void LoginUser(){

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-view.fxml"));

            Stage loginStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            loginStage.setScene(scene);
            loginStage.setTitle("Login");
            loginStage.setMinWidth(600);
            loginStage.setMinHeight(400);
            loginStage.setResizable(true);
            loginStage.setMaximized(true);

            loginStage.show();

            ((Stage) profileButton.getScene().getWindow()).close();



        } catch (Exception e) {
            System.err.println("ERROR loading login window:");
            e.printStackTrace();
        }
    }

    @FXML
    private void openLibrary() {
        // Check if user is logged in before accessing the Library
        if (!UserManager.isLoggedIn()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Please log in first");
            alert.setContentText("You must be logged in to access the Library.");
            alert.showAndWait();
            return;
        }

        try {
            // Load the Library FXML
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("library.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            // Create a new stage for the Library window
            Stage libraryStage = new Stage();
            libraryStage.setScene(scene);
            libraryStage.setTitle("Ctrl+Alt+Elite - Library");
            libraryStage.setMinWidth(600);
            libraryStage.setMinHeight(400);
            libraryStage.setResizable(true);
            libraryStage.setMaximized(true);
            libraryStage.show();

//            // Close the current window (e.g., Overview)
//            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
//            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Failed to open Library");
            errorAlert.setContentText("An unexpected error occurred while loading the Library.");
            errorAlert.showAndWait();
        }


    }

    private void openFileUpload(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("fileChooser.fxml"));

            Stage loginStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 900, 700);

            loginStage.setScene(scene);
            loginStage.setTitle("Ctrl.Alt.Elite");
            loginStage.setMinWidth(600);
            loginStage.setMinHeight(400);
            loginStage.setResizable(true);
            loginStage.setMaximized(true);

            loginStage.show();

            ((Stage) profileButton.getScene().getWindow()).close();



        } catch (Exception e) {
            System.err.println("ERROR loading login window:");
            e.printStackTrace();
        }
    }

    private void loadUserFiles() {
        String currentUserEmail = UserManager.getCurrentUser();

        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            showNoFilesMessage("Please log in to view your files");
            return;
        }

        // Show loading indicator
        Label loadingLabel = new Label("Loading files...");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        fileListContainer.getChildren().add(loadingLabel);

        // Load files in background thread
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                FindIterable<Document> files = FilesDatabaseConnection.getUserFiles(currentUserEmail);

                Platform.runLater(() -> {
                    fileListContainer.getChildren().clear();

                    int fileCount = 0;
                    for (Document fileDoc : files) {
                        VBox fileCard = createFileCard(fileDoc);
                        fileListContainer.getChildren().add(fileCard);
                        fileCount++;
                    }

                    if (fileCount == 0) {
                        showNoFilesMessage("No files uploaded yet");
                    }
                });

                return null;
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    fileListContainer.getChildren().clear();
                    showNoFilesMessage("Error loading files: " + getException().getMessage());
                    getException().printStackTrace();
                });
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    private VBox createFileCard(Document fileDoc) {

        VBox card = new VBox(12);
        card.setMaxWidth(600); // Fixed max width
        card.setPrefWidth(600);
        card.setMinWidth(600);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #F5F0E9; " +
                        "-fx-border-color: #112250; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        // Extract data from document
        String filename = fileDoc.getString("filename");
        String title = fileDoc.getString("title");
        String description = fileDoc.getString("description");
        Double price = fileDoc.getDouble("price");
        Long fileSize = fileDoc.getLong("file_size");
        LocalDateTime uploadDate = fileDoc.get("upload_date", LocalDateTime.class);
        String fileId = fileDoc.getObjectId("_id").toString();

        // Header row with icon and title
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label("📄");
        iconLabel.setFont(Font.font(36));
        iconLabel.setStyle("-fx-text-fill: #112250;");

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label titleLabel = new Label(title != null ? title : "Untitled");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #112250;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(450);

        Label filenameLabel = new Label(filename);
        filenameLabel.setFont(Font.font(12));
        filenameLabel.setStyle("-fx-text-fill: #757575;");

        titleBox.getChildren().addAll(titleLabel, filenameLabel);

        headerRow.getChildren().addAll(iconLabel, titleBox);

        // Description section
        VBox descriptionBox = new VBox(5);
        descriptionBox.setAlignment(Pos.TOP_LEFT);

        Label descLabel = new Label("Description:");
        descLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        descLabel.setStyle("-fx-text-fill: #112250;");

        Label descriptionText = new Label(description != null ? description : "No description");
        descriptionText.setFont(Font.font(13));
        descriptionText.setStyle("-fx-text-fill: #424242;");
        descriptionText.setWrapText(true);
        descriptionText.setMaxWidth(550);

        descriptionBox.getChildren().addAll(descLabel, descriptionText);

        // Info row with price, size, and date
        HBox infoRow = new HBox(20);
        infoRow.setAlignment(Pos.CENTER_LEFT);
        infoRow.setPadding(new Insets(10, 0, 0, 0));

        // Price
        VBox priceBox = new VBox(3);
        priceBox.setAlignment(Pos.TOP_LEFT);
        Label priceLabel = new Label("Price");
        priceLabel.setFont(Font.font(10));
        priceLabel.setStyle("-fx-text-fill: #757575;");
        Label priceValue = new Label(String.format("$%.2f", price != null ? price : 0.0));
        priceValue.setFont(Font.font("System", FontWeight.BOLD, 14));
        priceValue.setStyle("-fx-text-fill: #4CAF50;");
        priceBox.getChildren().addAll(priceLabel, priceValue);

        // File size
        VBox sizeBox = new VBox(3);
        sizeBox.setAlignment(Pos.TOP_LEFT);
        Label sizeLabel = new Label("Size");
        sizeLabel.setFont(Font.font(10));
        sizeLabel.setStyle("-fx-text-fill: #757575;");
        Label sizeValue = new Label(formatFileSize(fileSize != null ? fileSize : 0));
        sizeValue.setFont(Font.font("System", FontWeight.BOLD, 12));
        sizeValue.setStyle("-fx-text-fill: #112250;");
        sizeBox.getChildren().addAll(sizeLabel, sizeValue);

        // Upload date
        VBox dateBox = new VBox(3);
        dateBox.setAlignment(Pos.TOP_LEFT);
        Label dateLabel = new Label("Uploaded");
        dateLabel.setFont(Font.font(10));
        dateLabel.setStyle("-fx-text-fill: #757575;");
        Label dateValue = new Label(uploadDate != null ? uploadDate.format(DATE_FORMATTER) : "Unknown");
        dateValue.setFont(Font.font(11));
        dateValue.setStyle("-fx-text-fill: #112250;");
        dateBox.getChildren().addAll(dateLabel, dateValue);

        infoRow.getChildren().addAll(priceBox, sizeBox, dateBox);

        // Action buttons row
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(10, 0, 0, 0));

        Button viewButton = new Button("View");
        viewButton.setStyle(
                "-fx-background-color: #112250; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        viewButton.setOnMouseEntered(e ->
                viewButton.setStyle(
                        "-fx-background-color: #1a3366; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8 20; " +
                                "-fx-cursor: hand; " +
                                "-fx-background-radius: 5;"
                )
        );
        viewButton.setOnMouseExited(e ->
                viewButton.setStyle(
                        "-fx-background-color: #112250; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8 20; " +
                                "-fx-cursor: hand; " +
                                "-fx-background-radius: 5;"
                )
        );
        viewButton.setOnAction(e -> viewFile(fileId, filename));

//        Button deleteButton = new Button("Delete");
//        deleteButton.setStyle(
//                "-fx-background-color: transparent; " +
//                        "-fx-text-fill: #f44336; " +
//                        "-fx-font-weight: bold; " +
//                        "-fx-padding: 8 20; " +
//                        "-fx-cursor: hand; " +
//                        "-fx-border-color: #f44336; " +
//                        "-fx-border-width: 2; " +
//                        "-fx-border-radius: 5; " +
//                        "-fx-background-radius: 5;"
//        );
//        deleteButton.setOnMouseEntered(e ->
//                deleteButton.setStyle(
//                        "-fx-background-color: #f44336; " +
//                                "-fx-text-fill: white; " +
//                                "-fx-font-weight: bold; " +
//                                "-fx-padding: 8 20; " +
//                                "-fx-cursor: hand; " +
//                                "-fx-border-color: #f44336; " +
//                                "-fx-border-width: 2; " +
//                                "-fx-border-radius: 5; " +
//                                "-fx-background-radius: 5;"
//                )
//        );
//        deleteButton.setOnMouseExited(e ->
//                deleteButton.setStyle(
//                        "-fx-background-color: transparent; " +
//                                "-fx-text-fill: #f44336; " +
//                                "-fx-font-weight: bold; " +
//                                "-fx-padding: 8 20; " +
//                                "-fx-cursor: hand; " +
//                                "-fx-border-color: #f44336; " +
//                                "-fx-border-width: 2; " +
//                                "-fx-border-radius: 5; " +
//                                "-fx-background-radius: 5;"
//                )
//        );
//        deleteButton.setOnAction(e -> deleteFile(fileId, card));

//        buttonRow.getChildren().addAll(viewButton, deleteButton);

        // Add all sections to card
        card.getChildren().addAll(headerRow, descriptionBox, infoRow, buttonRow);

        return card;
    }

    private void viewFile(String fileId, String filename) {
        // Implement view file functionality
        System.out.println("Viewing file: " + fileId + " - " + filename);
        // You can open a PDF viewer here or download the file
    }

//    private void deleteFile(String fileId, VBox card) {
//        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmAlert.setTitle("Delete File");
//        confirmAlert.setHeaderText("Are you sure you want to delete this file?");
//        confirmAlert.setContentText("This action cannot be undone.");
//
//        confirmAlert.showAndWait().ifPresent(response -> {
//            if (response == ButtonType.OK) {
//                try {
//                    FilesDatabaseConnection.deleteFile(fileId);
//                    fileListContainer.getChildren().remove(card);
//
//                    if (fileListContainer.getChildren().isEmpty()) {
//                        showNoFilesMessage("No files uploaded yet");
//                    }
//
//                    showAlert("Success", "File deleted successfully!");
//                } catch (Exception e) {
//                    showAlert("Error", "Failed to delete file: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    private void showNoFilesMessage(String message) {
        VBox emptyBox = new VBox(15);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(50));
        emptyBox.setMaxWidth(400);

        Label emptyIcon = new Label("📁");
        emptyIcon.setFont(Font.font(60));

        Label emptyLabel = new Label(message);
        emptyLabel.setFont(Font.font(16));
        emptyLabel.setStyle("-fx-text-fill: #757575;");
        emptyLabel.setWrapText(true);
        emptyLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        emptyBox.getChildren().addAll(emptyIcon, emptyLabel);
        fileListContainer.getChildren().clear();
        fileListContainer.getChildren().add(emptyBox);
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

    public void refreshFiles() {
        loadUserFiles();
    }

}
