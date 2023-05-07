package it.uniba.dib.sms222334.Database.Dao;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import it.uniba.dib.sms222334.Models.Authentication;

public class AuthenticationDao {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void login(String email, String password, Authentication.LoginListenerResult listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            listener.onLoginSuccessful(user);
                        } else {
                            listener.onLoginFailure();
                        }
                    }
                });
    }

}
