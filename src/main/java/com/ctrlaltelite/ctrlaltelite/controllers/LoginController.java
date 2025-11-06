package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.ctrlaltelite.ctrlaltelite.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        cancelButton.setOnAction(event ->cancelButtonOnAction());
    }

    public void loginButtonOnAction(ActionEvent event) throws Exception{

        if (usernameTextField.getText().isBlank() == false && passwordField.getText().isBlank() == false) {
            String username = usernameTextField.getText();
            String password = passwordField.getText();

            if (DatabaseConnection.validateLogin(username, password)) {
                loginMessageLabel.setText("Login Successful. Welcome back!");

                FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-view.fxml"));
                Stage loginStage = new Stage();
                loginStage.initStyle(StageStyle.UNDECORATED);
                Stage currentStage = (Stage) cancelButton.getScene().getWindow();
                currentStage.close();

            } else {
                loginMessageLabel.setText("Login Failed. Invalid email or password.");
            }

        } else {
            loginMessageLabel.setText("Please enter username and password");
        }

    }

    public void cancelButtonOnAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-view.fxml"));

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

    public void createAccountForm(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("register-window.fxml"));
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
