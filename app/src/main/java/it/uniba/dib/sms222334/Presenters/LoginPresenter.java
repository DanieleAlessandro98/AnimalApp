package it.uniba.dib.sms222334.Presenters;

import it.uniba.dib.sms222334.Activity.LoginActivity;
import it.uniba.dib.sms222334.Database.Dao.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Models.Authentication;
import it.uniba.dib.sms222334.Utils.Validations;

public class LoginPresenter implements AuthenticationCallbackResult.LoginCompletedListener {

    private final LoginActivity loginActivity;
    private final Authentication loginModel;

    public LoginPresenter(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.loginModel = new Authentication(this);
    }

    public void onLogin(String email, String password) {
        if (!Validations.isValidEmail(email)) {
            loginActivity.showInvalidEmail();
            return;
        }
        if (!Validations.isValidPassword(password)) {
            loginActivity.showInvalidPassword();
            return;
        }

        loginModel.login(email, password);
    }

    @Override
    public void onLoginCompleted(boolean isSuccessful) {
        if (isSuccessful && loginModel.isLogged()) {
            loginActivity.authSuccessful(loginModel.getUserRole());
        } else {
            loginActivity.showLoginError();
        }
    }
}
