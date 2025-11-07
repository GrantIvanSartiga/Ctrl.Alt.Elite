


package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;


public class RegisterController {

    @FXML
    private Button cancelButton;
    @FXML
    private Label registerMessageLabel;
    @FXML
    private Label conpassMessageLabel;
    @FXML
    private TextField firstnameField;
    @FXML
    private TextField lastnameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField conpassField;


    @FXML
    public void initialize() {
        cancelButton.setOnAction(event ->cancelButtonOnAction());
    }

    public void registerButtonOnAction(ActionEvent event) {
        String firstName = firstnameField.getText().trim();
        String lastName = lastnameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = conpassField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerMessageLabel.setText("Missing Information. Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            conpassMessageLabel.setText("Passwords do not match.");
            return;
        }

        if (DatabaseConnection.emailExists(email)) {
            registerMessageLabel.setText("An account with this email already exists.");
            return;
        }

        try {
            LocalDateTime now = LocalDateTime.now();

            DatabaseConnection.addUser(firstName, lastName, email, username, password);

            registerMessageLabel.setText("Registration Successful. Press Cancel to Proceed to Login.");
            clearForm();
        } catch (Exception e) {
            registerMessageLabel.setText("Registration Failed");
        }


    }

    public void cancelButtonOnAction() {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("login-window.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);

            Stage stage = (Stage) cancelButton.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("CtrlAltElite");
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        firstnameField.clear();
        lastnameField.clear();
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
        conpassField.clear();
    }
}
