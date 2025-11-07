package com.ctrlaltelite.ctrlaltelite.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class BookCardController {
        @FXML private Button BookCoverButton;
        @FXML private ImageView BookCover;
        @FXML private Label Title;
        @FXML private Label Description;
        @FXML private Label Category;

        private String filePath;

        public void setBookData(String title, String description, String category, String coverPath, String filePath) {
            Title.setText(title);
            Description.setText(description);
            Category.setText(category);
            Image img = new Image(getClass().getResourceAsStream("/" + coverPath));
            BookCover.setImage(img);
            this.filePath = filePath;
        }

        @FXML
        private void openBook() {
            try {
                File pdfFile = new File(filePath);
                if (pdfFile.exists()) {
                    Desktop.getDesktop().open(pdfFile);
                }

                else if (filePath.startsWith("http")) {
                    Desktop.getDesktop().browse(URI.create(filePath));
                } else {
                    System.out.println("Book file not found: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

