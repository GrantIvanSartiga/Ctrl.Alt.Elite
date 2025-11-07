package com.ctrlaltelite.ctrlaltelite.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class CustomAlert {

    public enum AlertType {
        SUCCESS("✓", "#112250", "#D9CBC2"),  // Yellow icon on white background
        ERROR("✕", "#112250", "#D9CBC2"),    // Dark blue icon on gray background
        WARNING("⚠", "#3C507D", "#F5F0E9"),  // Light blue icon on white background
        INFO("ℹ", "#3C507D", "#F5F0E9");     // Light blue icon on white background

        private final String icon;
        private final String color;
        private final String backgroundColor;

        AlertType(String icon, String color, String backgroundColor) {
            this.icon = icon;
            this.color = color;
            this.backgroundColor = backgroundColor;
        }

        public String getIcon() { return icon; }
        public String getColor() { return color; }
        public String getBackgroundColor() { return backgroundColor; }
    }


    private Stage dialog;
    private AlertType type;
    private String title;
    private String headerText;
    private String contentText;
    private Consumer<Boolean> callback;
    private boolean showCancelButton = false;
    private String confirmButtonText = "OK";
    private String cancelButtonText = "Cancel";

    public CustomAlert(AlertType type, String title, String headerText, String contentText) {
        this.type = type;
        this.title = title;
        this.headerText = headerText;
        this.contentText = contentText;
    }

    public void setCallback(Consumer<Boolean> callback) {
        this.callback = callback;
    }

    public void setShowCancelButton(boolean show) {
        this.showCancelButton = show;
    }

    public void setConfirmButtonText(String text) {
        this.confirmButtonText = text;
    }

    public void setCancelButtonText(String text) {
        this.cancelButtonText = text;
    }

    public void show(Stage owner) {
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle(title);

        // Main container with rounded corners
        VBox mainContainer = new VBox(0);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setMaxWidth(450);
        mainContainer.setStyle(
                "-fx-background-color: #D9CBC2;" +   // Gray
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 5);"
        );

        // Header section with color accent
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);
        headerSection.setPadding(new Insets(30, 30, 20, 30));
        headerSection.setStyle(
                "-fx-background-color: " + type.getBackgroundColor() + ";" +
                        "-fx-background-radius: 15 15 0 0;"
        );

        // Icon
        Label iconLabel = new Label(type.getIcon());
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 50));
        iconLabel.setTextFill(Color.web(type.getColor()));
        iconLabel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-min-width: 80px;" +
                        "-fx-min-height: 80px;" +
                        "-fx-alignment: center;"
        );

        // Title
        Label titleLabel = new Label(headerText);
        titleLabel.setFont(Font.font("Alte Haas Grotesk", FontWeight.BOLD, 24));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(380);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextFill(Color.web("#112250"));

        headerSection.getChildren().addAll(iconLabel, titleLabel);

        // Content section
        VBox contentSection = new VBox(20);
        contentSection.setAlignment(Pos.CENTER);
        contentSection.setPadding(new Insets(20, 30, 30, 30));

        // Message
        Label messageLabel = new Label(contentText);
        messageLabel.setFont(Font.font("Alte Haas Grotesk", 15));
        messageLabel.setTextFill(Color.web("#555555"));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(380);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setStyle("-fx-text-alignment: center;");

        // Button container
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Confirm button
        Button confirmButton = createStyledButton(confirmButtonText, type.getColor(), true);
        confirmButton.setOnAction(e -> {
            if (callback != null) {
                callback.accept(true);
            }
            closeWithAnimation();
        });

        buttonBox.getChildren().add(confirmButton);

        // Cancel button (optional)
        if (showCancelButton) {
            Button cancelButton = createStyledButton(cancelButtonText, "#112250", false);
            cancelButton.setOnAction(e -> {
                if (callback != null) {
                    callback.accept(false);
                }
                closeWithAnimation();
            });
            buttonBox.getChildren().add(0, cancelButton);
        }

        contentSection.getChildren().addAll(messageLabel, buttonBox);
        mainContainer.getChildren().addAll(headerSection, contentSection);


        StackPane wrapper = new StackPane(mainContainer);
        wrapper.setStyle("-fx-background-color: transparent;");
        wrapper.setAlignment(Pos.CENTER);

        Scene scene = new Scene(wrapper, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        dialog.setScene(scene);

        // Show with animation
        showWithAnimation(mainContainer);

        dialog.showAndWait();
    }

    private Button createStyledButton(String text, String color, boolean isPrimary) {
        Button button = new Button(text);
        button.setPrefWidth(140);
        button.setPrefHeight(45);
        button.setFont(Font.font("Alte Haas Grotesk", FontWeight.BOLD, 14));

        if (isPrimary) {
            button.setStyle(
                    "-fx-background-color: " + color + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 25;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-width: 0;"
            );

            button.setOnMouseEntered(e -> {
                button.setStyle(
                        "-fx-background-color: derive(" + color + ", -15%);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 25;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-width: 0;"
                );
            });

            button.setOnMouseExited(e -> {
                button.setStyle(
                        "-fx-background-color: " + color + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 25;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-width: 0;"
                );
            });
        } else {
            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: " + color + ";" +
                            "-fx-background-radius: 25;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: " + color + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 25;"
            );

            button.setOnMouseEntered(e -> {
                button.setStyle(
                        "-fx-background-color: " + color + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 25;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-color: " + color + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 25;"
                );
            });

            button.setOnMouseExited(e -> {
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: " + color + ";" +
                                "-fx-background-radius: 25;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-color: " + color + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 25;"
                );
            });
        }

        return button;
    }

    private void showWithAnimation(VBox container) {
        // Fade in background
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialog.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Scale in the dialog
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), container);
        scaleIn.setFromX(0.7);
        scaleIn.setFromY(0.7);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        fadeIn.play();
        scaleIn.play();
    }

    private void closeWithAnimation() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), dialog.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> dialog.close());
        fadeOut.play();
    }

    // Static helper methods for quick alerts
    public static void showWarning(Stage owner, String title, String header, String content, Consumer<Boolean> callback) {
        CustomAlert alert = new CustomAlert(AlertType.WARNING, title, header, content);
        alert.setCallback(callback);
        alert.show(owner);
    }

    public static void showInfo(Stage owner, String title, String header, String content) {
        CustomAlert alert = new CustomAlert(AlertType.INFO, title, header, content);
        alert.show(owner);
    }

    public static void showSuccess(Stage owner, String title, String header, String content) {
        CustomAlert alert = new CustomAlert(AlertType.SUCCESS, title, header, content);
        alert.show(owner);
    }

    public static void showError(Stage owner, String title, String header, String content) {
        CustomAlert alert = new CustomAlert(AlertType.ERROR, title, header, content);
        alert.show(owner);
    }

    public static void showConfirmation(Stage owner, String title, String header, String content, Consumer<Boolean> callback) {
        CustomAlert alert = new CustomAlert(AlertType.WARNING, title, header, content);
        alert.setShowCancelButton(true);
        alert.setCallback(callback);
        alert.show(owner);
    }
}