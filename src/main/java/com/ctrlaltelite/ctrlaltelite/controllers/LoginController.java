package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.util.Duration;

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
    private BorderPane LoginUI;

    @FXML
    public void initialize() {
        registerButton.setOnAction(event -> createAccountForm());
        cancelButton.setOnAction(event ->cancelButtonOnAction());

        FadeTransition fadeLoginUI = new FadeTransition(Duration.seconds(1), LoginUI);
        fadeLoginUI.setFromValue(0);
        fadeLoginUI.setToValue(1);

        TranslateTransition slideLoginUI = new TranslateTransition(Duration.seconds(1), LoginUI);
        slideLoginUI.setFromY(80);
        slideLoginUI.setToY(0);

        FadeTransition fadeUserTxtField = new FadeTransition(Duration.seconds(3), usernameTextField);
        fadeUserTxtField.setFromValue(0);
        fadeUserTxtField.setToValue(1);

        TranslateTransition slideUserTxtField = new TranslateTransition(new Duration(3),usernameTextField);
        slideUserTxtField.setFromY(20);
        slideUserTxtField.setToY(0);

        FadeTransition fadePassField = new FadeTransition(Duration.seconds(3), passwordField);
        fadePassField.setFromValue(0);
        fadePassField.setToValue(1);

        TranslateTransition slidePassField = new TranslateTransition(new Duration(3),passwordField);
        slidePassField.setFromY(20);
        slidePassField.setToY(0);

        ParallelTransition PassFieldAnim = new ParallelTransition(slidePassField, fadePassField);
        ParallelTransition LoginUIAnim = new ParallelTransition(slideLoginUI, fadeLoginUI);
        ParallelTransition UserTxtFieldAnim = new ParallelTransition(slideUserTxtField, fadeUserTxtField);

        PassFieldAnim.play();
        UserTxtFieldAnim.play();
        LoginUIAnim.play();
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

    public void validateLogin(){
//        MongoDatabase db = DatabaseHelper.getInstance().getDatabase();
//
//        if (db != null){
//            loginMessageLabel.setText("Connected to database: " + db.getName() + " with collection of " + db.getCollection("movies").getNamespace());
//            // loginMessageLabel.setText("Please enter username and password");
//        }




//        DatabaseConnection connectNow = new DatabaseConnection();
//        Connection connectDB = connectNow.getConnection();
//
//        String verifyLogin = "SELECT count(1) FROM caeusers.useraccounts WHERE username = '"
//                + usernameTextField.getText() + "' AND password = ' " + passwordField.getText() + "'" ;
//
//        try {
//            Statement statement = connectDB.createStatement();
//            ResultSet queryResult = statement.executeQuery(verifyLogin);
//
//            while (queryResult.next()) {
//                if (queryResult.getInt(1) == 1) {
//                    loginMessageLabel.setText("Login successful");
//                    createAccountForm();
//                } else {
//                    loginMessageLabel.setText("Login failed");
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            e.getCause();
//        }

    }
    public void createAccountForm(){
        try{
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
