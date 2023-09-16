package it.uniba.dib.sms222334.Database.Dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Pathology;

public class PathologyDao {
    private final String TAG="PathologyDao";
    final private CollectionReference collectionPathology = FirebaseFirestore.getInstance().collection(AnimalAppDB.Pathology.TABLE_NAME);

    public void deletePathology(Pathology pathology){
        collectionPathology.document(pathology.getFirebaseID())
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

    private boolean value = true;

    public boolean deleteAnimalPathology(String id,String name) {
        collectionPathology.document(id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    value = true;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                    value = false;
                                }
                            });
        return value;
    }


    private static boolean valueReturn = true;

    public boolean createPathology(String IdAnimal, String TypePathology){
        Map<String,String> newAnimal = new HashMap<>();

        newAnimal.put("ID animal",IdAnimal);
        newAnimal.put("Type pathology",TypePathology);

        collectionPathology.add(newAnimal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"Creazione avenuta");
                valueReturn = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Creazione patologia fallita");
                valueReturn = false;
            }
        });
        return valueReturn;
    }

    public interface OnPathologyListListener {
        void onPathologyListReady(ArrayList<Pathology> listPathology);
    }

    private static ArrayList <Pathology> listPathology ;

    public static ArrayList<Pathology> getListPathology() {
        return listPathology;
    }

    public void getListPathology(String idAnimal, final OnPathologyListListener listener){
        listPathology = new ArrayList<>();
        collectionPathology.whereEqualTo("ID animal",idAnimal).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String pathologyData = document.getString("Type pathology");
                            listPathology.add(Pathology.Builder.create(document.getId(), pathologyData).build());
                        }
                        listener.onPathologyListReady(listPathology);
                    } else {
                        Log.w("W","Nessun dato trovato");
                        listener.onPathologyListReady(new ArrayList<>());
                    }
                } else {
                    Log.w("W","La query non ha funzionato");
                    listener.onPathologyListReady(new ArrayList<>());
                }
            }
        });
    }


}
