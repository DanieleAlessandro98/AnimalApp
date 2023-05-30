package it.uniba.dib.sms222334.Models;

import it.uniba.dib.sms222334.Database.Dao.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Database.Dao.AuthenticationDao;
import it.uniba.dib.sms222334.Utils.UserRole;

public class Authentication implements AuthenticationCallbackResult.Login, AuthenticationCallbackResult.Logout {

    private final AuthenticationDao authenticationDao;
    private final AuthenticationCallbackResult.LoginOrLogoutCompletedListener listenerRole;

    public Authentication(AuthenticationCallbackResult.LoginOrLogoutCompletedListener listenerRole) {
        authenticationDao = new AuthenticationDao();
        this.listenerRole = listenerRole;
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
        listenerRole.onLoginOrLogoutCompleted(true);
    }

    @Override
    public void onLoginFailure() {
        listenerRole.onLoginOrLogoutCompleted(false);
    }

    @Override
    public void onLogoutSuccessful() {
        SessionManager.getInstance().logoutUser();
        listenerRole.onLoginOrLogoutCompleted(true);
    }

    @Override
    public void onLogoutFailure() {
        listenerRole.onLoginOrLogoutCompleted(false);
    }
}
