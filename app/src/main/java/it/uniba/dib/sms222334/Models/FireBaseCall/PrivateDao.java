package it.uniba.dib.sms222334.Models.FireBaseCall;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Activity.MainActivity;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Private;

public final class PrivateDao {
    private final String TAG="PrivateDao";
    final private CollectionReference collectionPrivate = FirebaseFirestore.getInstance().collection("Private");

    //TODO:
    // Gestione foto:
        // foto profilo cambiata in string (path). più comodo. così la visualizzazione se ne occupa solo la view senza scaricarla nel model con flusso di byte
        // la foto nel db non può essere vuota. altrimenti darà nullexception quando facciamo il get per prenderla. inserito immagine default togliendo context (così lo rendiamo indipendente dalla activty)

    //TODO:
    // Gestione problema addOnCompleteListener di Firestore
        // la chiamata collectionPrivate.whereEqualTo è asincrona, quindi il valore di requested_private non è ancora stato impostato quando viene restituito (grazie chatgpt). di conseguenza da nullexception se usi il valore di ritorno in un'altra classe (es MainActivity)
        // per risolvere ho usato i listener (non c'è più valore di ritorno, e il risultato lo imposti tramite chiamata al listener)

    //TODO:
    // Gestione lista animali
        // per prima cosa ho dovuto aggiungere costruttore ad Owner. altrimenti non inizializzavamo le liste
        // poi ho creato animaldao implementato il metodo per restituire gli animali del proprietario (ho dovuto usare listener)
        // infine ho suddiviso getPrivateByEmail() per renderlo più leggibile

    public void getPrivateByEmail(String email, final MainActivity.GetPrivateByEmailResult listener) {
        collectionPrivate.whereEqualTo("email",email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                Private resultPrivate = findPrivate(document);

                                ArrayList<Animal> animalList = findPrivateAnimals(document);
                                for (Animal animal : animalList) {
                                    resultPrivate.addAnimal(animal);
                                }

                                listener.onPrivateRetrieved(resultPrivate);
                            } else {
                                listener.onPrivateNotFound();
                            }
                        } else {
                            listener.onPrivateQueryError(task.getException());
                        }
                    }
                });
    }

    private Private findPrivate(DocumentSnapshot document) {
        Private.Builder private_requested_builder=Private.Builder.create(document.getString("name"),document.getString("surname"))
                .setPassword(document.getString("password"))
                .setEmail(document.getString("email"))
                .setDate(document.getDate("birthdate"))
                .setPhoneNumber(document.getLong("phone_number"))
                .setPhoto(document.getString("photo"))
                .setTaxIdCode(document.getString("tax_id_code"));

        Private resultPrivate = private_requested_builder.build();
        resultPrivate.setFirebaseID(document.getId());

        return resultPrivate;
    }

    private ArrayList<Animal> findPrivateAnimals(final DocumentSnapshot document) {
        AnimalDao animalDao = new AnimalDao();
        ArrayList<Animal> animalList = new ArrayList<>();

        List<DocumentReference> animalRefs = (List<DocumentReference>) document.get("animalList");

        for (DocumentReference animalRef : animalRefs) {
            GetAnimalByReferenceResult animalListener = new GetAnimalByReferenceResult() {
                @Override
                public void onAnimalRetrieved(Animal resultAnimal) {
                    animalList.add(resultAnimal);

                    String log = "";
                    log += resultAnimal.getName() + " ";
                    log += resultAnimal.getOwner() + " ";
                    log += resultAnimal.getAge() + " ";
                    log += resultAnimal.getState() + " ";
                    log += resultAnimal.getSpecies() + " ";
                    log += resultAnimal.getRace() + " ";
                    log += resultAnimal.getPhoto() + " ";
                    log += resultAnimal.getMicrochip() + " ";

                    Log.d("resultAnimalTest", log);
                }

                @Override
                public void onAnimalNotFound() {
                    Log.d(TAG, "non esiste");
                }

                @Override
                public void onAnimalQueryError(Exception e) {
                    Log.w(TAG, "errore query.");
                }
            };

            animalDao.getAnimalByReference(animalRef, animalListener);
        }

        return animalList;
    }

    public void createPrivate(Private Private){
        List<DocumentReference> dr= new ArrayList<>();

        Map<String, Object> new_private = new HashMap<>();
        new_private.put("name", Private.getName());
        new_private.put("surname", Private.getSurname());
        new_private.put("animalList", dr);
        new_private.put("birthdate", new Timestamp(Private.getDate()));
        new_private.put("email", Private.getEmail());
        new_private.put("password", Private.getPassword());
        new_private.put("phone_number", Private.getPhoneNumber());
        new_private.put("photo", "/images/profiles/users/default.jpg");
        new_private.put("role", Private.getRole());
        new_private.put("tax_id_code", Private.getTax_id_code());

        collectionPrivate.add(new_private)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void deletePrivate(Private Private){
        collectionPrivate.document(Private.getFirebaseID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void updatePrivate(Private updatePrivate) {
        //TODO:
        // l'aggiunta/rimozione animale di un privato non la facciamo qui. creiamo dei metodi a parte in seguito
            // qui gestiamo solo la modifica dei dati del profilo

        // TODO:
        //  la modifica della foto avviene solo a livello di database qui
            // quando andremo ad implementare la funzionalità "cambia foto profilo" ricordarsi di gestire l'update del file

        Map<String, Object> newPrivateData = new HashMap<>();
        newPrivateData.put("name", updatePrivate.getName());
        newPrivateData.put("surname", updatePrivate.getSurname());
        newPrivateData.put("birthdate", new Timestamp(updatePrivate.getDate()));
        newPrivateData.put("email", updatePrivate.getEmail());
        newPrivateData.put("password", updatePrivate.getPassword());
        newPrivateData.put("phone_number", updatePrivate.getPhoneNumber());
        newPrivateData.put("photo", updatePrivate.getPhoto());
        newPrivateData.put("tax_id_code", updatePrivate.getTax_id_code());

        collectionPrivate.document(updatePrivate.getFirebaseID())
                .update(newPrivateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "update fatto");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "errore update");
                    }
                });
    }

    // TODO:
    // Stessa cosa di GetPrivateByEmailResult. Per ora messa qui.
    public interface GetAnimalByReferenceResult {
        void onAnimalRetrieved(Animal resultPrivate);
        void onAnimalNotFound();
        void onAnimalQueryError(Exception e);
    }


}
