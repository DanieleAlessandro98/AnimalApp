package it.uniba.dib.sms222334.Models.FireBaseCall;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222334.Models.Animal;

public class AnimalDao {
    private final String TAG="AnimalDao";
    final private CollectionReference collectionPrivate = FirebaseFirestore.getInstance().collection("Animal");

    public void getAnimalByReference(DocumentReference animalRef, final PrivateDao.GetAnimalByReferenceResult listener) {
        animalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Animal.Builder animal_find = Animal.Builder.create(document.getLong("state").intValue())
                            .setAge(document.getLong("age").intValue())
                            .setMicrochip(document.getString("microchip"))
                            .setName(document.getString("name"))
                            .setPhoto(document.getString("photo"))
                            .setRace(document.getString("race"))
                            .setSpecies(document.getString("species"));

                    //TODO:
                    // Problema: come ci associo il proprietario?
                        // Ovvero il proprietario è in fase di construzione (dal metodo getPrivateByEmail che a sua volta ha richiamato questo metodo) per i suoi animali
                        // L'oggetto Private (o Owner) non è ancora stato creato, non posso associarlo

                    Animal resultAnimal = animal_find.build();
                    resultAnimal.setFirebaseID(document.getId());

                    listener.onAnimalRetrieved(resultAnimal);
                } else {
                    listener.onAnimalNotFound();
                }
            } else {
                listener.onAnimalQueryError(task.getException());
            }
        });
    }

}
