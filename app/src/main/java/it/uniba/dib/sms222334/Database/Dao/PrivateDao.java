package it.uniba.dib.sms222334.Database.Dao;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Utils.Media;

public final class PrivateDao {
    private final String TAG="PrivateDao";
    final private CollectionReference collectionPrivate = FirebaseFirestore.getInstance().collection(AnimalAppDB.Private.TABLE_NAME);

    /**
     public void getPrivateByEmail(String email, final DatabaseCallbackResult<Private> listener) {
     collectionPrivate.whereEqualTo(AnimalAppDB.Private.COLUMN_NAME_EMAIL,email).get()
     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
    if (task.isSuccessful()) {
    QuerySnapshot querySnapshot = task.getResult();
    if (querySnapshot != null && !querySnapshot.isEmpty()) {
    DocumentSnapshot document = querySnapshot.getDocuments().get(0);

    Private resultPrivate = findPrivate(document);
    findPrivateAnimals(document, resultPrivate);

    listener.onDataRetrieved(resultPrivate);
    } else {
    listener.onDataNotFound();
    }
    } else {
    listener.onDataQueryError(task.getException());
    }
    }
    });
     }
     */

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
                        .setPhone(document.getLong(AnimalAppDB.Private.COLUMN_NAME_PHONE_NUMBER).intValue())
                        .setPhoto(bitmap)
                        .setSurname(document.getString(AnimalAppDB.Private.COLUMN_NAME_SURNAME))
                        .setBirthDate(document.getDate(AnimalAppDB.Private.COLUMN_NAME_BIRTH_DATE))
                        .setTaxIdCode(document.getString(AnimalAppDB.Private.COLUMN_NAME_TAX_ID));

                Private resultPrivate = private_requested_builder.build();
                findPrivateAnimals(document, resultPrivate);

                callback.onPrivateFound(resultPrivate);
            }

            @Override
            public void onPhotoDownloadFailed(Exception exception) {
                callback.onPrivateFindFailed(exception);
            }
        });
    }

    private void findPrivateAnimals(final DocumentSnapshot document, Owner resultPrivate) {
        AnimalDao animalDao = new AnimalDao();
        List<DocumentReference> animalRefs = (List<DocumentReference>) document.get(AnimalAppDB.Private.COLUMN_NAME_ANIMALS);

        for (DocumentReference animalRef : animalRefs) {
            DatabaseCallbackResult<Animal> animalListener = new DatabaseCallbackResult<Animal>() {
                @Override
                public void onDataRetrieved(Animal result) {
                    resultPrivate.addAnimal(result);

                    String log = "";
                    log += result.getName() + " ";
                    log += result.getAge() + " ";
                    log += result.getState() + " ";
                    log += result.getSpecies() + " ";
                    log += result.getRace() + " ";
                    log += result.getPhoto() + " ";
                    log += result.getMicrochip() + " ";

                    Log.d("resultAnimalTest", log);
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

            animalDao.getAnimalByReference(animalRef, resultPrivate, animalListener);
        }
    }

    public void createPrivate(Private Private){
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
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

    public void updatePrivate(Private updatePrivate) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(updatePrivate.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {}
                });

        Map<String, Object> newPrivateData = new HashMap<>();
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_NAME, updatePrivate.getName());
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_SURNAME, updatePrivate.getSurname());
        newPrivateData.put(AnimalAppDB.Private.COLUMN_NAME_BIRTH_DATE, new Timestamp(updatePrivate.getBirthDate()));
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
                        Log.d(TAG, "update fatto");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "errore update");
                    }
                });
    }

    public interface PrivateCallback {
        void onPrivateFound(Private privateObject);
        void onPrivateFindFailed(Exception exception);
    }

}
