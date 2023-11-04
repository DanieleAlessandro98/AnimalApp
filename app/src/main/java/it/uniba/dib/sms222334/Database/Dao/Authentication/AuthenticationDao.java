package it.uniba.dib.sms222334.Database.Dao.Authentication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;

public class AuthenticationDao {
    final static String TAG="AuthenticationDao";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void login(String email, String password, AuthenticationCallbackResult.Login listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        findUser(email, listener::onLoginSuccessful);
                    } else {
                        listener.onLoginFailure();
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
                                    listener.onUserFound(null);
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

                            publicAuthorityDao.findPublicAuthority(document, new PublicAuthorityDao.PublicAuthorityCallback() {
                                @Override
                                public void onPublicAuthorityFound(PublicAuthority resultPublicAuthority) {
                                    publicAuthorityDao.loadPublicAuthorityAnimals(document,resultPublicAuthority,null);
                                    listener.onUserFound(resultPublicAuthority);
                                }

                                @Override
                                public void onPublicAuthorityFindFailed(Exception exception) {
                                    listener.onUserFound(null);
                                }
                            });



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


                            veterinarianDao.findVeterinarian(document, new VeterinarianDao.VeterinarianCallback() {
                                @Override
                                public void onVeterinarianFound(Veterinarian resultVeterinarian) {
                                    veterinarianDao.loadVeterinarianVisits(resultVeterinarian);

                                    listener.onUserFound(resultVeterinarian);
                                }

                                @Override
                                public void onVeterinarianFindFailed(Exception exception) {
                                    listener.onUserFound(null);
                                }
                            });
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
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listener.onLogoutSuccessful();
                        } else {
                            listener.onLogoutFailure();
                        }
                    });
        }
    }

    public static void fireAuth(String email, String password, DocumentReference documentReference, UserCallback.UserRegisterCallback callback) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onRegisterSuccess())
                .addOnFailureListener(e -> {
                    //Il authentication fail, remove the created document
                    documentReference.delete().addOnCompleteListener(deleteTask -> callback.onRegisterFail());
                });
    }

    public interface FindUserListenerResult {
        void onUserFound(User user);
    }
}
