package it.uniba.dib.sms222334.Database.Dao;

import com.google.firebase.auth.FirebaseUser;

public interface AuthenticationCallbackResult {
    interface Login {
        void onLoginSuccessful(FirebaseUser user, int role);
        void onLoginFailure();
    }

    interface LoginCompletedListener {
        void onLoginCompleted();
    }
}
