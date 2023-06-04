package it.uniba.dib.sms222334.Models;

import it.uniba.dib.sms222334.Database.Dao.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.AuthenticationDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Authentication implements AuthenticationCallbackResult.Login, AuthenticationCallbackResult.Logout {

    private final AuthenticationDao authenticationDao;
    private AuthenticationCallbackResult.LoginCompletedListener listenerLogin;
    private AuthenticationCallbackResult.LogoutCompletedListener listenerLogout;

    public Authentication(AuthenticationCallbackResult.LoginCompletedListener listenerLogin) {
        authenticationDao = new AuthenticationDao();
        this.listenerLogin = listenerLogin;
    }

    public Authentication(AuthenticationCallbackResult.LogoutCompletedListener listenerLogout) {
        authenticationDao = new AuthenticationDao();
        this.listenerLogout = listenerLogout;
    }

    public void login(String email, String password) {
        authenticationDao.login(email, password, this);
    }

    public void delete() {
        authenticationDao.delete(this);
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
        listenerLogin.onLoginCompleted(true);
    }

    @Override
    public void onLoginFailure() {
        listenerLogin.onLoginCompleted(false);
    }

    @Override
    public void onLogoutSuccessful() {
        SessionManager.getInstance().logoutUser();
        listenerLogout.onLogoutCompleted(true);
    }

    @Override
    public void onLogoutFailure() {
        listenerLogout.onLogoutCompleted(false);
    }
}
