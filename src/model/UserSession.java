package model;

public class UserSession {

    private static User currentUser = null;

    private UserSession() {
    }

    public static void setSession(User user) {
        currentUser = user;
    }

    public static User getSession() {
        return currentUser;
    }

    public static String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "admin";
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : "Admin";
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}
