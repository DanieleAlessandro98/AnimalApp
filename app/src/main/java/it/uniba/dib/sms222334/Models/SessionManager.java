package it.uniba.dib.sms222334.Models;

import it.uniba.dib.sms222334.Utils.UserRole;

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

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLogged() {
        return currentUser != null;
    }

    public void updateCurrentUser(User user) {
        loginUser(user);
    }
}
