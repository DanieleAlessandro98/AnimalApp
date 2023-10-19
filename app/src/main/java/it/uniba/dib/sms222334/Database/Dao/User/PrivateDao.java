package it.uniba.dib.sms222334.Database.Dao.User;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.ContentProvider.OwnerSuggestContentProvider;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Utils.Media;

public final class PrivateDao {
    private final String TAG="PrivateDao";
    public static final CollectionReference collectionPrivate = FirebaseFirestore.getInstance().collection(AnimalAppDB.Private.TABLE_NAME);

    public void getPrivateByEmail(String email, final DatabaseCallbackResult<Private> listener) {
        collectionPrivate.whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL, email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                findPrivate(document, new PrivateCallback() {
                                    @Override
                                    public void onPrivateFound(Private privateObject) {
                                        loadPrivateAnimals(document, privateObject);
                                        listener.onDataRetrieved(privateObject);
                                    }

                                    @Override
                                    public void onPrivateFindFailed(Exception exception) {

                                    }
                                });
                            } else {
                                listener.onDataNotFound();
                            }
                        } else {
                            listener.onDataQueryError(task.getException());
                        }
                    }
                });
    }


    public void getPrivatesByEmail(String emailText, final DatabaseCallbackResult<Owner> listener) {
        Log.d(TAG, emailText);

        collectionPrivate.whereGreaterThanOrEqualTo("email", emailText)
                .whereLessThanOrEqualTo("email", emailText + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Owner> resultList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            findPrivate(document, new PrivateCallback() {
                                @Override
                                public void onPrivateFound(Private privateObject) {
                                    resultList.add(privateObject);

                                    Log.d(TAG, privateObject.getFirebaseID() + " found!");
                                }

                                @Override
                                public void onPrivateFindFailed(Exception exception) {

                                }
                            });
                        }

                        listener.onDataRetrieved(resultList);
                    }
                });
    }

    public void findPrivate(DocumentSnapshot document, PrivateCallback callback) {
        MediaDao mediaDao = new MediaDao();
        mediaDao.downloadPhoto(document.getString(AnimalAppDB.Private.COLUMN_NAME_PHOTO), new MediaDao.PhotoDownloadListener() {
            @Override
            public void onPhotoDownloaded(Bitmap bitmap) {
                Private.Builder private_requested_builder = Private.Builder.
                        create(
                                document.getId(),
                                document.getString(AnimalAppDB.Private.COLUMN_NAME_NAME),
                                document.getString(AnimalAppDB.Private.COLUMN_NAME_EMAIL))
                        .setPassword(document.getString(AnimalAppDB.Private.COLUMN_NAME_PASSWORD))
                        .setPhone(document.getLong(AnimalAppDB.Private.COLUMN_NAME_PHONE_NUMBER))
                        .setPhoto(bitmap)
                        .setSurname(document.getString(AnimalAppDB.Private.COLUMN_NAME_SURNAME))
                        .setBirthDate(document.getDate(AnimalAppDB.Private.COLUMN_NAME_BIRTH_DATE))
                        .setTaxIdCode(document.getString(AnimalAppDB.Private.COLUMN_NAME_TAX_ID));

                Private resultPrivate = private_requested_builder.build();
                callback.onPrivateFound(resultPrivate);
            }

            @Override
            public void onPhotoDownloadFailed(Exception exception) {
                callback.onPrivateFindFailed(exception);
            }
        });
    }

    public void loadPrivateAnimals(final DocumentSnapshot document, Private resultPrivate) {
        AnimalDao animalDao = new AnimalDao();
        List<DocumentReference> animalRefs = (List<DocumentReference>) document.get(AnimalAppDB.Private.COLUMN_NAME_ANIMALS);

        for (DocumentReference animalRef : animalRefs) {
            DatabaseCallbackResult<Animal> animalListener = new DatabaseCallbackResult<Animal>() {
                @Override
                public void onDataRetrieved(Animal result) {
                    resultPrivate.addAnimal(result);
                }

                @Override
                public void onDataRetrieved(ArrayList<Animal> results) {

                }

                @Override
                public void onDataNotFound() {
                    Log.d(TAG, "non esiste");
                }

                @Override
                public void onDataQueryError(Exception e) {
                    Log.w(TAG, "errore query.");
                }
            };

            animalDao.getAnimalByReference(animalRef, resultPrivate.getFirebaseID(), animalListener);
        }
    }


    public void createPrivate(Private Private, final UserCallback.UserRegisterCallback callback){
        List<DocumentReference> dr= new ArrayList<>();

        Map<String, Object> new_private = new HashMap<>();
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_NAME, Private.getName());
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_SURNAME, Private.getSurname());
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_ANIMALS, dr);
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_BIRTH_DATE, new Timestamp(Private.getBirthDate()));
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_EMAIL, Private.getEmail());
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_PASSWORD, Private.getPassword());
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_PHONE_NUMBER, Private.getPhone());
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_PHOTO, "/images/profiles/users/default.jpg");
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_ROLE, Private.getRole());
        new_private.put(AnimalAppDB.Private.COLUMN_NAME_TAX_ID, Private.getTaxIDCode());

        collectionPrivate.add(new_private)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        callback.onRegisterSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        callback.onRegisterFail();
                    }
                });
    }

    public void deletePrivate(Private Private){
        collectionPrivate.document(Private.getFirebaseID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void updatePrivate(Private updatePrivate,UserCallback.UserUpdateCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        List<DocumentReference> dr= new ArrayList<>();

        for(Animal a: updatePrivate.getAnimalList()){
            Log.d(TAG,a.getName());
            DocumentReference documentReference = AnimalDao.collectionAnimal.document(a.getFirebaseID());
            dr.add(documentReference);
        }

        user.updateEmail(updatePrivate.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {}
                });

        Map<String, Object> newPrivateData = new HashMap<>();
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_NAME, updatePrivate.getName());
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_SURNAME, updatePrivate.getSurname());
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_BIRTH_DATE, new Timestamp(updatePrivate.getBirthDate()));
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_ANIMALS, dr);
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_EMAIL, updatePrivate.getEmail());
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_PASSWORD, updatePrivate.getPassword());
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_PHONE_NUMBER, updatePrivate.getPhone());
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_PHOTO, Media.PROFILE_PHOTO_PATH + updatePrivate.getFirebaseID() + Media.PROFILE_PHOTO_EXTENSION);
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_TAX_ID, updatePrivate.getTaxIDCode());

        collectionPrivate.document(updatePrivate.getFirebaseID())
                .update(newPrivateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.notifyUpdateSuccesfull();
                        Log.d(TAG, "update fatto");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.notifyUpdateFailed();
                        Log.d(TAG, "errore update");
                    }
                });
    }

    public interface PrivateCallback {
        void onPrivateFound(Private privateObject);
        void onPrivateFindFailed(Exception exception);
    }

}
