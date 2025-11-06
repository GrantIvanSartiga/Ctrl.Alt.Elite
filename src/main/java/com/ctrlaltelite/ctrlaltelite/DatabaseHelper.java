//package com.ctrlaltelite.ctrlaltelite;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoDatabase;
//
//public class DatabaseHelper {
//    protected final String dbUrl = "mongodb+srv://AC:prussianperiwinkle@user.6dez51a.mongodb.net/?appName=User";
//    protected final String dbName = "sample_mflix";
//
//
//    private static final DatabaseHelper instance = new DatabaseHelper();
//    private MongoDatabase database;
//
//    private MongoClient mongoClient;
//
//
//    private DatabaseHelper() {
//    }
//
//    public MongoClient getDatabaseClient(){
//        if (this.mongoClient == null){
//            this.mongoClient = MongoClients.create(dbUrl);
//        }
//        return this.mongoClient;
//    }
//
//    public static DatabaseHelper getInstance() {
//        return instance;
//    }
//
//    public MongoDatabase getDatabase() {
//        if (this.database == null) {
//            try (MongoClient mongoClient = MongoClients.create(dbUrl)) {
//                this.database = mongoClient.getDatabase("sample_mflix");
//            }
//        }
//
//        return this.database;
//    }
//}
