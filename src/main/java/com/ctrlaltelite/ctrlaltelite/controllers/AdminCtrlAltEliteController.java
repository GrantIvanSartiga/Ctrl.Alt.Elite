package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.DatabaseConnection;
import com.ctrlaltelite.ctrlaltelite.FilesDatabaseConnection;
import com.ctrlaltelite.ctrlaltelite.util.UserManager;
import com.jfoenix.controls.JFXButton;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AdminCtrlAltEliteController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @FXML
    private JFXButton profileButton;
    @FXML
    private Text welcomeText;
    @FXML
    private ImageView ICON;
    @FXML
    private AnchorPane Logo;
    @FXML
    private Text Text2;
    @FXML
    private HBox SearchIcon;
    @FXML
    private HBox SearchAndButtons;
    @FXML
    private TextField searchField;
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
    private java.util.List<Document> allFiles = new java.util.ArrayList<>();
    private ObservableList<Document> filteredFiles = FXCollections.observableArrayList();

    @FXML
    private TableView<UserManager.User> userTable;
    @FXML
    private TableColumn<UserManager.User, String> UserIDColumn;
    @FXML
    private TableColumn<UserManager.User, String> UserNameColumn;
    @FXML
    private TableColumn<UserManager.User, String> UserEmailColumn;


    @FXML
    public void initialize() {

        try {
            // Load CSS
            try {
                var resource = this.getClass().getResource("/com/ctrlaltelite/ctrlaltelite/CtrlAltElite.css");
                if (resource != null) {
                    String css = resource.toExternalForm();
                    if (contentSection != null) {
                        contentSection.getStylesheets().add(css);
                        System.out.println("CSS loaded successfully");
                    }
                } else {
                    System.err.println("WARNING: CSS file not found at /com/ctrlaltelite/ctrlaltelite/CtrlAltElite.css");
                }
            } catch (Exception e) {
                System.err.println("ERROR loading CSS: " + e.getMessage());
                e.printStackTrace();
            }

            // Initialize table columns - ADD NULL CHECKS
            if (UserIDColumn != null && UserNameColumn != null && UserEmailColumn != null) {
                UserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
                UserNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                UserEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                loadUsers(); // Load user data into the table
            } else {
                System.err.println("WARNING: One or more table columns are null. Check FXML fx:id bindings.");
            }

            // Initialize UI elements with null checks for Hero Section Animations
            if (welcomeText != null) welcomeText.setOpacity(0);
            if (Text2 != null) Text2.setOpacity(0);
            if (ICON != null) {
                ICON.setOpacity(0);
                ICON.setTranslateY(80);
            }
            if (SearchIcon != null) {
                SearchIcon.setOpacity(0);
                SearchIcon.setTranslateY(80);
            }
            if (contentSection != null) {
                contentSection.setOpacity(0);
                contentSection.setTranslateY(40);
            }

            if (Logo != null) Logo.setTranslateX(-50);
            if (SearchAndButtons != null) SearchAndButtons.setTranslateX(50);

            // --- SEARCH BAR SETUP ---
            if (searchField != null) {
                // Run search when 'Enter' is pressed
                searchField.setOnAction(event -> performSearch());
                searchField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
                System.out.println("Search field 'Enter' key listener added.");
            } else {
                System.err.println("WARNING: searchField is null! Check FXML fx:id binding.");
            }

            if (SearchIcon != null) {
                // Run search when the magnifying glass icon is clicked
                SearchIcon.setOnMouseClicked(event -> performSearch());
                SearchIcon.setStyle("-fx-cursor: hand;");
                System.out.println("Search icon click listener added.");
            } else { // <-- This 'else' block needed the closing brace which caused the errors
                System.err.println("WARNING: SearchIcon is null! Check FXML fx:id binding.");
            } // <--- IMPORTANT FIX: Closing bracket added here!


            // Animations (Header/Top Bar)
            if (SearchAndButtons != null && Logo != null) {
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
            }

            // Scroll pane listener for visibility animations
            if (scrollPane != null) {
                scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> checkVisibility());
                checkVisibility();
            }

            // Initialize file list container
            if (fileListContainer != null) {
                fileListContainer.setAlignment(Pos.TOP_CENTER);
                fileListContainer.setSpacing(20);
                fileListContainer.setPadding(new Insets(30));
                fileListContainer.setVisible(true);
                fileListContainer.setManaged(true);
                System.out.println("File list container initialized successfully");
            } else {
                System.err.println("WARNING: fileListContainer is null! Check FXML fx:id binding.");
            }

            // Make content section visible (initial state)
            if (contentSection != null) {
                contentSection.setOpacity(1);
                contentSection.setTranslateY(0);
                contentSection.setVisible(true);
                contentSection.setManaged(true);
            }

            // Load files for the marketplace/stash
            loadUserFiles();

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in initialize():");
            e.printStackTrace();
            e.getCause();
        }
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

    private void loadUsers() {
        MongoCollection<Document> collection = DatabaseConnection.getCollection();

        ObservableList<UserManager.User> users = FXCollections.observableArrayList();
        FindIterable<Document> documents = collection.find();

        for (Document doc : documents) {
            String id = doc.getObjectId("_id").toString();
            String firstName = doc.getString("first_name");
            String lastName = doc.getString("last_name");
            String email = doc.getString("email");

            String fullName = firstName + " " + lastName;

            users.add(new UserManager.User(id, fullName, email));
        }

        userTable.setItems(users);
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

        private void loadUserFiles() {
            System.out.println("=== Loading Marketplace Files (All Users) ===");

            if (fileListContainer == null) {
                System.err.println("CRITICAL ERROR: fileListContainer is null!");
                return;
            }

            fileListContainer.setVisible(true);
            fileListContainer.setManaged(true);

            Platform.runLater(() -> {
                fileListContainer.getChildren().clear();
                Label loadingLabel = new Label("Loading study materials...");
                loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #F5F0E9; -fx-font-family:\"Alte Haas Grotesk\"; -fx-font-weight: bold;");
                fileListContainer.getChildren().add(loadingLabel);
            });

            Task<Void> loadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("Fetching ALL files from database...");

                    try {
                        // Fetch all files and store them in the master list
                        FindIterable<Document> files = FilesDatabaseConnection.getAllFiles();

                        // Clear and populate the master list (allFiles)
                        allFiles.clear();
                        for (Document fileDoc : files) {
                            allFiles.add(fileDoc);
                        }

                        // Initialize filteredFiles with all files
                        filteredFiles.clear();
                        filteredFiles.addAll(allFiles);

                        System.out.println("Total marketplace files loaded: " + allFiles.size());

                        // Update the UI using the shared display method
                        Platform.runLater(() -> updateFileListDisplay());

                    } catch (Exception e) {
                        System.err.println("Error fetching files from database:");
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            fileListContainer.getChildren().clear();
                            showNoFilesMessage("Error loading marketplace: " + e.getMessage());
                        });
                    }
                    return null;
                }

                @Override
                protected void failed() {
                }
            };

            Thread loadThread = new Thread(loadTask);
            loadThread.setDaemon(true);
            loadThread.start();
        }

    private HBox createModernFileCard(Document fileDoc) {
        HBox card = new HBox(20);
        card.getStyleClass().add("file-card");

        String filename = fileDoc.getString("filename");
        String title = fileDoc.getString("title");
        String description = fileDoc.getString("description");
        Double price = fileDoc.getDouble("price");
        Long fileSize = fileDoc.getLong("file_size");
        Object uploadObj = fileDoc.get("upload_date");
        LocalDateTime uploadDate = null;

        if (uploadObj instanceof java.util.Date) {
            uploadDate = ((java.util.Date) uploadObj).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
        } else if (uploadObj instanceof LocalDateTime) {
            uploadDate = (LocalDateTime) uploadObj;
        }

        String sellerEmail = fileDoc.getString("email");
        String fileId = fileDoc.getObjectId("_id").toString();


        VBox coverBox = new VBox(5);
        coverBox.getStyleClass().add("file-card-cover");
        coverBox.setPadding(new Insets(15));

        ImageView pdfIcon = new ImageView();
        try {
            pdfIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/ctrlaltelite/ctrlaltelite/images/ICON.png"))));
            pdfIcon.setFitWidth(100);
            pdfIcon.setFitHeight(100);
            pdfIcon.setPreserveRatio(true);
        } catch (Exception e) {
            Label fallbackIcon = new Label("📄");
            fallbackIcon.getStyleClass().add("pdf-icon");
            coverBox.getChildren().add(fallbackIcon);
        }

        if (pdfIcon.getImage() != null) {
            coverBox.getChildren().add(pdfIcon);
        }

        Label pdfLabel = new Label("PDF");
        pdfLabel.getStyleClass().add("pdf-label");
        coverBox.getChildren().add(pdfLabel);


        VBox detailsBox = new VBox(12);
        detailsBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        VBox titleBox = new VBox(3);
        Label titleLabel = new Label(title != null && !title.isEmpty() ? title : "Untitled Document");
        titleLabel.getStyleClass().add("file-title");

        Label filenameLabel = new Label(filename);
        filenameLabel.getStyleClass().add("file-filename");

        titleBox.getChildren().addAll(titleLabel, filenameLabel);

        Label descriptionLabel = new Label(description != null && !description.isEmpty() ? description : "No description available");
        descriptionLabel.getStyleClass().add("file-description");

        HBox infoRow = new HBox(20);
        infoRow.setAlignment(Pos.CENTER_LEFT);

        HBox sellerInfo = new HBox(8);
        sellerInfo.setAlignment(Pos.CENTER_LEFT);
        Label sellerIcon = new Label("👤");
        sellerIcon.getStyleClass().add("info-icon");
        String sellerName = sellerEmail != null ? sellerEmail.split("@")[0] : "Unknown";
        Label sellerText = new Label("By: " + sellerName);
        sellerText.getStyleClass().add("info-label");
        sellerInfo.getChildren().addAll(sellerIcon, sellerText);

        HBox sizeInfo = new HBox(8);
        sizeInfo.setAlignment(Pos.CENTER_LEFT);
        Label sizeIcon = new Label("📦");
        sizeIcon.getStyleClass().add("info-icon");
        Label sizeText = new Label(formatFileSize(fileSize != null ? fileSize : 0));
        sizeText.getStyleClass().add("info-label");
        sizeInfo.getChildren().addAll(sizeIcon, sizeText);

        HBox dateInfo = new HBox(8);
        dateInfo.setAlignment(Pos.CENTER_LEFT);
        Label dateIcon = new Label("📅");
        dateIcon.getStyleClass().add("info-icon");
        Label dateText = new Label(uploadDate != null ? uploadDate.format(DATE_FORMATTER) : "Unknown");
        dateText.getStyleClass().add("info-label");
        dateInfo.getChildren().addAll(dateIcon, dateText);

        infoRow.getChildren().addAll(sellerInfo, sizeInfo, dateInfo);

        HBox bottomRow = new HBox(15);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(bottomRow, Priority.ALWAYS);

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceLabel = new Label("Price");
        priceLabel.getStyleClass().add("price-label");
        Label priceValue = new Label(String.format("Php%.2f", price != null ? price : 0.0));
        priceValue.getStyleClass().add("price-value");
        priceBox.getChildren().addAll(priceLabel, priceValue);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("View Details");
        viewButton.getStyleClass().add("view-button");
        viewButton.setOnAction(e -> viewFileDetails(fileDoc));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setStyle("-fx-background-color: #E63946; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-border-radius: 5; -fx-cursor: hand;");
        deleteButton.setOnAction(e -> deleteFileWithAnimation(fileDoc, card));

        buttonBox.getChildren().addAll(viewButton, deleteButton);
        bottomRow.getChildren().addAll(priceBox, spacer, buttonBox);

        detailsBox.getChildren().addAll(titleBox, descriptionLabel, infoRow, bottomRow);

        card.getChildren().addAll(coverBox, detailsBox);

        return card;
    }

    private void viewFileDetails(Document fileDoc) {
        String filename = fileDoc.getString("filename");
        String title = fileDoc.getString("title");
        String description = fileDoc.getString("description");
        Double price = fileDoc.getDouble("price");
        String fileId = fileDoc.getObjectId("_id").toString();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Details");
        alert.setHeaderText(title != null ? title : "Untitled Document");
        alert.setContentText(
                "Filename: " + filename + "\n" +
                        "Description: " + (description != null ? description : "No description") + "\n" +
                        "Price: Php" + String.format("%.2f", price != null ? price : 0.0) + "\n" +
                        "File ID: " + fileId
        );

        ButtonType previewButton = new ButtonType("Preview PDF");
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(previewButton, closeButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == previewButton) {
                openPDFViewer(fileId, filename);
            }
        });
    }

    private void openPDFViewer(String fileId, String filename) {
        System.out.println("Opening PDF viewer for: " + filename + " (ID: " + fileId + ")");
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("PDF Viewer");
        info.setHeaderText("Opening: " + filename);
        info.setContentText("PDF viewer functionality will be implemented here.");
        info.showAndWait();
    }

    private void deleteFileWithAnimation(Document fileDoc, HBox card) {
        String title = fileDoc.getString("title");
        String fileId = fileDoc.getObjectId("_id").toString();


        Stage confirmation = new Stage();
        confirmation.initModality(Modality.APPLICATION_MODAL);
        confirmation.setTitle("Delete File");
        confirmation.setResizable(false);
        confirmation.initStyle(StageStyle.UNDECORATED);


        Label titleLabel = new Label("Delete: " + (title != null ? title : "Untitled Document"));
        titleLabel.getStyleClass().add("delete-dialog-title");

        VBox coverBox = new VBox(5);
        coverBox.getStyleClass().add("file-card-cover");
        coverBox.setPadding(new Insets(15));


        ImageView pdfIcon = new ImageView();
        try {
            pdfIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/ctrlaltelite/ctrlaltelite/images/ICON.png"))));
            pdfIcon.setFitWidth(100);
            pdfIcon.setFitHeight(100);
            pdfIcon.setPreserveRatio(true);
        } catch (Exception e) {

            Label fallbackIcon = new Label("📄");
            fallbackIcon.getStyleClass().add("pdf-icon");;
            coverBox.getChildren().add(fallbackIcon);
        }

        if (pdfIcon.getImage() != null) {
            coverBox.getChildren().add(pdfIcon);
        }

        Label messageLabel = new Label("Are you sure you want to delete this file?\nThis action cannot be undone.");
        messageLabel.getStyleClass().add("delete-dialog-message");
        messageLabel.setWrapText(true);


        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("delete-dialog-delete-btn");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("delete-dialog-cancel-btn");

        deleteBtn.setOnAction(e -> {
            confirmation.close();

            FadeTransition fade = new FadeTransition(Duration.millis(400), card);
            fade.setFromValue(1);
            fade.setToValue(0);

            TranslateTransition slide = new TranslateTransition(Duration.millis(400), card);
            slide.setByX(-100);

            ParallelTransition deleteAnim = new ParallelTransition(fade, slide);
            deleteAnim.setOnFinished(event -> {

                fileListContainer.getChildren().remove(card);


                deleteFileFromDatabase(fileId);


                if (fileListContainer.getChildren().isEmpty()) {
                    showNoFilesMessage("No study materials available yet. Be the first to upload!");
                }
            });

            deleteAnim.play();
        });

        cancelBtn.setOnAction(e -> confirmation.close());


        HBox buttonBox = new HBox(cancelBtn, deleteBtn);
        buttonBox.getStyleClass().add("dialog-button-box");

        HBox contentBox = new HBox(15);
        contentBox.setAlignment(Pos.CENTER);

        VBox detailsBox = new VBox(titleLabel, messageLabel, buttonBox);
        detailsBox.setSpacing(10);
        contentBox.getChildren().addAll(coverBox, detailsBox);

        VBox layout = new VBox(contentBox);
        layout.getStyleClass().add("delete-dialog-layout");

        Scene scene = new Scene(layout, 500, 250);


        var resource = CtrlAltEliteApplication.class.getResource("CtrlAltEliteStyles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        }

        confirmation.setScene(scene);


        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), layout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        confirmation.showAndWait();
    }

    private void deleteFileFromDatabase(String fileId) {
        Task<Void> deleteTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Deleting file with ID: " + fileId);
                    FilesDatabaseConnection.deleteFile(String.valueOf(new ObjectId(fileId)));
                    System.out.println("File deleted successfully!");

                    Platform.runLater(() -> {
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText("File Deleted");
                        success.setContentText("The file has been successfully deleted from the marketplace.");
                        success.showAndWait();
                    });

                } catch (Exception e) {
                    System.err.println("Error deleting file from database:");
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("Deletion Failed");
                        error.setContentText("Failed to delete the file: " + e.getMessage());
                        error.showAndWait();
                    });
                }
                return null;
            }
        };

        Thread deleteThread = new Thread(deleteTask);
        deleteThread.setDaemon(true);
        deleteThread.start();
    }

    private void showNoFilesMessage(String message) {
        VBox emptyBox = new VBox(20);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(60));

        Label emptyIcon = new Label("📚");
        emptyIcon.setFont(Font.font(80));

        Label emptyLabel = new Label(message);
        emptyLabel.setFont(Font.font("System", FontWeight.NORMAL, 18));
        emptyLabel.setStyle("-fx-text-fill: #F5F0E9;");
        emptyLabel.setWrapText(true);
        emptyLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        emptyLabel.setMaxWidth(500);

        emptyBox.getChildren().addAll(emptyIcon, emptyLabel);
        fileListContainer.getChildren().clear();
        fileListContainer.getChildren().add(emptyBox);
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }

    public void refreshFiles() {
        loadUserFiles();
    }

        @FXML
        private void performSearch() {
            // Safe way to get search text, handles if searchField is somehow null
            String searchText = (searchField != null && searchField.getText() != null)
                    ? searchField.getText().toLowerCase().trim()
                    : "";

            if (allFiles.isEmpty()) {
                System.out.println("Cannot search: Master file list is empty.");
                return;
            }

            if (searchText.isEmpty()) {
                // If search is empty, show all files
                filteredFiles.clear();
                filteredFiles.addAll(allFiles);
            } else {
                // Filter files based on search text
                filteredFiles.clear();

                for (Document file : allFiles) {
                    // Safely get and normalize text fields for comparison
                    String filename = file.getString("filename") != null ? file.getString("filename").toLowerCase() : "";
                    String title = file.getString("title") != null ? file.getString("title").toLowerCase() : "";
                    String description = file.getString("description") != null ? file.getString("description").toLowerCase() : "";

                    // Search across filename, title, and description
                    if (filename.contains(searchText) ||
                            title.contains(searchText) ||
                            description.contains(searchText)) {
                        filteredFiles.add(file);
                    }
                }
            }

            System.out.println("Search results: " + filteredFiles.size() + " files found");

            // Update the UI with filtered results
            updateFileListDisplay();

            // Auto-scroll to show results (same logic as before)
            if (!searchText.isEmpty() && scrollPane != null && contentSection != null) {
                Platform.runLater(() -> {
                    double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
                    double viewportHeight = scrollPane.getViewportBounds().getHeight();
                    double contentY = contentSection.getBoundsInParent().getMinY();
                    double maxV = Math.max(0, contentHeight - viewportHeight);

                    if (maxV > 0) {
                        double targetV = Math.min(1.0, contentY / maxV);
                        javafx.animation.KeyValue kv = new javafx.animation.KeyValue(scrollPane.vvalueProperty(), targetV);
                        javafx.animation.KeyFrame kf = new javafx.animation.KeyFrame(Duration.millis(500), kv);
                        javafx.animation.Timeline timeline = new javafx.animation.Timeline(kf);
                        timeline.play();
                    }
                });
            }
        }

        private void updateFileListDisplay() {
            if (fileListContainer == null) {
                return;
            }

            Platform.runLater(() -> {
                fileListContainer.getChildren().clear();

                if (filteredFiles.isEmpty()) {
                    showNoFilesMessage("No study materials match your search criteria.");
                } else {
                    java.util.List<HBox> fileCards = new java.util.ArrayList<>();
                    for (Document fileDoc : filteredFiles) {
                        try {
                            HBox fileCard = createModernFileCard(fileDoc);
                            fileCards.add(fileCard);
                        } catch (Exception e) {
                            System.err.println("Error creating card for file: " + fileDoc.getString("filename"));
                            e.printStackTrace();
                        }
                    }
                    fileListContainer.getChildren().addAll(fileCards);
                }
                fileListContainer.requestLayout();
            });
        }
}