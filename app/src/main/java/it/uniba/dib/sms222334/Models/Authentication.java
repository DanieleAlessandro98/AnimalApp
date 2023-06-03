package it.uniba.dib.sms222334.Models;

import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
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
        return SessionManager.getInstance().getCurrentUser().getRole();
    }

    @Override
    public void onLoginSuccessful(User user) {
        SessionManager.getInstance().loginUser(user);
        listenerRole.onLoginCompleted();
    }

    @Override
    public void onLoginFailure() {
        listenerRole.onLoginCompleted();
    }
}
