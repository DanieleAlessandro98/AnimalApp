package it.uniba.dib.sms222334.Database.Dao;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import it.uniba.dib.sms222334.Database.AnimalAppDB;

public class AuthenticationDao {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void login(String email, String password, AuthenticationCallbackResult.Login listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            findRole(email, new FindRoleListenerResult() {
                                @Override
                                public void onRoleFound(int role) {
                                    listener.onLoginSuccessful(user, role);
                                }
                            });
                        } else {
                            listener.onLoginFailure();
                        }
                    }
                });
    }

    public void findRole(String email, FindRoleListenerResult listener) {
        // TODO: Invece di fare molte verifiche su tutte le tabelle presenti, creare unica tabella con id - ruolo
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        findPrivateRole(db, email, listener);
    }

    private void findPrivateRole(FirebaseFirestore db, String email, FindRoleListenerResult listener) {
        db.collection(AnimalAppDB.Private.TABLE_NAME).whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            listener.onRoleFound(1);
                        } else {
                            findPublicAuthorityRole(db, email, listener);
                        }
                    }
                });
    }

    private void findPublicAuthorityRole(FirebaseFirestore db, String email, FindRoleListenerResult listener) {
        db.collection(AnimalAppDB.PublicAuthority.TABLE_NAME).whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            listener.onRoleFound(2);
                        } else {
                            findVeterinarianRole(db, email, listener);
                        }
                    }
                });
    }

    private void findVeterinarianRole(FirebaseFirestore db, String email, FindRoleListenerResult listener) {
        db.collection(AnimalAppDB.Veterinarian.TABLE_NAME).whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            listener.onRoleFound(3);
                        } else {
                            listener.onRoleFound(-1);
                        }
                    }
                });
    }


    public interface FindRoleListenerResult {
        void onRoleFound(int role);
    }
}
