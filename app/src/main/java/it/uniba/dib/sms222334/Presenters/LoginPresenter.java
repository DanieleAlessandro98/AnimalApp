package it.uniba.dib.sms222334.Presenters;

import android.util.Patterns;

import it.uniba.dib.sms222334.Activity.LoginActivity;
import it.uniba.dib.sms222334.Database.Dao.AuthenticationCallbackResult;
import it.uniba.dib.sms222334.Models.Authentication;

public class LoginPresenter implements AuthenticationCallbackResult.LoginCompletedListener  {

    private final LoginActivity loginActivity;
    private final Authentication loginModel;

    public LoginPresenter(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.loginModel = new Authentication(this);
    }

    public void onLogin(String email, String password) {
        if (!isValidEmail(email)) {
            loginActivity.showInvalidEmail();
            return;
        }
        if (!isValidPassword(password)) {
            loginActivity.showInvalidPassword();
            return;
        }

        loginModel.login(email, password);
    }

    @Override
    public void onLoginCompleted() {
        if (loginModel.isLogged()) {
            loginActivity.authSuccessful(loginModel.getUserRole());
        } else {
            loginActivity.showLoginError();
        }
    }

    private boolean isValidEmail(String email) {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean isValidPassword(String password) {
        return (password.length() >= 6);
    }
}
