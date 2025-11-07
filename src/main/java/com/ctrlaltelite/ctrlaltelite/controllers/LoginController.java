package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;


public class LoginController {

    // Hardcoded admin credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";


    @FXML
    private Button HomeButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        registerButton.setOnAction(event -> createAccountForm());
        cancelButton.setOnAction(event -> cancelButtonOnAction());
        HomeButton.setOnAction(event -> cancelButtonOnAction());

    }

    public void loginButtonOnAction(ActionEvent event) {
        if (usernameTextField.getText().isBlank() == false && passwordField.getText().isBlank() == false) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter username and password");
        }
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

    public void validateLogin() {
        String enteredUsername = usernameTextField.getText();
        String enteredPassword = passwordField.getText();

        // Check if admin credentials
        if (enteredUsername.equals(ADMIN_USERNAME) && enteredPassword.equals(ADMIN_PASSWORD)) {
            loginMessageLabel.setText("Admin login successful!");
            openAdminPage();
        }
        // TODO: Add regular user authentication here when database is ready
        else {
            loginMessageLabel.setText("Invalid username or password");
        }

        // Commented out database code for when your groupmate is ready
        /*
        MongoDatabase db = DatabaseHelper.getInstance().getDatabase();

        if (db != null) {
            // Check database for regular users
            // Your MongoDB authentication logic here
        }
        */
    }

    private void openAdminPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-viewAdmin.fxml"));

            Stage adminStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);

            adminStage.setScene(scene);
            adminStage.setTitle("CtrlAltElite");
            adminStage.setMinWidth(600);
            adminStage.setMinHeight(400);
            adminStage.setResizable(true);
            adminStage.setMaximized(true);
            adminStage.show();

            // Close login window
            ((Stage) loginButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Error loading admin panel");
        }
    }

    public void createAccountForm() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("register.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 470);
            registerStage.setScene(scene);
            registerStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();


        }

    }
}