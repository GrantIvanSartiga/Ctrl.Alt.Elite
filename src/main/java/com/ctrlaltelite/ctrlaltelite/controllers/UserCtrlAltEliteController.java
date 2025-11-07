package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.FilesDatabaseConnection;
import com.ctrlaltelite.ctrlaltelite.util.CustomAlert;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UserCtrlAltEliteController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @FXML
    private JFXButton profileButton;
    @FXML
    private JFXButton libraryButton;
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

        // Animations
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

        // Scroll listener
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> checkVisibility());

        // Button actions
        profileButton.setOnAction(actionEvent -> LoginUser());
        fileUploadButton.setOnAction(actionEvent -> openFileUpload());

        checkVisibility();

        // Configure scroll pane
        if (scrollPane != null) {
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }

        // Configure file list container
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

        if (contentSection != null) {
            contentSection.setOpacity(1);
            contentSection.setTranslateY(0);
            contentSection.setVisible(true);
            contentSection.setManaged(true);
        }


        loadUserFiles();
    }

    private void checkVisibility() {
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double scrollY = scrollPane.getVvalue() * (contentHeight - viewportHeight);

        // Hero section visibility
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

        // Content section visibility
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
    private void LoginUser() {
        try {
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
        if (!UserManager.isLoggedIn()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Please log in first");
            alert.setContentText("You must be logged in to access the Library.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("library.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            Stage libraryStage = new Stage();
            libraryStage.setScene(scene);
            libraryStage.setTitle("Ctrl+Alt+Elite - Library");
            libraryStage.setMinWidth(600);
            libraryStage.setMinHeight(400);
            libraryStage.setResizable(true);
            libraryStage.setMaximized(true);
            libraryStage.show();

            ((Stage) libraryButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Failed to open Library");
            errorAlert.setContentText("An unexpected error occurred while loading the Library: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private void openFileUpload() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("fileChooser.fxml"));

            Stage uploadStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 900, 700);

            uploadStage.setScene(scene);
            uploadStage.setTitle("Upload File - Ctrl.Alt.Elite");
            uploadStage.setMinWidth(600);
            uploadStage.setMinHeight(400);
            uploadStage.setResizable(true);
            uploadStage.setMaximized(true);

            uploadStage.show();

            ((Stage) fileUploadButton.getScene().getWindow()).close();

        } catch (Exception e) {
            System.err.println("ERROR loading file upload window:");
            e.printStackTrace();
        }
    }

    private void loadUserFiles() {
        System.out.println("=== Loading Marketplace Files (All Users) ===");

        if (fileListContainer == null) {
            System.err.println("CRITICAL ERROR: fileListContainer is null!");
            return;
        }

        // Make sure containers are visible
        fileListContainer.setVisible(true);
        fileListContainer.setManaged(true);

        // Show loading indicator
        Platform.runLater(() -> {
            fileListContainer.getChildren().clear();
            Label loadingLabel = new Label("Loading study materials...");
            loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #F5F0E9; -fx-font-family:\"Alte Haas Grotesk\"; -fx-font-weight: bold;");
            fileListContainer.getChildren().add(loadingLabel);
        });

        // Load files in background thread
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Fetching ALL files from database...");

                try {
                    FindIterable<Document> files = FilesDatabaseConnection.getAllFiles();

                    // Process files OUTSIDE Platform.runLater to avoid iterator issues
                    java.util.List<HBox> fileCards = new java.util.ArrayList<>();
                    int fileCount = 0;

                    for (Document fileDoc : files) {
                        System.out.println("Processing file: " + fileDoc.getString("filename") +
                                " by " + fileDoc.getString("email"));
                        try {
                            HBox fileCard = createModernFileCard(fileDoc);
                            fileCards.add(fileCard);
                            fileCount++;
                        } catch (Exception e) {
                            System.err.println("Error creating card for file: " + fileDoc.getString("filename"));
                            e.printStackTrace();
                        }
                    }

                    final int totalFiles = fileCount;
                    System.out.println("Total marketplace files loaded: " + totalFiles);

                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        fileListContainer.getChildren().clear();

                        if (totalFiles == 0) {
                            showNoFilesMessage("No study materials available yet. Be the first to upload!");
                        } else {
                            fileListContainer.getChildren().addAll(fileCards);
                        }

                        // Force layout refresh
                        fileListContainer.requestLayout();
                    });

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
                Platform.runLater(() -> {
                    fileListContainer.getChildren().clear();
                    showNoFilesMessage("Error loading files: " + getException().getMessage());
                    System.err.println("Failed to load files:");
                    getException().printStackTrace();
                });
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private HBox createModernFileCard(Document fileDoc) {
        // Main card container - horizontal layout
        HBox card = new HBox(20);
        card.getStyleClass().add("file-card");;

        // Extract data from document
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
            fallbackIcon.getStyleClass().add("pdf-icon");;
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
        //descriptionLabel.setMaxWidth(450);


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

        // Price display
        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceLabel = new Label("Price");
        priceLabel.getStyleClass().add("price-label");
        Label priceValue = new Label(String.format("Php%.2f", price != null ? price : 0.0));
        priceValue.getStyleClass().add("price-value");
        priceBox.getChildren().addAll(priceLabel, priceValue);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("View Details");
        viewButton.getStyleClass().add("view-button");
        viewButton.setOnAction(e -> viewFileDetails(fileDoc));

        Button buyButton = new Button("Buy Now");
        buyButton.getStyleClass().add("buy-button");


        String currentUser = UserManager.getCurrentUser();
        if (currentUser != null && currentUser.equals(sellerEmail)) {
            buyButton.setText("Your File");
            buyButton.setDisable(true);

        } else {
            buyButton.setOnAction(e -> purchaseFile(fileDoc));
        }

        buttonBox.getChildren().addAll(viewButton, buyButton);
        bottomRow.getChildren().addAll(priceBox, spacer, buttonBox);

        // Add all to details box
        detailsBox.getChildren().addAll(titleBox, descriptionLabel, infoRow, bottomRow);

        // Add cover and details to card
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
                        "Price: $" + String.format("%.2f", price != null ? price : 0.0) + "\n" +
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
        // TODO: Implement PDF viewer
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("PDF Viewer");
        info.setHeaderText("Opening: " + filename);
        info.setContentText("PDF viewer functionality will be implemented here.");
        info.showAndWait();
    }

    private void purchaseFile(Document fileDoc) {
        String title = fileDoc.getString("title");
        Double price = fileDoc.getDouble("price");

        // Create custom dialog (Stage)
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Purchase Confirmation");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UNDECORATED);

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

        Label pdfLabel = new Label("PDF");
        pdfLabel.getStyleClass().add("pdf-label");
        coverBox.getChildren().add(pdfLabel);



        Label header = new Label("Purchase: " + (title != null ? title : "Untitled Document"));
        header.getStyleClass().add("purchase-header");

        Label priceLabel = new Label("Price: Php" + String.format("%.2f", price != null ? price : 0.0));
        priceLabel.getStyleClass().add("purchase-price");

        Label message = new Label("Do you want to proceed with this purchase?");
        message.getStyleClass().add("purchase-message");

        Button confirmBtn = new Button("Buy Now");
        confirmBtn.getStyleClass().add("confirm-button");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("cancel-button");

        confirmBtn.setOnAction(e -> {
            processPurchase(fileDoc);
            dialog.close();
        });

        cancelBtn.setOnAction(e -> dialog.close());

        // Layout
        HBox buttonBox = new HBox(confirmBtn, cancelBtn);
        buttonBox.getStyleClass().add("dialog-button-box");

        HBox contentBox = new HBox(15);
        contentBox.setAlignment(Pos.CENTER);

        VBox detailsBox = new VBox(header, priceLabel, message, buttonBox);
        detailsBox.setSpacing(10);
        contentBox.getChildren().addAll(coverBox, detailsBox);

        VBox layout = new VBox(contentBox);
        layout.getStyleClass().add("purchase-dialog");

        Scene scene = new Scene(layout, 500, 250);

        var resource = CtrlAltEliteApplication.class.getResource("CtrlAltEliteStyles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
            System.out.println("CSS loaded for dialog");
        } else {
            System.err.println("CSS file not found! Check the file location.");
        }

        dialog.setScene(scene);

        // Optional fade-in animation
        FadeTransition fade = new FadeTransition(Duration.millis(300), layout);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        dialog.showAndWait();
    }

    private void processPurchase(Document fileDoc) {
        System.out.println("Processing purchase for: " + fileDoc.getString("filename"));
        // TODO: Implement payment processing

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Purchase Successful");
        successAlert.setHeaderText("Thank you for your purchase!");
        successAlert.setContentText("The file has been added to your library.\nYou can now access it anytime.");
        successAlert.showAndWait();
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

    public void logoutUser(javafx.event.ActionEvent event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        CustomAlert.showConfirmation(currentStage,
                "Logout Confirmation",
                "Are you sure you want to log out?",
                "You will be redirected to the homepage.",
                confirmed -> {
                    if (confirmed) {
                        currentStage.close();

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ctrlaltelite/ctrlaltelite/overview-view.fxml"));
                            Stage stage = new Stage();
                            stage.setTitle("Ctrl.Alt.Elite");
                            stage.setScene(new Scene(loader.load()));
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            CustomAlert.showError(null, "Error", "Cannot load homepage", e.getMessage());
                        }
                    }
                });
    }
}
