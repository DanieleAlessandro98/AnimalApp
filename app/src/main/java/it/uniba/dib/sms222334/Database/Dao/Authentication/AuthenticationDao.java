package it.uniba.dib.sms222334.Database.Dao.Authentication;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.CompletableFuture;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.User;

public class AuthenticationDao {
    final static String TAG="AuthenticationDao";
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

                            privateDao.findPrivate(document, new PrivateDao.PrivateCallback() {
                                @Override
                                public void onPrivateFound(Private resultPrivate) {
                                    privateDao.loadPrivateAnimals(document, resultPrivate,null);
                                    listener.onUserFound(resultPrivate);
                                }

                                @Override
                                public void onPrivateFindFailed(Exception exception) {

                                }
                            });
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
                            PublicAuthority authorityFound = publicAuthorityDao.findPublicAuthority(document);

                            publicAuthorityDao.loadPublicAuthorityAnimals(document,authorityFound,null);

                            User user = authorityFound;

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

    public void delete(AuthenticationCallbackResult.Logout listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                listener.onLogoutSuccessful();
                            } else {
                                listener.onLogoutFailure();
                            }
                        }
                    });
        }
    }

    public static void fireAuth(String email, String password, DocumentReference documentReference, UserCallback.UserRegisterCallback callback) {
        // Esegui l'autenticazione dell'utente
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "Autenticazione riuscita");
                    callback.onRegisterSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Errore durante l'autenticazione", e);

                    // Se l'autenticazione fallisce, elimina il documento creato
                    documentReference.delete().addOnCompleteListener(deleteTask -> {
                        if (deleteTask.isSuccessful()) {
                            Log.d(TAG, "Documento eliminato con successo");
                        } else {
                            Log.w(TAG, "Errore durante l'eliminazione del documento", deleteTask.getException());
                        }

                        // Chiamare il callback di registrazione fallita dopo aver gestito l'eliminazione
                        callback.onRegisterFail();
                    });
                });
    }

    public void isEmailUnique(String email, FindSameEmail emailfind ) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
                            emailfind.emailfind(emailExists);
                        } else {
                            emailfind.emailfind(false);
                        }
                    }
                });
    }
    public interface FindUserListenerResult {
        void onUserFound(User user);
    }

    public interface FindSameEmail{
        void emailfind(boolean result);
    }

}
