package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
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


    @FXML
    public void initialize() {
        cancelButton.setOnAction(event ->cancelButtonOnAction());
    }

    public void registerButtonOnAction(ActionEvent event) {

        if (passwordField.getText().equals(conpassField.getText())) {
            registerUser();
            conpassMessageLabel.setText("Valid Match");
        } else {
            conpassMessageLabel.setText("Invalid Match");
        }


    }

    public void cancelButtonOnAction() {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("overview-view.fxml"));
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
