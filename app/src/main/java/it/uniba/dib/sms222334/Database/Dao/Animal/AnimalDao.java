package it.uniba.dib.sms222334.Database.Dao.Animal;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;

public class AnimalDao {
    private final String TAG="AnimalDao";
    final private CollectionReference collectionAnimal = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);

    public void getAnimalByReference(DocumentReference animalRef, final String resultPrivateReference, final DatabaseCallbackResult<Animal> listener) {
        animalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Animal resultAnimal = findAnimal(document, resultPrivateReference);
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

    public void checkAnimalExist(String microchip, final AnimalCallbacks.alreadyExistCallBack listener) {
        collectionAnimal.whereEqualTo(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP,microchip).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            listener.alreadyExist();
                        } else {
                            listener.notExistYet();
                        }
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

    private Animal findAnimal(DocumentSnapshot document, final String resultPrivateRefernce) {
        int stateInteger = document.getLong(AnimalAppDB.Animal.COLUMN_NAME_STATE).intValue();
        Animal.stateList state = Animal.stateList.values()[stateInteger];

        Animal.Builder animal_find = Animal.Builder.create(document.getId(), state)
                .setBirthDate(document.getDate(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE))
                .setMicrochip(document.getString(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP))
                .setName(document.getString(AnimalAppDB.Animal.COLUMN_NAME_NAME))
                //.setPhoto(document.getString(AnimalAppDB.Animal.COLUMN_NAME_PHOTO))   TODO: Da finire
                .setRace(document.getString(AnimalAppDB.Animal.COLUMN_NAME_RACE))
                .setSpecies(document.getString(AnimalAppDB.Animal.COLUMN_NAME_SPECIES))
                .setOwner(resultPrivateRefernce);

        return animal_find.build();
    }

}
