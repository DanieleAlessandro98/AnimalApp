package it.uniba.dib.sms222334.Models;

import com.google.firebase.auth.FirebaseUser;
import it.uniba.dib.sms222334.Database.Dao.AuthenticationDao;

public class Authentication {

    private FirebaseUser user;   //TODO: Che ci memorizziamo in questa classe? per ora mi salvo l'utente loggato.
    private AuthenticationDao authenticationDao;

    public Authentication() {
        authenticationDao = new AuthenticationDao();
        this.user = null;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public boolean isLogged() {
        return (user != null);
    }

    public boolean login(String email, String password) {
        LoginListenerResult listener = new LoginListenerResult() {
            @Override
            public void onLoginSuccessful(FirebaseUser user) {
                setUser(user);
            }

            @Override
            public void onLoginFailure() {
                setUser(null);
            }
        };

        authenticationDao.login(email, password, listener);
        return isLogged();
    }

    // TODO:
    // Come al solito, serve per comunicare con i metodi asincroni di firebase.
    // la spostiamo?. dove la mettiamo però? Nel dao? classe a parte? ecc
    public interface LoginListenerResult {
        void onLoginSuccessful(FirebaseUser user);
        void onLoginFailure();
    }
}
