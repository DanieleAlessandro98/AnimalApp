package it.uniba.dib.sms222334.Database.Dao.Authentication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Models.Authentication;
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

    public void updateUserAuth(String email, String password, AuthenticationCallbackResult.UpdateAuthentication listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!email.equals("") && !password.equals(""))
            updateUserEmailAndPassword(user, email, password, listener);
        else if (!email.equals(""))
            updateUserEmail(user, email, listener);
        else if (!password.equals(""))
            updateUserPassword(user, password, listener);
        else
            listener.onUpdateSuccessful();
    }

    private void updateUserEmailAndPassword(FirebaseUser user, String email, String password, AuthenticationCallbackResult.UpdateAuthentication listener) {
        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> emailTask) {
                if (emailTask.isSuccessful())
                    updateUserPassword(user, password, listener);
                else
                    listener.onUpdateFailure();
            }
        });
    }

    private void updateUserEmail(FirebaseUser user, String email, AuthenticationCallbackResult.UpdateAuthentication listener) {
        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> emailTask) {
                if (emailTask.isSuccessful())
                    listener.onUpdateSuccessful();
                else
                    listener.onUpdateFailure();
            }
        });
    }

    private void updateUserPassword(FirebaseUser user, String password, AuthenticationCallbackResult.UpdateAuthentication listener) {
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> passwordTask) {
                if (passwordTask.isSuccessful())
                    listener.onUpdateSuccessful();
                else
                    listener.onUpdateFailure();
            }
        });
    }

    public interface FindUserListenerResult {
        void onUserFound(User user);
    }
}
