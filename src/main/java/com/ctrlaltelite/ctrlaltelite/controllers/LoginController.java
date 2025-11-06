package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
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


public class LoginController {

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



    public void loginButtonOnAction(ActionEvent event) {

        if (usernameTextField.getText().isBlank() == false && passwordField.getText().isBlank() == false) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter username and password");
        }

    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
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
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("register-window.fxml"));
            Stage registerStage = new Stage();
//            Scene scene = new Scene(fxmlLoader.load(), 600, 470);
//            registerStage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(fxmlLoader.load(), 600, 470);
            registerStage.setMinWidth(600);
            registerStage.setMinHeight(400);
            registerStage.setResizable(true);
            registerStage.setMaximized(true);
            registerStage.setScene(scene);
            registerStage.show();

//            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
//
//            registerStage.setMinWidth(600);
//            registerStage.setMinHeight(400);
//            registerStage.setResizable(true);
//            registerStage.setMaximized(true);
//            registerStage.show();


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}
