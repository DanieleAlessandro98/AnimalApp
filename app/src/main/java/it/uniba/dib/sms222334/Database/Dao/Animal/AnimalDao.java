package it.uniba.dib.sms222334.Database.Dao.Animal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Database.Dao.User.PrivateDao;
import it.uniba.dib.sms222334.Database.Dao.User.PublicAuthorityDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.UserRole;

public class AnimalDao {
    private final String TAG="AnimalDao";
    final public static CollectionReference collectionAnimal = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);

    final public static StorageReference animalStorage = FirebaseStorage.getInstance().getReference().child("images/profiles/animals/");


    public void createAnimal(Animal animal ,AnimalCallbacks.creationCallback callback){
        Map<String, Object> new_animal = new HashMap<>();
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_NAME, animal.getName());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE, new Timestamp(animal.getBirthDate()));
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP, animal.getMicrochip());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_IMAGES, new ArrayList<>());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_RACE, animal.getRace());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_OWNER, SessionManager.getInstance().getCurrentUser().getFirebaseID());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_PHOTO, "");
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_SPECIES, animal.getSpecies());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_STATE, animal.getState().ordinal());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_VIDEOS, new ArrayList<>());

        collectionAnimal.add(new_animal)
                .addOnSuccessListener(documentReference -> {
                    animal.setFirebaseID(documentReference.getId());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    animal.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();



                    UploadTask uploadTask = animalStorage.child(documentReference.getId()+".jpg").putBytes(data);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {

                    }).addOnFailureListener(exception -> {
                        // Si è verificato un errore durante il caricamento dell'immagine
                    });

                    if(SessionManager.getInstance().getCurrentUser().getRole() == UserRole.PRIVATE){
                        Private pvt=((Private)SessionManager.getInstance().getCurrentUser());
                        pvt.addAnimal(animal);

                        PrivateDao privateDao =new PrivateDao();

                        privateDao.updatePrivate(pvt, new UserCallback.UserUpdateCallback() {
                            @Override
                            public void notifyUpdateSuccesfull() {
                                callback.createdSuccesfully();
                            }

                            @Override
                            public void notifyUpdateFailed() {
                                callback.failedCreation();
                                pvt.removeAnimal(animal);
                            }
                        });
                    } else if (SessionManager.getInstance().getCurrentUser().getRole()== UserRole.PUBLIC_AUTHORITY) {
                        PublicAuthority pla=((PublicAuthority)SessionManager.getInstance().getCurrentUser());
                        pla.addAnimal(animal);

                        PublicAuthorityDao authorityDao =new PublicAuthorityDao();

                        authorityDao.updatePublicAuthority(pla, new UserCallback.UserUpdateCallback() {
                            @Override
                            public void notifyUpdateSuccesfull() {
                                callback.createdSuccesfully();
                            }

                            @Override
                            public void notifyUpdateFailed() {
                                callback.failedCreation();
                                pla.removeAnimal(animal);
                            }
                        });
                    }

                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        callback.failedCreation();
                    }
                });
    }
    
    public void editAnimal(Animal animal, AnimalCallbacks.updateCallback callback,boolean profilePictureFlag) {

        Map<String, Object> editedAnimal = new HashMap<>();
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_NAME, animal.getName());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE, new Timestamp(animal.getBirthDate()));
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP, animal.getMicrochip());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_IMAGES, new ArrayList<>());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_RACE, animal.getRace());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_OWNER, animal.getOwnerReference());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_PHOTO, "");
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_SPECIES, animal.getSpecies());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_STATE, animal.getState().ordinal());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_VIDEOS, new ArrayList<>());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        animal.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();



        UploadTask uploadTask = animalStorage.child(animal.getFirebaseID()+".jpg").putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {

        }).addOnFailureListener(exception -> {
            // Si è verificato un errore durante il caricamento dell'immagine
        });

        collectionAnimal.document(animal.getFirebaseID())
                .update(editedAnimal)
                .addOnSuccessListener(command -> {
                    ((Owner)SessionManager
                            .getInstance()
                            .getCurrentUser())
                            .updateAnimal(animal,profilePictureFlag);
                    callback.updatedSuccesfully();
                })
                .addOnFailureListener(command -> {
                    callback.failedUpdate();
                });

        if(animal.getOwnerReference().compareTo(SessionManager.getInstance().getCurrentUser().getFirebaseID())!=0){
            //TODO implementare la rimozione e aggiunta dell'animale sul proprietario
        }
    }
    public void getAnimalByReference(DocumentReference animalRef, final String resultPrivateReference, final DatabaseCallbackResult<Animal> listener) {
        animalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Animal resultAnimal = findAnimal(document, resultPrivateReference);

                    //TODO togliere da qui
                    final long MAX_SIZE = 1024 * 1024; // dimensione massima dell'immagine in byte

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("images/profiles/animals/"+document.getId()+".jpg");

                    storageRef.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Converti i dati dell'immagine in un oggetto Bitmap
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            // Utilizza il bitmap come desideri, ad esempio, impostalo in un'ImageView
                            resultAnimal.setPhoto(bitmap);
                            listener.onDataRetrieved(resultAnimal);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            listener.onDataRetrieved(resultAnimal);
                        }
                    });

                    findAnimalImages(document, resultAnimal);
                    findAnimalVideos(document, resultAnimal);
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

    public void deleteAnimal(Animal animal, AnimalCallbacks.eliminationCallback callback) {

        User user=SessionManager.getInstance().getCurrentUser();

        ((Owner) user).removeAnimal(animal);

        switch (user.getRole()){
            case PRIVATE:
                PrivateDao privateDao= new PrivateDao();
                privateDao.updatePrivate((Private)user, new UserCallback.UserUpdateCallback() {
                    @Override
                    public void notifyUpdateSuccesfull() {

                        final String firebaseID=animal.getFirebaseID();

                        collectionAnimal.document(firebaseID).delete();

                        animalStorage.child(firebaseID+".jpg").delete().addOnSuccessListener(command ->
                                {
                                    callback.eliminatedSuccesfully();
                                }
                        );

                    }

                    @Override
                    public void notifyUpdateFailed() {
                        callback.failedElimination();
                        ((Private) user).addAnimal(animal);
                    }
                });
                break;
            case PUBLIC_AUTHORITY:
                PublicAuthorityDao publicAuthorityDao= new PublicAuthorityDao();
                publicAuthorityDao.updatePublicAuthority((PublicAuthority) user, new UserCallback.UserUpdateCallback() {
                    @Override
                    public void notifyUpdateSuccesfull() {
                        final String firebaseID=animal.getFirebaseID();

                        collectionAnimal.document(firebaseID).delete();

                        collectionAnimal.document(firebaseID).delete();

                        animalStorage.child(firebaseID+".jpg").delete().addOnSuccessListener(command ->
                                {
                                    callback.eliminatedSuccesfully();
                                }
                        );

                    }

                    @Override
                    public void notifyUpdateFailed() {
                        callback.failedElimination();
                        ((PublicAuthority) user).addAnimal(animal);
                    }
                });
                break;
        }
    }
}
