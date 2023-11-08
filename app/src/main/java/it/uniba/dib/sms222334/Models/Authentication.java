package it.uniba.dib.sms222334.Models;

import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;

import it.uniba.dib.sms222334.Utils.UserRole;

public class Authentication implements AuthenticationCallbackResult.Login, AuthenticationCallbackResult.Logout, AuthenticationCallbackResult.UpdateAuthentication {

    private final AuthenticationDao authenticationDao;
    private AuthenticationCallbackResult.LoginCompletedListener listenerLogin;
    private AuthenticationCallbackResult.LogoutCompletedListener listenerLogout;
    private AuthenticationCallbackResult.UpdateAuthentication listenerUpdate;

    public Authentication(AuthenticationCallbackResult.LoginCompletedListener listenerLogin) {
        authenticationDao = new AuthenticationDao();
        this.listenerLogin = listenerLogin;
    }

    public Authentication(AuthenticationCallbackResult.LogoutCompletedListener listenerLogout) {
        authenticationDao = new AuthenticationDao();
        this.listenerLogout = listenerLogout;
    }

    public Authentication(AuthenticationCallbackResult.UpdateAuthentication listenerUpdate) {
        authenticationDao = new AuthenticationDao();
        this.listenerUpdate = listenerUpdate;
    }

    public void login(String email, String password) {
        authenticationDao.login(email, password, this);
    }

    public void delete() {
        authenticationDao.delete(this);
    }

    public void updateUserAuth(String email, String password) {
        authenticationDao.updateUserAuth(email, password, this);
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
        listenerLogout.onLogoutCompleted(true);
    }

    @Override
    public void onLogoutFailure() {
        listenerLogout.onLogoutCompleted(false);
    }

    @Override
    public void onUpdateSuccessful() {
        listenerUpdate.onUpdateSuccessful();
    }

    @Override
    public void onUpdateFailure() {
        listenerUpdate.onUpdateFailure();
    }
}
