package it.uniba.dib.sms222334.Database.Dao;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.UserRole;

public class AuthenticationDao {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void login(String email, String password, AuthenticationCallbackResult.Login listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            findUser(email, new FindUserListenerResult() {
                                @Override
                                public void onUserFound(User user) {
                                    listener.onLoginSuccessful(user);
                                }
                            });
                        } else {
                            listener.onLoginFailure();
                        }
                    }
                });
    }

    public void findUser(String email, FindUserListenerResult listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        findPrivateUser(db, email, listener);
    }

    private void findPrivateUser(FirebaseFirestore db, String email, FindUserListenerResult listener) {
        db.collection(AnimalAppDB.Private.TABLE_NAME).whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            PrivateDao privateDao = new PrivateDao();
                            User user = privateDao.findPrivate(document);

                            listener.onUserFound(user);
                        } else {
                            findPublicAuthorityUser(db, email, listener);
                        }
                    }
                });
    }

    private void findPublicAuthorityUser(FirebaseFirestore db, String email, FindUserListenerResult listener) {
        db.collection(AnimalAppDB.PublicAuthority.TABLE_NAME).whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            PublicAuthorityDao publicAuthorityDao = new PublicAuthorityDao();
                            User user = publicAuthorityDao.findPublicAuthority(document);

                            listener.onUserFound(user);
                        } else {
                            findVeterinarianUser(db, email, listener);
                        }
                    }
                });
    }

    private void findVeterinarianUser(FirebaseFirestore db, String email, FindUserListenerResult listener) {
        db.collection(AnimalAppDB.Veterinarian.TABLE_NAME).whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            VeterinarianDao veterinarianDao = new VeterinarianDao();
                            User user = veterinarianDao.findVeterinarian(document);

                            listener.onUserFound(user);
                        } else {
                            listener.onUserFound(null);
                        }
                    }
                });
    }


    public interface FindUserListenerResult {
        void onUserFound(User user);
    }
}
