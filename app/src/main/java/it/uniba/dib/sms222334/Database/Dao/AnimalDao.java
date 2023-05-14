package it.uniba.dib.sms222334.Database.Dao;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;

public class AnimalDao {
    private final String TAG="AnimalDao";
    final private CollectionReference collectionPrivate = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);

    public void getAnimalByReference(DocumentReference animalRef, Owner resultPrivate, final DatabaseCallbackResult<Animal> listener) {
        animalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Animal.Builder animal_find = Animal.Builder.create(document.getLong(AnimalAppDB.Animal.COLUMN_NAME_STATE).intValue())
                            .setAge(document.getLong(AnimalAppDB.Animal.COLUMN_NAME_AGE).intValue())
                            .setMicrochip(document.getString(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP))
                            .setName(document.getString(AnimalAppDB.Animal.COLUMN_NAME_NAME))
                            .setPhoto(document.getString(AnimalAppDB.Animal.COLUMN_NAME_PHOTO))
                            .setRace(document.getString(AnimalAppDB.Animal.COLUMN_NAME_RACE))
                            .setSpecies(document.getString(AnimalAppDB.Animal.COLUMN_NAME_SPECIES))
                            .setOwner(resultPrivate);

                    Animal resultAnimal = animal_find.build();
                    resultAnimal.setFirebaseID(document.getId());

                    listener.onDataRetrieved(resultAnimal);
                } else {
                    listener.onDataNotFound();
                }
            } else {
                listener.onDataQueryError(task.getException());
            }
        });
    }

}
