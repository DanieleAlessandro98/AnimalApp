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
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Request;

public class RequestDao {
    private final String TAG="RequestDao";
    final private CollectionReference collectionRequest = FirebaseFirestore.getInstance().collection(AnimalAppDB.Request.TABLE_NAME);

    public void createRequest(Request request, final DatabaseCallbackResult callbackModel) {
        Map<String, Object> new_request = new HashMap<>();

        new_request.put(AnimalAppDB.Request.COLUMN_NAME_USER_ID, request.getUserID());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_TYPE, request.getType().ordinal());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_DESCRIPTION, request.getDescription());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_SPECIES, request.getAnimalSpecies().ordinal());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_ID, request.getAnimalID());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_BEDS_NUMBER, request.getNBeds());

        collectionRequest.add(new_request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentID = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentID);

                        callbackModel.onDataRetrieved(documentID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        callbackModel.onDataQueryError(e);
                    }
                });
    }

}
