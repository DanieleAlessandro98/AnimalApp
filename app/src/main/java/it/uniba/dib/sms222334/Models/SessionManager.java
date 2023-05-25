package it.uniba.dib.sms222334.Models;

import it.uniba.dib.sms222334.Utils.UserRole;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private UserRole role;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }

        return instance;
    }

    public void loginUser(User user, UserRole role) {
        this.currentUser = user;
        this.role = role;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isLogged() {
        return currentUser != null;
    }
}
