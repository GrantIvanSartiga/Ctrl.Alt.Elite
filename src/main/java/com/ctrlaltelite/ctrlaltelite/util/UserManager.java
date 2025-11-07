


package com.ctrlaltelite.ctrlaltelite.util;

public class UserManager {
    private static String currentUser;

    public static void login(String email) {
        currentUser = email;
    }

    public static void logout() {
        currentUser = null;
    }

    public static void setCurrentUser(String email) {
        currentUser = email;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public class User {
        private String userId;
        private String name;
        private String email;

        public User(String userId, String name, String email) {
            this.userId = userId;
            this.name = name;
            this.email = email;
        }

        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}


