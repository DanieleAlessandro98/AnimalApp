package it.uniba.dib.sms222334.Database.Dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.AnimalStates;

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
    public void deleteRelation(String idAnimal1, String idAnimal2){
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
                                int SpeciesInteger = document.getLong(AnimalAppDB.Animal.COLUMN_NAME_SPECIES).intValue();
                                AnimalSpecies species = AnimalSpecies.values()[SpeciesInteger];
                                Animal getAnimal = Animal.Builder.create(document.getId(), AnimalStates.ADOPTED)
                                        .setSpecies(species)
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
                        listener.onGetAnimalListener(animalList);
                    } else {
                        Log.w("W","Nessun dato trovato");
                        listener.onGetAnimalListener(new ArrayList<>());
                    }
                } else {
                    Log.w(TAG,"ricerca fallita");
                    listener.onGetAnimalListener(new ArrayList<>());
                }
            }
        });
    }

    public ArrayList <Relation> listRelation;
    public void getRelation(String ownerID, String idAnimal, final OnRelationAnimalListener listener) {
        listRelation = new ArrayList<>();
        getListAnimalDao(ownerID, new OnRelationListener() {
            @Override
            public void onGetAnimalListener(List<Animal> animalGetList) {
                // Creare due query separate per "idAnimal1" e "idAnimal2"
                Query query1 = collectionRelation.whereEqualTo("idAnimal1", idAnimal);
                Query query2 = collectionRelation.whereEqualTo("idAnimal2", idAnimal);

                // Unire i risultati delle due query
                Task<QuerySnapshot> query1Result = query1.get();
                Task<QuerySnapshot> query2Result = query2.get();

                Tasks.whenAllSuccess(query1Result, query2Result)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> results) {
                                List<QuerySnapshot> snapshots = new ArrayList<>();
                                for (Object result : results) {
                                    snapshots.add((QuerySnapshot) result);
                                }

                                for (QuerySnapshot snapshot : snapshots) {
                                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                                        // Processa i risultati come hai fatto in precedenza
                                        final Relation.relationType[] relation = new Relation.relationType[1];
                                        relation[0] = Relation.relationType.valueOf(document.getString("Relation"));
                                        String idAnimal1 = document.getString("idAnimal1");
                                        String idAnimal2 = document.getString("idAnimal2");
                                        String searchAnimalId;

                                        if (idAnimal.equals(idAnimal1)){
                                            searchAnimalId = idAnimal2;
                                        }else{
                                            searchAnimalId = idAnimal1;
                                        }

                                        getAnimalClass(searchAnimalId, new OnRelationClassAnimalListener() {
                                            @Override
                                            public void onRelationClassAnimalListener(Animal animalList) {
                                                assert searchAnimalId != null;
                                                if (searchAnimalId.equals(idAnimal1)) {
                                                    listRelation.add(Relation.Builder.create(idAnimal2, relation[0], animalList).build());
                                                }else{
                                                    listRelation.add(Relation.Builder.create(idAnimal1, relation[0], animalList).build());
                                                }
                                                listener.onRelationAnimalListener(listRelation, animalGetList);
                                            }
                                        });
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void getAnimalClass(String idAnimal, final OnRelationClassAnimalListener listener){
        collectionAnimalRelation.document(idAnimal).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Timestamp timestamp = document.getTimestamp("birthdate");
                        if (timestamp != null) {
                            Date birthDate = timestamp.toDate();
                            int SpeciesInteger = document.getLong(AnimalAppDB.Animal.COLUMN_NAME_SPECIES).intValue();
                            //TODO da capire come sistemare lo stato dell'animale al suo tipo
                            AnimalSpecies species = AnimalSpecies.values()[SpeciesInteger];
                            Animal getAnimal = Animal.Builder.create(document.getId(),AnimalStates.ADOPTED)
                                .setSpecies(species)
                                .setBirthDate(birthDate)
                                .setName(document.getString("name"))
                                .setOwner(document.getString("ownerID"))
                                .build();
                            listener.onRelationClassAnimalListener(getAnimal);
                        }
                    }else{
                        Log.d(TAG, "Il documento non esiste.");
                    }
                }else {
                    Log.w(TAG, "Errore nel recupero del documento.", task.getException());
                }
            }
        });
    }

    public interface OnRelationListener {
        void onGetAnimalListener(List <Animal> animalList);
    }

    public interface OnRelationAnimalListener{
        void onRelationAnimalListener(ArrayList <Relation> relationList,List <Animal> animalList);
    }

    public interface OnRelationClassAnimalListener{
        void onRelationClassAnimalListener(Animal animalList);
    }
}