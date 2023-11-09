package it.uniba.dib.sms222334.Database.Dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Models.Pathology;

public class PathologyDao {
    private final String TAG="PathologyDao";
    public final static CollectionReference collectionPathology = FirebaseFirestore.getInstance().collection(AnimalAppDB.Pathology.TABLE_NAME);

    public void deletePathology(Pathology pathology, AnimalCallbacks.eliminationCallback callback){
        collectionPathology.document(pathology.getFirebaseID())
                .delete()
                .addOnSuccessListener(command -> {
                    if(callback!=null)
                        callback.eliminatedSuccesfully();
                })
                .addOnFailureListener(e -> {
                    if(callback!=null)
                        callback.failedElimination();
                });
    }

    public void createPathology(Pathology pathology,String TypePathology,OnPathologyCreateListener listener){
        Map<String,String> newAnimal = new HashMap<>();

        newAnimal.put("ID animal",pathology.getIdAnimal());
        newAnimal.put("Type pathology",TypePathology);

        collectionPathology.add(newAnimal).addOnSuccessListener(documentReference -> {
            pathology.setFirebaseID(documentReference.getId());
            listener.onCreatedReady(pathology);
        }).addOnFailureListener(e -> {
            listener.onFailureReady();
        });
    }

    public interface PathologyListener {
        void onPathologyFound(Pathology pathology);
    }

    public interface OnPathologyCreateListener{
        void onCreatedReady(Pathology pathology);
        void onFailureReady();
    }

}
