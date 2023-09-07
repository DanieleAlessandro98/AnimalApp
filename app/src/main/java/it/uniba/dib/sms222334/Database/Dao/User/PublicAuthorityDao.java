package it.uniba.dib.sms222334.Database.Dao.User;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;

public class PublicAuthorityDao {
    private final String TAG="PublicAuthorityDao";
    public static final CollectionReference collectionPublicAuthority = FirebaseFirestore.getInstance().collection(AnimalAppDB.PublicAuthority.TABLE_NAME);

    public PublicAuthority findPublicAuthority(DocumentSnapshot document) {
        PublicAuthority.Builder public_authority_requested_builder=PublicAuthority.Builder.
                create(
                        document.getId(),
                        document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME),
                        document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL))  //TODO: document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHOTO))
                .setPassword(document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD))
                .setPhone(document.getLong(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER).intValue())
                .setLegalSite(document.getGeoPoint(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE))
                //.setLatitude(document.getDouble(AnimalAppDB.PublicAuthority.COLUMN_NAME_BIRTH_DATE)) // TODO: Langitude
                //.setLongitude(document.getDouble(AnimalAppDB.PublicAuthority.COLUMN_NAME_BIRTH_DATE)) // TODO: Longitude
                .setNBeds(document.getLong(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER).intValue());

        return public_authority_requested_builder.build();
    }

    public void loadPublicAuthorityAnimals(final DocumentSnapshot document, PublicAuthority resultPublicAuthority) {
        AnimalDao animalDao = new AnimalDao();
        List<DocumentReference> animalRefs = (List<DocumentReference>) document.get(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS);

        for (DocumentReference animalRef : animalRefs) {
            DatabaseCallbackResult<Animal> animalListener = new DatabaseCallbackResult<Animal>() {
                @Override
                public void onDataRetrieved(Animal result) {
                    resultPublicAuthority.addAnimal(result);
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
                        ArrayList<Owner> resultList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PublicAuthority authorityFound = findPublicAuthority(document);
                            resultList.add(authorityFound);

                            Log.d(TAG,authorityFound.getFirebaseID()+" found!");
                        }

                        listener.onDataRetrieved(resultList);
                    }
                });
    }

    public void createPublicAuthority(PublicAuthority publicAuthority, final UserCallback.UserRegisterCallback callback){

        List<DocumentReference> dr= new ArrayList<>();


        Map<String, Object> new_authority = new HashMap<>();
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME, publicAuthority.getName());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL, publicAuthority.getEmail());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS, dr);
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER,publicAuthority.getNBeds());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD, publicAuthority.getPassword());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_LOGO, "/images/profiles/users/default.jpg");
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER, publicAuthority.getPhone());
        new_authority.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE, publicAuthority.getLegalSite());

        collectionPublicAuthority.add(new_authority)
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

    public void updatePublicAuthority(PublicAuthority updateAuthority, UserCallback.UserUpdateCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        List<DocumentReference> dr= new ArrayList<>();

        for(Animal a: updateAuthority.getAnimalList()){
            DocumentReference documentReference = AnimalDao.collectionAnimal.document(a.getFirebaseID());
            dr.add(documentReference);
        }

        user.updateEmail(updateAuthority.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {}
                });

        Map<String, Object> newAuthorityData = new HashMap<>();
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME, updateAuthority.getName());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL, updateAuthority.getEmail());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS, dr);
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER,updateAuthority.getNBeds());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD, updateAuthority.getPassword());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_LOGO, "");
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER, updateAuthority.getPhone());
        newAuthorityData.put(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE, updateAuthority.getLegalSite());

        collectionPublicAuthority.document(updateAuthority.getFirebaseID())
                .update(newAuthorityData)
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

    public void getPublicAuthorityByEmail(String email, DatabaseCallbackResult<PublicAuthority> listener) {
        collectionPublicAuthority.whereEqualTo(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL,email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                PublicAuthority resultAuthority = findPublicAuthority(document);
                                loadPublicAuthorityAnimals(document, resultAuthority);

                                listener.onDataRetrieved(resultAuthority);
                            } else {
                                listener.onDataNotFound();
                            }
                        } else {
                            listener.onDataQueryError(task.getException());
                        }
                    }
                });
    }
}
