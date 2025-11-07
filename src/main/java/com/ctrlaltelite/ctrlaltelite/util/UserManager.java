


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

}


