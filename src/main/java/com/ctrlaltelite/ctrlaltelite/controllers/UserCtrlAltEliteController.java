package com.ctrlaltelite.ctrlaltelite.controllers;

import com.ctrlaltelite.ctrlaltelite.CtrlAltEliteApplication;
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UserCtrlAltEliteController {
    @FXML
    private JFXButton profileButton;
    @FXML
    private Text welcomeText;
    @FXML
    private ImageView ICON;
    @FXML
    private Pane Logo;
    @FXML
    private Text Text2;
    @FXML
    private HBox SearchIcon;
    @FXML
    private HBox SearchAndButtons;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane heroSection;
    @FXML
    private AnchorPane contentSection;

    private boolean heroVisible = false;
    private boolean contentVisible = false;

    @FXML
    public void initialize() {

        welcomeText.setOpacity(0);
        Text2.setOpacity(0);
        ICON.setOpacity(0);
        SearchIcon.setOpacity(0);
        contentSection.setOpacity(0);

        Logo.setTranslateX(-50);
        SearchAndButtons.setTranslateX(50);
        ICON.setTranslateY(80);
        SearchIcon.setTranslateY(80);
        contentSection.setTranslateY(40);


        TranslateTransition slideSearchAndButtons = new TranslateTransition(Duration.seconds(1), SearchAndButtons);
        slideSearchAndButtons.setFromX(50);
        slideSearchAndButtons.setToX(0);

        FadeTransition fadeSearchAndButtons = new FadeTransition(Duration.seconds(1), SearchAndButtons);
        fadeSearchAndButtons.setFromValue(0);
        fadeSearchAndButtons.setToValue(1);

        TranslateTransition slideLogo = new TranslateTransition(Duration.seconds(1), Logo);
        slideLogo.setFromX(-50);
        slideLogo.setToX(0);

        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(1), Logo);
        fadeLogo.setFromValue(0);
        fadeLogo.setToValue(1);

        ParallelTransition logoAnim = new ParallelTransition(slideLogo, fadeLogo);
        ParallelTransition buttonsAnim = new ParallelTransition(slideSearchAndButtons, fadeSearchAndButtons);

        logoAnim.play();
        buttonsAnim.play();

        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> checkVisibility());


        profileButton.setOnAction(actionEvent -> LoginUser());


        checkVisibility();
    }

    private void checkVisibility() {
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double scrollY = scrollPane.getVvalue() * (contentHeight - viewportHeight);


        double heroY = heroSection.getBoundsInParent().getMinY();
        double heroBottom = heroSection.getBoundsInParent().getMaxY();
        boolean heroInView = heroBottom > scrollY && heroY < scrollY + viewportHeight;

        if (heroInView && !heroVisible) {
            playHeroAnimation();
            heroVisible = true;
        } else if (!heroInView && heroVisible) {
            resetHeroElements();
            heroVisible = false;
        }


        double contentY = contentSection.getBoundsInParent().getMinY();
        boolean contentInView = contentY < scrollY + viewportHeight - 150;

        if (contentInView && !contentVisible) {
            playContentAnimation();
            contentVisible = true;
        } else if (!contentInView && contentVisible) {
            resetContentElements();
            contentVisible = false;
        }
    }


    private void playHeroAnimation() {

        FadeTransition fadeWelcome = new FadeTransition(Duration.seconds(2), welcomeText);
        fadeWelcome.setFromValue(0);
        fadeWelcome.setToValue(1);

        FadeTransition fadeText2 = new FadeTransition(Duration.seconds(2), Text2);
        fadeText2.setFromValue(0);
        fadeText2.setToValue(1);

        TranslateTransition slideIcon = new TranslateTransition(Duration.seconds(1), ICON);
        slideIcon.setFromY(80);
        slideIcon.setToY(0);

        FadeTransition fadeIcon = new FadeTransition(Duration.seconds(2), ICON);
        fadeIcon.setFromValue(0);
        fadeIcon.setToValue(1);

        TranslateTransition slideSearchIcon = new TranslateTransition(Duration.seconds(2), SearchIcon);
        slideSearchIcon.setFromY(80);
        slideSearchIcon.setToY(0);

        FadeTransition fadeSearchIcon = new FadeTransition(Duration.seconds(2), SearchIcon);
        fadeSearchIcon.setFromValue(0);
        fadeSearchIcon.setToValue(1);

        ParallelTransition iconAnim = new ParallelTransition(slideIcon, fadeIcon);
        ParallelTransition searchIconAnim = new ParallelTransition(slideSearchIcon, fadeSearchIcon);

        fadeWelcome.play();
        fadeText2.play();
        iconAnim.play();
        searchIconAnim.play();
    }

    private void resetHeroElements() {

        welcomeText.setOpacity(0);
        Text2.setOpacity(0);
        ICON.setOpacity(0);
        ICON.setTranslateY(80);
        SearchIcon.setOpacity(0);
        SearchIcon.setTranslateY(80);
    }

    private void playContentAnimation() {
        FadeTransition fade = new FadeTransition(Duration.millis(800), contentSection);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), contentSection);
        slide.setFromY(40);
        slide.setToY(0);

        fade.play();
        slide.play();
    }

    private void resetContentElements() {
        contentSection.setOpacity(0);
        contentSection.setTranslateY(40);
    }

    @FXML
    private void LoginUser(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(CtrlAltEliteApplication.class.getResource("login-window.fxml"));

            Stage loginStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            loginStage.setScene(scene);
            loginStage.setTitle("Login");
            loginStage.setMinWidth(600);
            loginStage.setMinHeight(400);
            loginStage.setResizable(true);
            loginStage.setMaximized(true);

            loginStage.show();

            ((Stage) profileButton.getScene().getWindow()).close();



        } catch (Exception e) {
            System.err.println("ERROR loading login window:");
            e.printStackTrace();
        }
    }
}
