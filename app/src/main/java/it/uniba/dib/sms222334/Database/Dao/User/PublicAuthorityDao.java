package it.uniba.dib.sms222334.Database.Dao.User;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.PublicAuthority;

public class PublicAuthorityDao {
    private final String TAG="PublicAuthorityDao";
    final private CollectionReference collectionPublicAuthority = FirebaseFirestore.getInstance().collection(AnimalAppDB.PublicAuthority.TABLE_NAME);

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

    public void findPublicAuthorityAnimals(final DocumentSnapshot document, PublicAuthority resultPublicAuthority) {
        AnimalDao animalDao = new AnimalDao();
        List<DocumentReference> animalRefs = (List<DocumentReference>) document.get(AnimalAppDB.PublicAuthority.COLUMN_NAME_ANIMALS);

        for (DocumentReference animalRef : animalRefs) {
            DatabaseCallbackResult<Animal> animalListener = new DatabaseCallbackResult<Animal>() {
                @Override
                public void onDataRetrieved(Animal result) {
                    resultPublicAuthority.addAnimal(result);

                    String log = "";
                    log += result.getName() + " ";
                    log += result.getBirthDate() + " ";
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

            animalDao.getAnimalByReference(animalRef, resultPublicAuthority.getFirebaseID(), animalListener);
        }
    }

    // TODO: createPublicAuthority metodo
    //TODO: Creare Autentication

    //Callback per far tornare dalla pagina di registrazione a quella di login (DA INSERIRE NEL METODO createPublicAuthority)
    /*                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
    });*/


}
