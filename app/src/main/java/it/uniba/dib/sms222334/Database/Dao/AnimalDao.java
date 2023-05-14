package it.uniba.dib.sms222334.Database.Dao;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;

public class AnimalDao {
    private final String TAG="AnimalDao";
    final private CollectionReference collectionPrivate = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);

    public void getAnimalByReference(DocumentReference animalRef, final Owner resultPrivate, final DatabaseCallbackResult<Animal> listener) {
        animalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Animal resultAnimal = findAnimal(document, resultPrivate);
                    findAnimalImages(document, resultAnimal);
                    findAnimalVideos(document, resultAnimal);

                    listener.onDataRetrieved(resultAnimal);
                } else {
                    listener.onDataNotFound();
                }
            } else {
                listener.onDataQueryError(task.getException());
            }
        });
    }

    private void findAnimalImages(DocumentSnapshot document, Animal animal) {
        List<String> images = (List<String>) document.get(AnimalAppDB.Animal.COLUMN_NAME_IMAGES);
        for (String image : images) {
            animal.addImage(image);
        }
    }

    private void findAnimalVideos(DocumentSnapshot document, Animal animal) {
        List<String> videos = (List<String>) document.get(AnimalAppDB.Animal.COLUMN_NAME_VIDEOS);
        for (String video : videos) {
            animal.addVideo(video);
        }
    }

    private Animal findAnimal(DocumentSnapshot document, final Owner resultPrivate) {
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

        return resultAnimal;
    }

}
