package com.ctrlaltelite.ctrlaltelite.controllers;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
//
//public class LibraryController implements Initializable {
//    @FXML
//    private FlowPane bookContainer;
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        loadPurchasedBooks();
//    }
//
//    private void loadPurchasedBooks() {
////        MongoClient client = MongoClients.create("mongodb+srv://AC:prussianperiwinkle@user.6dez51a.mongodb.net/");
////        MongoDatabase db = client.getDatabase("Users");
////        MongoCollection<Document> books = db.getCollection("purchased_notes");
////
////        String currentUser = com.ctrlaltelite.ctrlaltelite.util.UserManager.getCurrentUser();
////        FindIterable<Document> purchasedBooks = books.find(Filters.in("purchasedBy", currentUser));
////
////        for (Document doc : purchasedBooks) {
////            try {
////                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ctrlaltelite/ctrlaltelite/book_card.fxml"));
////                AnchorPane card = loader.load();
////
////                BookCardController controller = loader.getController();
////                controller.setBookData(
////                        doc.getString("title"),
////                        doc.getString("description"),
////                        doc.getString("category"),
////                        doc.getString("coverImage"),
////                        doc.getString("filePath")
////                );
////
////                card.setOnMouseClicked(e -> openBookDetails(doc));
////
////                bookContainer.getChildren().add(card);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////
////        client.close();
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ctrlaltelite/ctrlaltelite/book-holder.fxml"));
//        AnchorPane bookCard = loader.load();
//
//        bookContainer.getChildren().add(bookCard);
//
//        System.out.println("Sample book card added!");
//    } catch(IOException e)    {
//        e.printStackTrace();
//    }
//}
//
//
//    private void openBookDetails(Document doc) {
//        System.out.println("Opening details for: " + doc.getString("title"));
//    }
//
////    private void openProfile() {
////        System.out.println("Profile button clicked!");
////        // You can open another FXML here later if you want:
////        // FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ctrlaltelite/ctrlaltelite/profile.fxml"));
////        // Parent root = loader.load();
////        // Stage stage = new Stage();
////        // stage.setScene(new Scene(root));
////        // stage.show();
////    }
//
//}


public class LibraryController implements Initializable {

    @FXML
    private FlowPane bookContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSampleBooks();
    }

    private void loadSampleBooks() {
        try {
            // Load the sample book card
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ctrlaltelite/ctrlaltelite/book-holder.fxml"));
            AnchorPane bookCard = loader.load();

            // Add it to the FlowPane
            bookContainer.getChildren().add(bookCard);

            System.out.println("Sample book card added!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void logoutButtonOnAction(ActionEvent event) {
//        Stage stage = (Stage) logoutButton.getScene().getWindow();
//        stage.close();
//        platform.close();
//    }
}


