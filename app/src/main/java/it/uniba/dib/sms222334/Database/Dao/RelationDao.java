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
import java.util.Objects;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.AnimalStates;

public class RelationDao {
    private final String TAG="RelationDao";
    final private CollectionReference collectionRelation = FirebaseFirestore.getInstance().collection(AnimalAppDB.Relation.TABLE_NAME);
    final private CollectionReference collectionAnimalRelation = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);

    public void createRelation(Relation relation,String idMyAnimal,OnRelationCreated callBack){
        Map<String,String> newRelation = new HashMap<>();
        newRelation.put("idAnimal1",idMyAnimal);
        newRelation.put("idAnimal2",relation.getAnimal().getFirebaseID());
        newRelation.put("Relation",relation.getRelationType().toString());

        collectionRelation.add(newRelation).addOnSuccessListener(documentReference -> {
            Log.d(TAG,"the relation is create");
            relation.setFirebaseID(documentReference.getId());
            callBack.onRelationCreatedListener(relation);
        }).addOnFailureListener(e -> {
            Log.d(TAG,"Failure to create relation");
        });
    }
    public void deleteRelation(Relation relation){
        System.out.println("firebase id"+relation.getFirebaseID());
        collectionRelation.document(relation.getFirebaseID())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("I","eliminato la relazione di animali");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("W","eliminazione fallita");
                    }
                });
    }

    public void getListAnimalDao(String ownerID, final OnRelationListener listener){
        collectionAnimalRelation.whereNotEqualTo("ownerID", ownerID).get().addOnCompleteListener(task -> {
            List<Animal> animalList = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Timestamp timestamp = document.getTimestamp("birthdate");
                        if (timestamp != null) {
                            Date birthDate = timestamp.toDate();

                            int SpeciesInteger = Objects.requireNonNull(document.getLong(AnimalAppDB.Animal.COLUMN_NAME_SPECIES)).intValue();
                            AnimalSpecies species = AnimalSpecies.values()[SpeciesInteger];
                            AnimalStates[] EnumValues = AnimalStates.values();
                            AnimalStates state = EnumValues[Objects.requireNonNull(document.getLong(AnimalAppDB.Animal.COLUMN_NAME_STATE)).intValue()];

                            Animal getAnimal = Animal.Builder.create(document.getId(), state)
                                    .setSpecies(species)
                                    .setBirthDate(birthDate)
                                    .setName(document.getString("name"))
                                    .setOwner(document.getString("ownerID"))
                                    .build();
                            animalList.add(getAnimal);
                            System.out.println("Data  "+getAnimal.getBirthDate());
                        } else {
                            System.out.println("the 'birthday' in null o have an error");
                        }
                    }
                    listener.onGetAnimalListener(animalList);
                } else {
                    Log.w("W","Nothing to get in the database");
                    listener.onGetAnimalListener(new ArrayList<>());
                }
            } else {
                Log.w(TAG,"search failure");
                listener.onGetAnimalListener(new ArrayList<>());
            }
        });
    }
    public ArrayList <Relation> listRelation;
    public void getRelation(String ownerID, String idAnimal, final OnRelationAnimalListener listener) {
        listRelation = new ArrayList<>();
        getListAnimalDao(ownerID, animalGetList -> {
            Query query1 = collectionRelation.whereEqualTo("idAnimal1", idAnimal);
            Query query2 = collectionRelation.whereEqualTo("idAnimal2", idAnimal);

            List<DocumentSnapshot> resultList = new ArrayList<>();

            query1.get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    resultList.addAll(task1.getResult().getDocuments());
                    query2.get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            resultList.addAll(task2.getResult().getDocuments());

                            if (resultList.size()>0) {
                                for (DocumentSnapshot document : resultList) {
                                    final Relation.relationType[] relation = new Relation.relationType[1];
                                    relation[0] = Relation.relationType.valueOf(document.getString("Relation"));
                                    String idAnimal1 = document.getString("idAnimal1");
                                    String idAnimal2 = document.getString("idAnimal2");
                                    String documentID = document.getId();
                                    String searchAnimalId;
                                    if (idAnimal.equals(idAnimal1)) {
                                        searchAnimalId = idAnimal2;
                                    } else {
                                        searchAnimalId = idAnimal1;
                                    }


                                    getAnimalClass(searchAnimalId, animalClass -> {
                                        assert searchAnimalId != null;
                                        if (searchAnimalId.equals(idAnimal1)) {
                                            listRelation.add(Relation.Builder.create(documentID, relation[0], animalClass).build());
                                        } else {
                                            listRelation.add(Relation.Builder.create(documentID, relation[0], animalClass).build());
                                        }
                                        listener.onRelationAnimalListener(listRelation, animalGetList);
                                    });
                                }
                            }else{
                                System.out.println("Nothing here");
                                listener.onRelationAnimalListener(new ArrayList<>(),animalGetList);
                            }
                        } else {
                            Log.w(TAG,"Failure second time");
                            listener.onRelationAnimalListener(new ArrayList<>(),animalGetList);
                        }
                    });
                } else {
                    Log.w(TAG,"Failure first time");
                    listener.onRelationAnimalListener(new ArrayList<>(),animalGetList);
                }
            });
        });
    }

    private void getAnimalClass(String idAnimal, final OnRelationClassAnimalListener listener){
        collectionAnimalRelation.document(idAnimal).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
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
                        listener.onRelationClassAnimalListener(getAnimal);
                    }
                }else{
                    Log.d(TAG, "The document is not exsits");
                }
            }else {
                Log.w(TAG, "Failure to get the document", task.getException());
            }
        });
    }

    public interface OnRelationListener {
        void onGetAnimalListener(List <Animal> animalList);
    }
    public interface OnRelationCreated{
        void onRelationCreatedListener(Relation relation);
    }

    public interface OnRelationAnimalListener{
        void onRelationAnimalListener(ArrayList <Relation> relationList,List <Animal> animalList);
    }

    public interface OnRelationClassAnimalListener{
        void onRelationClassAnimalListener(Animal animalClass);
    }
}
