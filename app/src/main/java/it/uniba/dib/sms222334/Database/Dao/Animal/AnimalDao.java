package it.uniba.dib.sms222334.Database.Dao.Animal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

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
import it.uniba.dib.sms222334.Models.Photo;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Video;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.AnimalStates;
import it.uniba.dib.sms222334.Utils.ReportType;
import it.uniba.dib.sms222334.Utils.UserRole;

public class AnimalDao {
    private final String TAG="AnimalDao";
    final public static CollectionReference collectionAnimal = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);

    final public static StorageReference animalStorage = FirebaseStorage.getInstance().getReference().child("images/profiles/animals/");


    public void createAnimal(Animal animal ,AnimalCallbacks.creationCallback callback){ //TODO risolvere problema accesso
        Map<String, Object> new_animal = new HashMap<>();
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_NAME, animal.getName());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE, new Timestamp(animal.getBirthDate()));
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP, animal.getMicrochip());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_IMAGES, new ArrayList<>());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_RACE, animal.getRace());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_OWNER, SessionManager.getInstance().getCurrentUser().getFirebaseID());
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_PHOTO, "");
        new_animal.put(AnimalAppDB.Animal.COLUMN_NAME_SPECIES, animal.getSpecies().ordinal());
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

                        privateDao.updatePrivateAuthExcluded(pvt, new UserCallback.UserUpdateCallback() {
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

                        authorityDao.updatePublicAuthorityAuthExcluded(pla, new UserCallback.UserUpdateCallback() {
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
    
    public void editAnimal(Animal animal,String ownerEmail, AnimalCallbacks.updateCallback callback,boolean profilePictureFlag) {

        Map<String, Object> editedAnimal = new HashMap<>();
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_NAME, animal.getName());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE, new Timestamp(animal.getBirthDate()));
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP, animal.getMicrochip());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_IMAGES, new ArrayList<>());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_RACE, animal.getRace());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_OWNER, animal.getOwnerReference());
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_PHOTO, "");
        editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_SPECIES, animal.getSpecies().ordinal());
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

                    if(animal.getOwnerReference().compareTo(SessionManager.getInstance().getCurrentUser().getFirebaseID())!=0){
                        editAnimalOwners(animal,ownerEmail, new UserCallback.UserUpdateCallback() {
                            @Override
                            public void notifyUpdateSuccesfull() {
                                callback.updatedSuccesfully();
                            }

                            @Override
                            public void notifyUpdateFailed() {
                                editedAnimal.remove(AnimalAppDB.Animal.COLUMN_NAME_OWNER);
                                editedAnimal.put(AnimalAppDB.Animal.COLUMN_NAME_OWNER, animal.getOwnerReference());
                                collectionAnimal.document(animal.getFirebaseID()).update(editedAnimal);
                                callback.failedUpdate();
                            }
                        });
                    }
                    else{
                        ((Owner)SessionManager
                                .getInstance()
                                .getCurrentUser())
                                .updateAnimal(animal,profilePictureFlag);

                        callback.updatedSuccesfully();
                    }
                })
                .addOnFailureListener(command -> {
                    callback.failedUpdate();
                });
    }


    private void updateNewOwner(Animal animal,String ownerEmail,UserCallback.UserUpdateCallback callback){
        PrivateDao privateDao= new PrivateDao();
        privateDao.getPrivateByEmail(ownerEmail, new DatabaseCallbackResult<Private>() {
            @Override
            public void onDataRetrieved(Private result) {
                result.addAnimal(animal);

                Log.d("test passaggio proprietà","ora aggiungo il nuovo animale e aggiorno");
                privateDao.updatePrivateAuthExcluded(result, new UserCallback.UserUpdateCallback() {
                    @Override
                    public void notifyUpdateSuccesfull() {
                        Log.d("test passaggio proprietà","aggiorno con successo");
                        callback.notifyUpdateSuccesfull();
                    }

                    @Override
                    public void notifyUpdateFailed() {
                        Log.d("test passaggio proprietà","errore dell'aggiornamento nuovo proprietario");
                        result.removeAnimal(animal);
                        callback.notifyUpdateFailed();
                    }
                });
            }

            @Override
            public void onDataRetrieved(ArrayList<Private> results) {

            }

            @Override
            public void onDataNotFound() {
                Log.d("test passaggio proprietà","non ho trovato il privato ora trovo gli enti");

                PublicAuthorityDao publicAuthorityDao=new PublicAuthorityDao();

                publicAuthorityDao.getPublicAuthorityByEmail(ownerEmail, new DatabaseCallbackResult<PublicAuthority>() {
                    @Override
                    public void onDataRetrieved(PublicAuthority result) {
                        result.addAnimal(animal);

                        Log.d("test passaggio proprietà","ora aggiungo il nuovo animale e aggiorno");

                        publicAuthorityDao.updatePublicAuthorityAuthExcluded(result, new UserCallback.UserUpdateCallback() {
                            @Override
                            public void notifyUpdateSuccesfull() {
                                Log.d("test passaggio proprietà","aggiorno con successo");
                                callback.notifyUpdateSuccesfull();
                            }

                            @Override
                            public void notifyUpdateFailed() {
                                Log.d("test passaggio proprietà","errore dell'aggiornamento nuovo proprietario");
                                result.removeAnimal(animal);
                                callback.notifyUpdateFailed();
                            }
                        });
                    }

                    @Override
                    public void onDataRetrieved(ArrayList<PublicAuthority> results) {

                    }

                    @Override
                    public void onDataNotFound() {
                        callback.notifyUpdateFailed();
                    }

                    @Override
                    public void onDataQueryError(Exception e) {

                    }
                });
            }

            @Override
            public void onDataQueryError(Exception e) {

            }
        });
    }


    private void editAnimalOwners(Animal animal,String ownerEmail,UserCallback.UserUpdateCallback callback){
        Owner user= (Owner) SessionManager.getInstance().getCurrentUser();

        user.removeAnimal(animal);

        if(user instanceof Private){
            new PrivateDao().updatePrivate((Private) user, new UserCallback.UserUpdateCallback() {
                @Override
                public void notifyUpdateSuccesfull() {
                    updateNewOwner(animal,ownerEmail,callback);
                }

                @Override
                public void notifyUpdateFailed() {
                    user.addAnimal(animal);
                    callback.notifyUpdateFailed();
                }
            });
        }
        else if (user instanceof PublicAuthority){
            new PublicAuthorityDao().updatePublicAuthority((PublicAuthority) user, new UserCallback.UserUpdateCallback() {
                @Override
                public void notifyUpdateSuccesfull() {
                    updateNewOwner(animal,ownerEmail,callback);
                }

                @Override
                public void notifyUpdateFailed() {
                    user.addAnimal(animal);
                    callback.notifyUpdateFailed();
                }
            });
        }
    }

    public void getAnimalByReference(DocumentReference animalRef, final String resultPrivateReference, final DatabaseCallbackResult<Animal> listener) {
        animalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Animal resultAnimal = findAnimal(document, resultPrivateReference);

                    //TODO togliere da qui
                    final long MAX_SIZE = 4096 * 4096; // dimensione massima dell'immagine in byte

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("images/profiles/animals/"+document.getId()+".jpg");

                    storageRef.getBytes(MAX_SIZE).addOnSuccessListener(bytes -> {
                        // Converti i dati dell'immagine in un oggetto Bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        // Utilizza il bitmap come desideri, ad esempio, impostalo in un'ImageView
                        resultAnimal.setPhoto(bitmap);

                        findAnimalImages(document, resultAnimal);
                        findAnimalVideos(document, resultAnimal);

                        listener.onDataRetrieved(resultAnimal);
                    }).addOnFailureListener(exception -> {
                        Log.d(TAG,"Foto non caricata: "+exception.getMessage());

                        findAnimalImages(document, resultAnimal);
                        findAnimalVideos(document, resultAnimal);

                        listener.onDataRetrieved(resultAnimal);
                    });
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
        ArrayList<HashMap<String, Object>> images=((ArrayList<HashMap<String, Object>>) document.get(AnimalAppDB.Animal.COLUMN_NAME_IMAGES));

        for (HashMap<String, Object> image : images) {
            Log.d(TAG,"image: "+ image.get("photo")+"\ntimestamp: "+image.get("timestamp"));
            animal.addImage(new Photo((String) image.get("photo"),(Timestamp) image.get("timestamp")));
        }
    }

    private void findAnimalVideos(DocumentSnapshot document, Animal animal) {
        ArrayList<HashMap<String, Object>> videos=((ArrayList<HashMap<String, Object>>) document.get(AnimalAppDB.Animal.COLUMN_NAME_VIDEOS));

        for (HashMap<String, Object> video : videos) {
            Log.d(TAG,"video: "+ video.get("video")+"\ntimestamp: "+video.get("timestamp"));
            animal.addVideo(new Video((String) video.get("video"),(Timestamp) video.get("timestamp")));
        }
    }

    private Animal findAnimal(DocumentSnapshot document, final String resultPrivateRefernce) {
        int stateInteger = document.getLong(AnimalAppDB.Animal.COLUMN_NAME_STATE).intValue();
        AnimalStates state = AnimalStates.values()[stateInteger];

        int SpeciesInteger = document.getLong(AnimalAppDB.Animal.COLUMN_NAME_SPECIES).intValue();
        AnimalSpecies species = AnimalSpecies.values()[SpeciesInteger];

        Animal.Builder animal_find = Animal.Builder.create(document.getId(), state)
                .setBirthDate(document.getDate(AnimalAppDB.Animal.COLUMN_NAME_BIRTH_DATE))
                .setMicrochip(document.getString(AnimalAppDB.Animal.COLUMN_NAME_MICROCHIP))
                .setName(document.getString(AnimalAppDB.Animal.COLUMN_NAME_NAME))
                //.setPhoto(document.getString(AnimalAppDB.Animal.COLUMN_NAME_PHOTO))   TODO: Da finire
                .setRace(document.getString(AnimalAppDB.Animal.COLUMN_NAME_RACE))
                .setSpecies(species)
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

    public void updateState(String documentID, ReportType reportType) {
        Map<String, Object> animal = new HashMap<>();

        int stateValue = AnimalStates.NULL.ordinal();
        if (reportType == ReportType.LOST)
            stateValue = AnimalStates.LOST.ordinal();
        else if (reportType == ReportType.IN_DANGER)
            stateValue = AnimalStates.IN_DANGER.ordinal();

        if (documentID.equals("") || stateValue == 0)
            return;

        animal.put(AnimalAppDB.Animal.COLUMN_NAME_STATE, stateValue);

        collectionAnimal.document(documentID)
                .update(animal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public DocumentReference findAnimalRef(String animalID) {
        if (animalID.equals(""))
            return null;

        return collectionAnimal.document(animalID);
    }
}
