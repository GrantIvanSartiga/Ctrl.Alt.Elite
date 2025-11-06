package com.ctrlaltelite.ctrlaltelite.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
    private PasswordField conpassField;


    public void registerButtonOnAction(ActionEvent event) {

        if (passwordField.getText().equals(conpassField.getText())) {
            registerUser();
            conpassMessageLabel.setText("Valid Match");
        } else {
            conpassMessageLabel.setText("Invalid Match");
        }


    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    public void registerUser(){
//        DatabaseConnection connectNow = new DatabaseConnection();
//        Connection connectDB = connectNow.getConnection();
//
//        String firstName = firstnameField.getText();
//        String lastName = lastnameField.getText();
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//
//        String insertFields = " INSERT INTO useraccounts (FirstName, LastName, Username, Password) VALUES (' ";
//        String insertValues = firstName + "','" + lastName + "','" + username + "','" + password + "')";
//        String insertToRegister = insertFields + insertValues;
//
//        try {
//            Statement statement = connectDB.createStatement();
//            statement.executeUpdate(insertToRegister);
//
//            registerMessageLabel.setText("User Registered Successfully");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            e.getCause();
//        }
    }
}
