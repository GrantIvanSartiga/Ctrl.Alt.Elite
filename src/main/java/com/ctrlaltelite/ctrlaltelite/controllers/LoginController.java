


package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.DatabaseConnection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginController {


    @FXML
    private Button HomeButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailTextField;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @FXML
    public void initialize() {
        registerButton.setOnAction(event -> createAccountForm());
        cancelButton.setOnAction(event -> cancelButtonOnAction());
        HomeButton.setOnAction(event -> cancelButtonOnAction());

    }

    public void loginButtonOnAction(ActionEvent event) throws Exception {

        if (emailTextField.getText().isBlank() == false && passwordField.getText().isBlank() == false) {
            String email = emailTextField.getText();
            String password = passwordField.getText();

            if (DatabaseConnection.validateLogin(email, password)) {
                loginMessageLabel.setText("Login Successful. Welcome back!");

                UserManager.login(email);

                FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-view.fxml"));
                Parent root = fxmlLoader.load();

                Stage overviewStage = new Stage();
                overviewStage.setScene(new Scene(root));
                overviewStage.setMaximized(true);
                overviewStage.show();

                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();


            } else {
                loginMessageLabel.setText("Login Failed. Invalid email or password.");
            }

        } else {
            loginMessageLabel.setText("Please enter username and password");
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

    }
        public void cancelButtonOnAction (){
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
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

    public void createAccountForm(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("register-window.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 470);
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(scene);
            registerStage.show();


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}
