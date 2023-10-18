package it.uniba.dib.sms222334.Database.Dao.User;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.User;

public class UerDao {
    public void findUser(String userID, AuthenticationDao.FindUserListenerResult listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        findPrivateUser(db, userID, listener);
    }

    private void findPrivateUser(FirebaseFirestore db, String userID, AuthenticationDao.FindUserListenerResult listener) {
        db.collection(AnimalAppDB.Private.TABLE_NAME).document(userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            PrivateDao privateDao = new PrivateDao();
                            privateDao.findPrivate(document, new PrivateDao.PrivateCallback() {
                                @Override
                                public void onPrivateFound(Private resultPrivate) {
                                    privateDao.loadPrivateAnimals(document, resultPrivate);
                                    listener.onUserFound(resultPrivate);
                                }

                                @Override
                                public void onPrivateFindFailed(Exception exception) {

                                }
                            });
                        } else {
                            findPublicAuthorityUser(db, userID, listener);
                        }
                    }
                });
    }

    private void findPublicAuthorityUser(FirebaseFirestore db, String userID, AuthenticationDao.FindUserListenerResult listener) {
        db.collection(AnimalAppDB.PublicAuthority.TABLE_NAME).document(userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            PublicAuthorityDao publicAuthorityDao = new PublicAuthorityDao();
                            PublicAuthority authorityFound = publicAuthorityDao.findPublicAuthority(document);
                            publicAuthorityDao.loadPublicAuthorityAnimals(document, authorityFound);

                            User user = authorityFound;
                            listener.onUserFound(user);
                        } else {
                            findVeterinarianUser(db, userID, listener);
                        }
                    }
                });
    }

    private void findVeterinarianUser(FirebaseFirestore db, String userID, AuthenticationDao.FindUserListenerResult listener) {
        db.collection(AnimalAppDB.Veterinarian.TABLE_NAME).document(userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            VeterinarianDao veterinarianDao = new VeterinarianDao();
                            User user = veterinarianDao.findVeterinarian(document);
                            listener.onUserFound(user);
                        } else {
                            listener.onUserFound(null);
                        }
                    }
                });
    }
}
