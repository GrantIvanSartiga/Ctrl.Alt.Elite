package com.ctrlaltelite.ctrlaltelite;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

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

    /**
     * Upload a file to the database
     * @param file The file to upload
     * @param email The email of the user uploading the file
     * @return The file ID (ObjectId as String)
     */

    public static String uploadFile(File file, String email) throws Exception {
        byte[] fileData = readFileToBytes(file);

        Document fileDoc = new Document("filename", file.getName())
                .append("file_data", new Binary(fileData))
                .append("file_size", file.length())
                .append("file_type", getFileType(file))
                .append("email", email)
                .append("upload_date", LocalDateTime.now())
                .append("absolute_path", file.getAbsolutePath());

        var result = filesCollection.insertOne(fileDoc);
        return result.getInsertedId().asObjectId().getValue().toString();
    }

    /**
     * Get a specific file by ID
     */
    public static Document getFile(String fileId) throws Exception {
        return filesCollection.find(new Document("_id", new ObjectId(fileId))).first();
    }

    /**
     * Get all files uploaded by a specific user
     */
    public static FindIterable<Document> getUserFiles(String email) {
        return filesCollection.find(eq("email", email))
                .sort(descending("upload_date"));
    }

    /**
     * Delete a file by ID
     */
    public static void deleteFile(String fileId) throws Exception {
        filesCollection.deleteOne(new Document("_id", new ObjectId(fileId)));
    }

    /**
     * Delete all files uploaded by a user
     */
    public static void deleteUserFiles(String email) throws Exception {
        filesCollection.deleteMany(eq("email", email));
    }

    /**
     * Update file metadata
     */
    public static void updateFileMetadata(String fileId, String newFilename) throws Exception {
        filesCollection.updateOne(
                new Document("_id", new ObjectId(fileId)),
                new Document("$set", new Document("filename", newFilename))
        );
    }

    /**
     * Get file count for a user
     */
    public static long getUserFileCount(String email) {
        return filesCollection.countDocuments(eq("email", email));
    }

    /**
     * Get total storage used by a user (in bytes)
     */
    public static long getUserStorageUsed(String email) {
        long totalSize = 0;
        FindIterable<Document> files = filesCollection.find(eq("email", email));
        for (Document file : files) {
            totalSize += file.getLong("file_size");
        }
        return totalSize;
    }

    /**
     * Check if a file exists
     */
    public static boolean fileExists(String fileId) {
        return filesCollection.find(new Document("_id", new ObjectId(fileId))).first() != null;
    }

    /**
     * Search files by filename
     */
    public static FindIterable<Document> searchFilesByName(String email, String filename) {
        return filesCollection.find(
                new Document("email", email)
                        .append("filename", new Document("$regex", filename).append("$options", "i"))
        );
    }

    /**
     * Get files of a specific type
     */
    public static FindIterable<Document> getFilesByType(String email, String fileType) {
        return filesCollection.find(
                new Document("email", email).append("file_type", fileType)
        );
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
