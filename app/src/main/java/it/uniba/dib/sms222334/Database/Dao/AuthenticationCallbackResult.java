package it.uniba.dib.sms222334.Database.Dao;

import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.UserRole;

public interface AuthenticationCallbackResult {
    interface Login {
        void onLoginSuccessful(User user);
        void onLoginFailure();
    }

    interface LoginCompletedListener {
        void onLoginCompleted();
    }
}
