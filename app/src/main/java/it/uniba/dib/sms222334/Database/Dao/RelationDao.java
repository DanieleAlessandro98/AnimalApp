package it.uniba.dib.sms222334.Database.Dao;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Pathology;
import it.uniba.dib.sms222334.Models.Relation;

public class RelationDao {
    private final String TAG="RelationDao";
    final private CollectionReference collectionRelation = FirebaseFirestore.getInstance().collection(AnimalAppDB.Relation.TABLE_NAME);
    final private CollectionReference collectionAnimalRelation = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);
    private boolean valueReturn = true;
    public boolean createRelation(Relation.relationType tipo,String MyIdAnimal,String TheyIdAnimal){
        //TODO cambiare l'id da stringa in Reference dopo che il problema di visualizzazione è risolto
        Map<String,String> newRelation = new HashMap<>();
        newRelation.put("idAnimal1",MyIdAnimal);
        newRelation.put("idAnimal2",TheyIdAnimal);
        newRelation.put("Relation",tipo.toString());

        collectionRelation.add(newRelation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"Creazione avenuta");
                valueReturn = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Creazione fallita");
                valueReturn = false;
            }
        });

        return valueReturn;
    }
    public boolean deleteRelation(String idAnimal1,String idAnimal2){
        collectionRelation
                .whereEqualTo("idAnimal1",idAnimal1)
                .whereEqualTo("idAnimal2",idAnimal2)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            collectionRelation.document(document.getId())
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            valueReturn = true;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                            valueReturn = false;
                                        }
                                    });
                        }
                    }
                });
        return valueReturn;
    }

    public interface OnRelationListener {
        void onRelationListener(List <Animal> animalList);
    }
    //TODO capire come sistemare questo enum di stato dell'animale
    public void getListAnimalDao(String ownerID, final OnRelationListener listener){
        collectionAnimalRelation.whereNotEqualTo("ownerID", ownerID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Animal> animalList = new ArrayList<>();
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Timestamp timestamp = document.getTimestamp("birthdate");
                            if (timestamp != null) {
                                Date birthDate = timestamp.toDate();

                                Animal getAnimal = Animal.Builder.create(document.getId(),Animal.stateList.ADOPTED)
                                        .setSpecies(document.getString("species"))
                                        .setBirthDate(birthDate)
                                        .setName(document.getString("name"))
                                        .setOwner(document.getString("ownerID"))
                                        .build();
                                animalList.add(getAnimal);
                                System.out.println("Data  "+getAnimal.getBirthDate());
                            } else {
                                System.out.println("Il campo 'birthdate' è nullo o non esiste nel documento.");
                            }
                        }
                        listener.onRelationListener(animalList);
                    } else {
                        Log.w("W","Nessun dato trovato");
                        listener.onRelationListener(new ArrayList<>());
                    }
                } else {
                    Log.w(TAG,"ricerca fallita");
                    listener.onRelationListener(new ArrayList<>());
                }
            }
        });
    }
}
