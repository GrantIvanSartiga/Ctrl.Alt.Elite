package com.ctrlaltelite.ctrlaltelite;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class FilesDatabaseConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://AC:prussianperiwinkle@user.6dez51a.mongodb.net/";
    private static final String DB_NAME = "Books";
    private static final String FILES_COLLECTION_NAME = "books";

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> filesCollection;

    static {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        database = mongoClient.getDatabase(DB_NAME);
        filesCollection = database.getCollection(FILES_COLLECTION_NAME);
    }

    public static String uploadFile(File file, String email, String title, String description, double price) throws Exception {
        byte[] fileData = readFileToBytes(file);

        Document fileDoc = new Document("filename", file.getName())
                .append("file_data", new Binary(fileData))
                .append("file_size", file.length())
                .append("file_type", getFileType(file))
                .append("email", email)
                .append("title", title)
                .append("description", description)
                .append("price", price)
                .append("upload_date", LocalDateTime.now())
                .append("absolute_path", file.getAbsolutePath());

        var result = filesCollection.insertOne(fileDoc);
        return result.getInsertedId().asObjectId().getValue().toString();
    }

    public static String uploadFile(File file, String email) throws Exception {
        return uploadFile(file, email, "Untitled", "No description provided", 0.0);
    }

    public static Document getFile(String fileId) throws Exception {
        return filesCollection.find(new Document("_id", new ObjectId(fileId))).first();
    }

    public static FindIterable<Document> getUserFiles(String email) {
        return filesCollection.find(eq("email", email))
                .sort(descending("upload_date"));
    }

    public static void deleteFile(String fileId) throws Exception {
        filesCollection.deleteOne(new Document("_id", new ObjectId(fileId)));
    }

    public static void deleteUserFiles(String email) throws Exception {
        filesCollection.deleteMany(eq("email", email));
    }

    public static void updateFileMetadata(String fileId, String newFilename, String title, String description, double price) throws Exception {
        Document updateDoc = new Document("filename", newFilename)
                .append("title", title)
                .append("description", description)
                .append("price", price);

        filesCollection.updateOne(
                new Document("_id", new ObjectId(fileId)),
                new Document("$set", updateDoc)
        );
    }

    public static void updateFileMetadata(String fileId, String newFilename) throws Exception {
        filesCollection.updateOne(
                new Document("_id", new ObjectId(fileId)),
                new Document("$set", new Document("filename", newFilename))
        );
    }

    public static void updateFileTitle(String fileId, String title) throws Exception {
        filesCollection.updateOne(
                new Document("_id", new ObjectId(fileId)),
                new Document("$set", new Document("title", title))
        );
    }

    public static void updateFileDescription(String fileId, String description) throws Exception {
        filesCollection.updateOne(
                new Document("_id", new ObjectId(fileId)),
                new Document("$set", new Document("description", description))
        );
    }

    public static void updateFilePrice(String fileId, double price) throws Exception {
        filesCollection.updateOne(
                new Document("_id", new ObjectId(fileId)),
                new Document("$set", new Document("price", price))
        );
    }

    public static long getUserFileCount(String email) {
        return filesCollection.countDocuments(eq("email", email));
    }

    public static long getUserStorageUsed(String email) {
        long totalSize = 0;
        FindIterable<Document> files = filesCollection.find(eq("email", email));
        for (Document file : files) {
            totalSize += file.getLong("file_size");
        }
        return totalSize;
    }

    public static boolean fileExists(String fileId) {
        return filesCollection.find(new Document("_id", new ObjectId(fileId))).first() != null;
    }

    public static FindIterable<Document> searchFilesByName(String email, String filename) {
        return filesCollection.find(
                new Document("email", email)
                        .append("filename", new Document("$regex", filename).append("$options", "i"))
        );
    }

    public static FindIterable<Document> searchFilesByTitle(String email, String title) {
        return filesCollection.find(
                new Document("email", email)
                        .append("title", new Document("$regex", title).append("$options", "i"))
        );
    }

    public static FindIterable<Document> getFilesByPriceRange(String email, double minPrice, double maxPrice) {
        return filesCollection.find(
                new Document("email", email)
                        .append("price", new Document("$gte", minPrice).append("$lte", maxPrice))
        ).sort(descending("upload_date"));
    }

    public static FindIterable<Document> getFilesByType(String email, String fileType) {
        return filesCollection.find(
                new Document("email", email).append("file_type", fileType)
        );
    }

    public static FindIterable<Document> getUserFilesSortedByPrice(String email, boolean ascending) {
        return filesCollection.find(eq("email", email))
                .sort(ascending ? new Document("price", 1) : new Document("price", -1));
    }

    public static FindIterable<Document> getAllFiles() {
        return filesCollection.find().sort(descending("upload_date"));
    }


    public static void addPurchasedFileToUser(String userEmail, String fileId, Double price) {
        try {
            MongoCollection<Document> purchasedNotesCollection = database.getCollection("purchased_notes");

            System.out.println("DEBUG: Inserting into purchased_notes");
            System.out.println("  - Email: " + userEmail);
            System.out.println("  - File ID: " + fileId);
            System.out.println("  - Price: " + price);

            Document purchaseRecord = new Document("user_email", userEmail)
                    .append("file_id", new ObjectId(fileId))
                    .append("purchase_date", new Date())
                    .append("price", price);

            purchasedNotesCollection.insertOne(purchaseRecord);

            System.out.println("✓ Successfully inserted into purchased_notes!");


            long count = purchasedNotesCollection.countDocuments(eq("user_email", userEmail));
            System.out.println("  - User now has " + count + " purchased files");

        } catch (Exception e) {
            System.err.println("ERROR adding purchased file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static List<Document> getUserPurchasedFiles(String userEmail) {
        try {
            MongoCollection<Document> purchasedNotesCollection = database.getCollection("purchased_notes");

            List<Document> purchasedFiles = new ArrayList<>();
            FindIterable<Document> purchases = purchasedNotesCollection.find(eq("user_email", userEmail));

            for (Document purchase : purchases) {
                purchasedFiles.add(purchase);
            }

            System.out.println("Found " + purchasedFiles.size() + " purchase records for " + userEmail);
            return purchasedFiles;
        } catch (Exception e) {
            System.err.println("Error fetching user purchased files: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Document> getUserPurchasedFilesDetails(String userEmail) {
        try {
            List<Document> purchasedRecords = getUserPurchasedFiles(userEmail);
            List<Document> fileDetails = new ArrayList<>();

            System.out.println("Processing " + purchasedRecords.size() + " purchase records...");

            for (Document purchase : purchasedRecords) {
                ObjectId fileId = (ObjectId) purchase.get("file_id");
                System.out.println("  Looking up file: " + fileId);

                Document fileDoc = filesCollection.find(eq("_id", fileId)).first();

                if (fileDoc != null) {
                    System.out.println("    ✓ Found: " + fileDoc.getString("title"));
                    // Add purchase info to file document
                    fileDoc.append("purchase_date", purchase.get("purchase_date"));
                    fileDoc.append("purchase_price", purchase.get("price"));
                    fileDoc.append("purchase_id", purchase.getObjectId("_id"));
                    fileDetails.add(fileDoc);
                } else {
                    System.out.println("    ✗ File not found in books collection");
                }
            }

            System.out.println("Returning " + fileDetails.size() + " file details");
            return fileDetails;
        } catch (Exception e) {
            System.err.println("Error fetching purchased file details: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static List<Document> getUserUploadedFiles(String userEmail) {
        try {
            List<Document> userFiles = new ArrayList<>();
            FindIterable<Document> files = filesCollection.find(eq("email", userEmail));

            for (Document file : files) {
                userFiles.add(file);
            }

            System.out.println("Found " + userFiles.size() + " uploaded files for " + userEmail);
            return userFiles;
        } catch (Exception e) {
            System.err.println("Error fetching user uploaded files: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static boolean hasUserPurchasedFile(String userEmail, String fileId) {
        try {
            MongoCollection<Document> purchasedNotesCollection = database.getCollection("purchased_notes");

            Document purchase = purchasedNotesCollection.find(
                    new Document("user_email", userEmail)
                            .append("file_id", new ObjectId(fileId))
            ).first();

            boolean hasPurchased = purchase != null;
            System.out.println("User " + userEmail + " has purchased file " + fileId + ": " + hasPurchased);
            return hasPurchased;
        } catch (Exception e) {
            System.err.println("Error checking purchase status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String createReceiptRecord(String userEmail, String fileId, Double totalPrice) {
        try {
            MongoCollection<Document> receiptsCollection = database.getCollection("receipts");

            String receiptId = "RCP-" + System.currentTimeMillis();

            Document receipt = new Document("receipt_id", receiptId)
                    .append("buyer_email", userEmail)
                    .append("file_id", new ObjectId(fileId))
                    .append("purchase_date", new Date())
                    .append("total", totalPrice)
                    .append("status", "completed");

            receiptsCollection.insertOne(receipt);
            System.out.println("Receipt created: " + receiptId);

            return receiptId;
        } catch (Exception e) {
            System.err.println("Error creating receipt: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Document getReceipt(String receiptId) {
        try {
            MongoCollection<Document> receiptsCollection = database.getCollection("receipts");
            return receiptsCollection.find(eq("receipt_id", receiptId)).first();
        } catch (Exception e) {
            System.err.println("Error fetching receipt: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static long getUserPurchaseCount(String userEmail) {
        try {
            MongoCollection<Document> purchasedNotesCollection = database.getCollection("purchased_notes");
            long count = purchasedNotesCollection.countDocuments(eq("user_email", userEmail));
            System.out.println("User " + userEmail + " has " + count + " purchases");
            return count;
        } catch (Exception e) {
            System.err.println("Error counting user purchases: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }


    private static byte[] readFileToBytes(File file) throws Exception {
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }
        return fileData;
    }

    private static String getFileType(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
            return "image";
        } else if (fileName.endsWith(".pdf")) {
            return "pdf";
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return "document";
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return "spreadsheet";
        } else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) {
            return "archive";
        } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov")) {
            return "video";
        } else if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")) {
            return "audio";
        } else {
            return "other";
        }
    }
}