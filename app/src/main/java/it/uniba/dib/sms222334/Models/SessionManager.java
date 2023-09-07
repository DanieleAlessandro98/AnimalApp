package it.uniba.dib.sms222334.Models;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }

        return instance;
    }

    public void loginUser(User user) {
        this.currentUser = user;
    }

    public void logoutUser() {
        this.currentUser = null;
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLogged() {
        return currentUser != null;
    }
}
