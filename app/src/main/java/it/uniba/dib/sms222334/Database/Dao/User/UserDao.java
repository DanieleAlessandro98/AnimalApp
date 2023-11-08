package it.uniba.dib.sms222334.Database.Dao.User;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;

public class UserDao {
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
                                    privateDao.loadPrivateAnimals(document, resultPrivate, null);
                                    listener.onUserFound(resultPrivate);
                                }

                                @Override
                                public void onPrivateFindFailed(Exception exception) {
                                    findVeterinarianUser(db, userID, listener);
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

                            publicAuthorityDao.findPublicAuthority(document, new PublicAuthorityDao.PublicAuthorityCallback() {
                                @Override
                                public void onPublicAuthorityFound(PublicAuthority resultPublicAuthority) {
                                    publicAuthorityDao.loadPublicAuthorityAnimals(document, resultPublicAuthority, null);

                                    listener.onUserFound(resultPublicAuthority);
                                }

                                @Override
                                public void onPublicAuthorityFindFailed(Exception exception) {
                                    findVeterinarianUser(db, userID, listener);
                                }
                            });

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

    public void getVeterinariansAndPublicAuthorities(UserCallback.UserFindCallback listener) {
        Task<QuerySnapshot> veterinariansTask = VeterinarianDao.collectionVeterinarian.get();
        Task<QuerySnapshot> publicAuthoritiesTask = PublicAuthorityDao.collectionPublicAuthority.get();


        Tasks.whenAllSuccess(veterinariansTask, publicAuthoritiesTask)
                .addOnSuccessListener(v -> {
                    QuerySnapshot veterinariansSnapshot = veterinariansTask.getResult();
                    QuerySnapshot publicAuthoritiesSnapshot = publicAuthoritiesTask.getResult();

                    if (veterinariansSnapshot != null) {
                        for (QueryDocumentSnapshot document : veterinariansSnapshot) {
                            VeterinarianDao veterinarianDao=new VeterinarianDao();
                            veterinarianDao.findVeterinarian(document, new VeterinarianDao.VeterinarianCallback() {
                                @Override
                                public void onVeterinarianFound(Veterinarian resultVeterinarian) {
                                    veterinarianDao.loadVeterinarianVisits(resultVeterinarian);
                                    listener.onUserFound(resultVeterinarian);
                                }

                                @Override
                                public void onVeterinarianFindFailed(Exception exception) {
                                    listener.onUserNotFound(exception);
                                }
                            });
                        }
                    } else {
                        Log.w("W", "Veterinarians data is empty");
                    }

                    if (publicAuthoritiesSnapshot != null) {
                        for (QueryDocumentSnapshot document : publicAuthoritiesSnapshot) {
                            PublicAuthorityDao publicAuthorityDao=new PublicAuthorityDao();
                            publicAuthorityDao.findPublicAuthority(document, new PublicAuthorityDao.PublicAuthorityCallback() {
                                @Override
                                public void onPublicAuthorityFound(PublicAuthority resultPublicAuthority) {
                                    publicAuthorityDao.loadPublicAuthorityAnimals(document,resultPublicAuthority,null);
                                    listener.onUserFound(resultPublicAuthority);

                                    if(publicAuthoritiesSnapshot.getDocuments().indexOf(document) == (publicAuthoritiesSnapshot.size()-1))
                                        listener.onLastUserFound();
                                }

                                @Override
                                public void onPublicAuthorityFindFailed(Exception exception) {
                                    listener.onUserNotFound(exception);
                                }
                            });
                        }
                    }

                })
                .addOnFailureListener(listener::onUserNotFound);
    }
}
