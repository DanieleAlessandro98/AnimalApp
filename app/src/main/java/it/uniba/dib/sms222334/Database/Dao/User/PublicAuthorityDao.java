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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Utils.Media;

public class PublicAuthorityDao {
    private final String TAG="PublicAuthorityDao";
    public static final CollectionReference collectionPublicAuthority = FirebaseFirestore.getInstance().collection(AnimalAppDB.PublicAuthority.TABLE_NAME);

    public void findPublicAuthority(DocumentSnapshot document, PublicAuthorityCallback callback) {
        MediaDao mediaDao= new MediaDao();

        mediaDao.downloadPhoto(document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_LOGO), new MediaDao.PhotoDownloadListener() {
            @Override
            public void onPhotoDownloaded(Bitmap bitmap) {
                PublicAuthority.Builder public_authority_requested_builder=PublicAuthority.Builder.
                        create(
                                document.getId(),
                                document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME),
                                document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL))
                        .setPhoto(bitmap)
                        .setPassword(document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD))
                        .setPhone(document.getLong(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER))
                        .setLegalSite(document.getGeoPoint(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE))
                        .setNBeds(document.getLong(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER).intValue());

                PublicAuthority resultPublicAuthority=public_authority_requested_builder.build();

                callback.onPublicAuthorityFound(resultPublicAuthority);

            }

            @Override
            public void onPhotoDownloadFailed(Exception exception) {
                callback.onPublicAuthorityFindFailed(exception);
            }
        });

    }

    public void loadPublicAuthorityAnimals(final DocumentSnapshot document, PublicAuthority resultPublicAuthority,@Nullable UserCallback.UserStateListener userCallback) {
        AnimalDao animalDao = new AnimalDao();
        List<DocumentReference> animalRefs = (List<DocumentReference>) document.get(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS);

        if(animalRefs.isEmpty() && userCallback!=null){
            userCallback.notifyItemLoaded();
        }

        for (DocumentReference animalRef : animalRefs) {
            DatabaseCallbackResult<Animal> animalListener = new DatabaseCallbackResult<Animal>() {
                @Override
                public void onDataRetrieved(Animal result) {
                    resultPublicAuthority.addAnimal(result);
                    Log.d("test passaggio proprietà","aggiungo un suo animale:ente");

                    if(((animalRefs.indexOf(animalRef)+1) == animalRefs.size()) && (userCallback!=null))
                        userCallback.notifyItemLoaded();
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

            animalDao.getAnimalByReference(animalRef, resultPublicAuthority.getFirebaseID(), animalListener);
        }
    }

    public void getPublicAuthoritiesByEmail(String emailText,final DatabaseCallbackResult<Owner> listener){
        Log.d(TAG,emailText);

        collectionPublicAuthority.whereGreaterThanOrEqualTo("email", emailText)
                .whereLessThanOrEqualTo("email", emailText + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            findPublicAuthority(document, new PublicAuthorityCallback() {
                                @Override
                                public void onPublicAuthorityFound(PublicAuthority resultPublicAuthority) {
                                    Log.d(TAG,resultPublicAuthority.getFirebaseID()+" found!");
                                    listener.onDataRetrieved(resultPublicAuthority);
                                }

                                @Override
                                public void onPublicAuthorityFindFailed(Exception exception) {

                                }
                            });
                        }
                    }
                });
    }

    public void createPublicAuthority(PublicAuthority PublicAuthority, final UserCallback.UserRegisterCallback callback){
        List<DocumentReference> dr= new ArrayList<>();

        Map<String, Object> new_authority = new HashMap<>();
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME, PublicAuthority.getName());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL, PublicAuthority.getEmail());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS, dr);
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER,0);
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD, PublicAuthority.getPassword());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_LOGO, "/images/profiles/users/default.jpg");
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER, PublicAuthority.getPhone());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE, PublicAuthority.getLocation());

        collectionPublicAuthority.add(new_authority)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    AuthenticationDao.fireAuth(PublicAuthority.getEmail(), PublicAuthority.getPassword(), documentReference, callback);
                    callback.onRegisterSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    callback.onRegisterFail();
                });
    }

    public void updatePublicAuthorityAuthExcluded(PublicAuthority updateAuthority, UserCallback.UserUpdateCallback callback) {
        List<DocumentReference> dr= new ArrayList<>();

        for(Animal a: updateAuthority.getAnimalList()){
            DocumentReference documentReference = AnimalDao.collectionAnimal.document(a.getFirebaseID());
            dr.add(documentReference);
        }

        Map<String, Object> newAuthorityData = new HashMap<>();
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME, updateAuthority.getName());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL, updateAuthority.getEmail());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS, dr);
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER,updateAuthority.getNBeds());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD, updateAuthority.getPassword());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_LOGO, updateAuthority.getPhotoPath());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER, updateAuthority.getPhone());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE, updateAuthority.getLocation());

        collectionPublicAuthority.document(updateAuthority.getFirebaseID())
                .update(newAuthorityData)
                .addOnSuccessListener(aVoid -> {
                    callback.notifyUpdateSuccesfull();
                    Log.d(TAG, "update fatto");
                })
                .addOnFailureListener(e -> {
                    callback.notifyUpdateFailed();
                    Log.d(TAG, "errore update");
                });
    }

    public void updatePublicAuthority(PublicAuthority updateAuthority, UserCallback.UserUpdateCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        List<DocumentReference> dr= new ArrayList<>();

        for(Animal a: updateAuthority.getAnimalList()){
            DocumentReference documentReference = AnimalDao.collectionAnimal.document(a.getFirebaseID());
            dr.add(documentReference);
        }

        user.updateEmail(updateAuthority.getEmail())
                .addOnCompleteListener(task -> {});

        Map<String, Object> newAuthorityData = new HashMap<>();
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME, updateAuthority.getName());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL, updateAuthority.getEmail());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS, dr);
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER,updateAuthority.getNBeds());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD, updateAuthority.getPassword());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER, updateAuthority.getPhone());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE, updateAuthority.getLocation());

        collectionPublicAuthority.document(updateAuthority.getFirebaseID())
                .update(newAuthorityData)
                .addOnSuccessListener(
                        aVoid -> {
                    callback.notifyUpdateSuccesfull();
                    Log.d(TAG, "update fatto");
                })
                .addOnFailureListener(
                        e -> {
                    callback.notifyUpdateFailed();
                    Log.d(TAG, "errore update");
                });
    }

    public void updatePhoto(String userID) {
        Map<String, Object> newPublicAuthorityData = new HashMap<>();
        newPublicAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_LOGO, Media.PROFILE_PHOTO_PATH + userID + Media.PROFILE_PHOTO_EXTENSION);
        collectionPublicAuthority.document(userID)
                .update(newPublicAuthorityData);
    }

    //this method load first all the animal and then notify the user data, DO NOT use this in UI Thread!!
    public void getPublicAuthorityByEmail(String email, DatabaseCallbackResult<PublicAuthority> listener) {
        collectionPublicAuthority.whereEqualTo(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(
                        task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                            findPublicAuthority(document, new PublicAuthorityCallback() {
                                @Override
                                public void onPublicAuthorityFound(PublicAuthority resultPublicAuthority) {
                                    Log.d("test passaggio proprietà","ho trovato l'ente ora carico i suoi animali");
                                    loadPublicAuthorityAnimals(document, resultPublicAuthority, new UserCallback.UserStateListener() {
                                        @Override
                                        public void notifyItemLoaded() {
                                            Log.d("test passaggio proprietà","ho caricato tutti i suoi animali:ente");
                                            listener.onDataRetrieved(resultPublicAuthority);
                                        }

                                        @Override
                                        public void notifyItemUpdated(int position) {

                                        }

                                        @Override
                                        public void notifyItemRemoved(int position) {

                                        }
                                    });
                                }

                                @Override
                                public void onPublicAuthorityFindFailed(Exception exception) {

                                }
                            });
                        } else {
                            listener.onDataNotFound();
                        }
                    } else {
                        listener.onDataQueryError(task.getException());
                    }
                });
    }

    public void deleteAuthority(PublicAuthority Authority){
        collectionPublicAuthority.document(Authority.getFirebaseID())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    public interface PublicAuthorityCallback {
        void onPublicAuthorityFound(PublicAuthority resultPublicAuthority);
        void onPublicAuthorityFindFailed(Exception exception);
    }
}
