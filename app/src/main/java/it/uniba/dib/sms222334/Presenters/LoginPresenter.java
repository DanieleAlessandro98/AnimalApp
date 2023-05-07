package it.uniba.dib.sms222334.Presenters;

import it.uniba.dib.sms222334.Activity.LoginActivity;
import it.uniba.dib.sms222334.Models.Authentication;

public class LoginPresenter {

    private LoginActivity loginActivity;
    private Authentication loginModel;

    public LoginPresenter(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.loginModel = new Authentication();
    }

    public void onLogin(String email, String password) {
        boolean loginSuccess = loginModel.login(email, password);

        if (loginSuccess) {
            loginActivity.showLoginSuccess();
        } else {
            loginActivity.showLoginError();
        }
    }
}
