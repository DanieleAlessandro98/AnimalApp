package it.uniba.dib.sms222334.Database.Dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Visit;

public class VisitDao {
    private final String TAG="VisitDao";
    final private CollectionReference collectionVisit = FirebaseFirestore.getInstance().collection(AnimalAppDB.Visit.TABLE_NAME);

    private boolean returnValue = true;
    public boolean createVisit(Visit visit){
        Map<String,String> newVisit = new HashMap<>();

        newVisit.put("animalID",visit.getAnimal().getFirebaseID());
        newVisit.put("Visit Type",visit.getType().toString());
        newVisit.put("Date",visit.getDate().toString());
        newVisit.put("name",visit.getName());
        newVisit.put("diagnosis","");
        newVisit.put("medical_note","");
        newVisit.put("state","");

        collectionVisit.add(newVisit).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"Visit is create");
                returnValue = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Creation Visit is failure");
                returnValue = false;
            }
        });
        return returnValue;
    }

    public void deleteVisit(Visit visit){
        collectionVisit.document(visit.getFirebaseID())
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

}
