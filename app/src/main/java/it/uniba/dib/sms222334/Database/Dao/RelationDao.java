package it.uniba.dib.sms222334.Database.Dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
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

    public void createRelationDao(Relation relation, String idMyAnimal, OnRelationCreateListener callBack){
        Map<String,String> newRelation = new HashMap<>();
        newRelation.put("idAnimal1",idMyAnimal);
        newRelation.put("idAnimal2",relation.getAnimal().getFirebaseID());
        newRelation.put("Relation",relation.getRelationType().toString());

        collectionRelation.add(newRelation).addOnSuccessListener(documentReference -> {
            relation.setFirebaseID(documentReference.getId());
            callBack.onCreateSuccess(relation);
        }).addOnFailureListener(e -> {
            callBack.onCreateFailure();
        });
    }
    public void deleteRelationDao(Relation relation){
        System.out.println("firebase id"+relation.getFirebaseID());
        collectionRelation.document(relation.getFirebaseID())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("I","The Relation is deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("W","delete relation failure");
                    }
                });
    }

    public void getAnimalListForChooseAnimalDao(String ownerID, final OnGetListAnimalForChooseAnimal listener){
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
                        } else {
                            Log.w("W","birthday' in null o have an error");
                        }
                    }
                    listener.onGetListAnimalForChooseAnimalListener(animalList);
                } else {
                    Log.w("W","Nothing to get in the database");
                    listener.onGetListAnimalForChooseAnimalListener(new ArrayList<>());
                }
            } else {
                Log.w(TAG,"search failure");
                listener.onGetListAnimalForChooseAnimalListener(new ArrayList<>());
            }
        });
    }
    public ArrayList <Relation> listRelation;
    public void getRelation(String idAnimal, final OnAnimalRelationListListener listener) {
        listRelation = new ArrayList<>();
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
                                    listener.onAnimalRelationListListener(listRelation);
                                });
                            }
                        }else{
                            Log.w("W","Nothing here");
                            listener.onAnimalRelationListListener(new ArrayList<>());
                        }
                    } else {
                        Log.w(TAG,"Failure second time");
                        listener.onAnimalRelationListListener(new ArrayList<>());
                    }
                });
            } else {
                Log.w(TAG,"Failure first time");
                listener.onAnimalRelationListListener(new ArrayList<>());
            }
        });
    }
    //this method is use for get all date of an animal and set in a class.
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
    public interface OnRelationCreateListener {
        void onCreateSuccess(Relation relation);
        void onCreateFailure();
    }

    public interface OnRelationAnimalListener{
        void onRelationAnimalListener(ArrayList <Relation> relationList,List <Animal> animalList);
    }

    public interface OnRelationClassAnimalListener{
        void onRelationClassAnimalListener(Animal animalClass);
    }

    public interface OnGetListAnimalForChooseAnimal {
        void onGetListAnimalForChooseAnimalListener(List <Animal> animalList);
    }

    public interface OnAnimalRelationListListener {
        void onAnimalRelationListListener(ArrayList <Relation> relationList);
    }


}
