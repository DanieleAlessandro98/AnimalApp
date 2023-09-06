package it.uniba.dib.sms222334.Database.Dao.User;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Veterinarian;

public class VeterinarianDao {
    private final String TAG = "VeterinarianDao";
    final private CollectionReference collectionVeterinarian = FirebaseFirestore.getInstance().collection(AnimalAppDB.Veterinarian.TABLE_NAME);

    public Veterinarian findVeterinarian(DocumentSnapshot document) {
        Veterinarian.Builder veterinarian_requested_builder = Veterinarian.Builder.
                create(
                        document.getId(),
                        document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_COMPANY_NAME),
                        document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_EMAIL))  //TODO: document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_PHOTO))
                .setLegalSite(document.getGeoPoint(AnimalAppDB.Veterinarian.COLUMN_NAME_SITE))
                .setPassword(document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_PASSWORD))
                .setPhone(document.getLong(AnimalAppDB.Veterinarian.COLUMN_NAME_PHONE_NUMBER).intValue())
                //.setLatitude(document.getDouble(AnimalAppDB.Veterinarian.COLUMN_NAME_BIRTH_DATE)) // TODO: Langitude
                //.setLongitude(document.getDouble(AnimalAppDB.Veterinarian.COLUMN_NAME_BIRTH_DATE)) // TODO: Longitude
                ;

        return veterinarian_requested_builder.build();
    }

    // TODO: createVeterinarian metodo
    //TODO: Creare Autentication

    //Callback per far tornare dalla pagina di registrazione a quella di login (DA INSERIRE NEL METODO createVeterinarian)
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
