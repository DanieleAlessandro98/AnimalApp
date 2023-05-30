package it.uniba.dib.sms222334.Database.Dao;

import it.uniba.dib.sms222334.Models.User;

public interface AuthenticationCallbackResult {
    interface Login {
        void onLoginSuccessful(User user);
        void onLoginFailure();
    }

    interface Logout {
        void onLogoutSuccessful();
        void onLogoutFailure();
    }

    interface LoginOrLogoutCompletedListener {
        void onLoginOrLogoutCompleted(boolean isSuccessful);
    }

}
