package it.uniba.dib.sms222334.Models;

import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms222334.Database.Dao.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.AuthenticationDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Authentication implements AuthenticationCallbackResult.Login {

    private final AuthenticationDao authenticationDao;
    private final AuthenticationCallbackResult.LoginCompletedListener  listenerRole;

    public Authentication(AuthenticationCallbackResult.LoginCompletedListener  listenerRole) {
        authenticationDao = new AuthenticationDao();
        this.listenerRole = listenerRole;
    }

    public void login(String email, String password) {
        authenticationDao.login(email, password, this);
    }

    public boolean isLogged() {
        return SessionManager.getInstance().isLogged();
    }

    public UserRole getUserRole() {
        return SessionManager.getInstance().getRole();
    }

    @Override
    public void onLoginSuccessful(User user, UserRole role) {
        SessionManager.getInstance().loginUser(user, role);
        listenerRole.onLoginCompleted();
    }

    @Override
    public void onLoginFailure() {
        listenerRole.onLoginCompleted();
    }
}
