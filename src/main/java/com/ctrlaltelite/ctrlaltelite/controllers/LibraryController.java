package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    @FXML
    private FlowPane bookContainer;

    @FXML
    private JFXButton profileButton;  // Changed to JFXButton to match FXML

    @FXML
    private JFXButton homeButton;  // Add this for Home button

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("LibraryController initializing...");

        loadSampleBooks();

        // Set up button actions
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
    }

    private void loadSampleBooks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ctrlaltelite/ctrlaltelite/book-holder.fxml"));
            AnchorPane bookCard = loader.load();
            bookContainer.getChildren().add(bookCard);
            System.out.println("Sample book card added!");
        } catch (IOException e) {
            System.err.println("Error loading book card: " + e.getMessage());
            e.printStackTrace();
        }
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