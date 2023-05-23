package it.uniba.dib.sms222334.Models;

import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms222334.Database.Dao.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.AuthenticationDao;

public class Authentication implements AuthenticationCallbackResult.Login {

    private static FirebaseUser user;   //TODO: Che ci memorizziamo in questa classe? per ora mi salvo l'utente loggato.
    private static int userRole;
    private final AuthenticationDao authenticationDao;
    private final AuthenticationCallbackResult.LoginCompletedListener  listenerRole;

    public Authentication(AuthenticationCallbackResult.LoginCompletedListener  listenerRole) {
        authenticationDao = new AuthenticationDao();
        this.listenerRole = listenerRole;
        this.user = null;
        this.userRole = -1;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public boolean isLogged() {
        return (user != null && userRole != -1);
    }

    public static int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public void login(String email, String password) {
        authenticationDao.login(email, password, this);
    }

    @Override
    public void onLoginSuccessful(FirebaseUser user, int role) {
        setUser(user);
        setUserRole(role);
        listenerRole.onLoginCompleted();
    }

    @Override
    public void onLoginFailure() {
        setUser(null);
        setUserRole(-1);
        listenerRole.onLoginCompleted();
    }
}
