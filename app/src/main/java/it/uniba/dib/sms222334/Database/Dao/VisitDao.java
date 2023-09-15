package it.uniba.dib.sms222334.Database.Dao;

import android.annotation.SuppressLint;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Visit;

public class VisitDao {
    private final String TAG="VisitDao";
    final private CollectionReference collectionVisit = FirebaseFirestore.getInstance().collection(AnimalAppDB.Visit.TABLE_NAME);

    private boolean returnValue = true;
    public boolean createVisit(Visit visit){
        Map<String, Object> newVisit = new HashMap<>();

        // Creazione di un riferimento all'animale usando il suo firebaseID
        DocumentReference animalReference = FirebaseFirestore.getInstance()
                .collection(AnimalAppDB.Animal.TABLE_NAME)
                .document(visit.getAnimal().getFirebaseID());

        newVisit.put("animalID", animalReference);
        newVisit.put("Visit Type", visit.getType().toString());
        newVisit.put("Date", visit.getDate().toString());
        newVisit.put("name", visit.getName());
        newVisit.put("diagnosis", "");
        newVisit.put("medical_note", "");
        newVisit.put("state", "");


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

    private boolean value = true;

    public boolean removeVisit(String idAnimal, String TypeVisit){
        System.out.println("ID  "+idAnimal);
        System.out.println("Tipo  "+TypeVisit);
        collectionVisit
                .whereEqualTo("animalID",idAnimal)
                .whereEqualTo("Visit Type",TypeVisit)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            collectionVisit.document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Visit successfully deleted!");
                                            value = true;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting Visit", e);
                                            value = false;
                                        }
                                    });
                        }
                    }
                });
        return value;
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

    public void editVisit(Visit visit,String idAnimal,String name){
        collectionVisit.whereEqualTo("animalID",idAnimal).whereEqualTo("name",name)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                            visit.setDate(convertDate(document.getString("Date")));
                            visit.setType(Visit.visitType.valueOf(document.getString("Visit Type")));
                            visit.setFirebaseID(document.getString("animalID"));
                            visit.setName(document.getString("name"));

                            Map<String,Object> updateMap = new HashMap<>();

                            System.out.println("Diagnosi   "+visit.getDiagnosis());

                            updateMap.put("Date",visit.getDate().toString());
                            updateMap.put("Visit Type",visit.getType().toString());
                            updateMap.put("animalID",visit.getFirebaseID());
                            updateMap.put("diagnosis",visit.getDiagnosis().toString());
                            updateMap.put("medical_note", visit.getMedicalNotes());
                            updateMap.put("name", visit.getName());
                            updateMap.put("state",visit.getState().toString());

                            collectionVisit.document(document.getId())
                                    .update(updateMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "update fatto");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "errore update");
                                        }
                                    });
                        }
                    }
                });
    }

    private Date convertDate (String DateString){
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

        Date date = null;

        try {
            // Parsa la data di input nel formato corretto
            date = inputDateFormat.parse(DateString);

            // Ora puoi formattare la data nel tuo formato desiderato
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            assert date != null;
            String formattedDate = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
