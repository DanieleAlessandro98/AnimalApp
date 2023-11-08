package it.uniba.dib.sms222334.Database.Dao;

import android.annotation.SuppressLint;
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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Utils.UserRole;

public class VisitDao {
    private final String TAG="VisitDao";
    final public static CollectionReference collectionVisit = FirebaseFirestore.getInstance().collection(AnimalAppDB.Visit.TABLE_NAME);


    public void getVisitsByDoctorID(String doctorId, VisitListener listener){
        collectionVisit.whereEqualTo(AnimalAppDB.Visit.COLUMN_NAME_DOCTOR_ID,doctorId).get()
                .addOnSuccessListener(task -> {

                    for(DocumentSnapshot document: task.getDocuments()){
                        String visitID = document.getId();
                        String visitName = document.getString(AnimalAppDB.Visit.COLUMN_NAME_NAME);
                        int visitType = Math.toIntExact(document.getLong(AnimalAppDB.Visit.COLUMN_NAME_TYPE));
                        Timestamp time = document.getTimestamp(AnimalAppDB.Visit.COLUMN_NAME_DATE);
                        DocumentReference animal = (DocumentReference) document.get(AnimalAppDB.Visit.COLUMN_NAME_ANIMAL);
                        int diagnosisType= Math.toIntExact(document.getLong(AnimalAppDB.Visit.COLUMN_NAME_DIAGNOSIS));
                        String doctorName= document.getString(AnimalAppDB.Visit.COLUMN_NAME_DOCTOR_NAME);
                        String ownerId= document.getString(AnimalAppDB.Visit.COLUMN_NAME_OWNER_ID);
                        String medicalNote= document.getString(AnimalAppDB.Visit.COLUMN_NAME_MEDICAL_NOTE);
                        int state= Math.toIntExact(document.getLong(AnimalAppDB.Visit.COLUMN_NAME_STATE));

                        new AnimalDao().getAnimalByReference(animal, new DatabaseCallbackResult<Animal>() {
                            @Override
                            public void onDataRetrieved(Animal result) {
                                Visit visit=Visit.Builder
                                        .create(visitID,visitName, Visit.visitType.values()[visitType],time)
                                        .setAnimal(result)
                                        .setDiagnosis(Visit.diagnosisType.values()[diagnosisType])
                                        .setDoctorName(doctorName)
                                        .setIDowner(ownerId)
                                        .setDoctorId(doctorId)
                                        .setMedicalNotes(medicalNote)
                                        .setState(Visit.visitState.values()[state])
                                        .build();

                                listener.onVisitLoadSuccesfull(visit);
                            }

                            @Override
                            public void onDataRetrieved(ArrayList<Animal> results) {

                            }

                            @Override
                            public void onDataNotFound() {

                            }

                            @Override
                            public void onDataQueryError(Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(listener::onVisitLoadFailed);

    }

    public void createVisit(Visit visit,OnVisitCreateListener listener){
        Map<String, Object> newVisit = new HashMap<>();

        DocumentReference animalReference = FirebaseFirestore.getInstance()
                .collection(AnimalAppDB.Animal.TABLE_NAME)
                .document(visit.getAnimal().getFirebaseID());

        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_ANIMAL, animalReference);
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_TYPE, visit.getType().ordinal());
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DATE, visit.getDate());
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_NAME, visit.getName());
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DIAGNOSIS, Visit.diagnosisType.NULL.ordinal());
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DOCTOR_NAME,"");
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_MEDICAL_NOTE, "");
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_STATE, Visit.visitState.NOT_EXECUTED.ordinal());
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DOCTOR_ID,visit.getDoctorID());
        newVisit.put(AnimalAppDB.Visit.COLUMN_NAME_OWNER_ID,visit.getAnimal().getOwnerReference());

        collectionVisit.add(newVisit).addOnSuccessListener(documentReference -> {
            Log.d(TAG,"Visit is create");
            visit.setFirebaseID(documentReference.getId());
            listener.onCreateVisit(visit);
        }).addOnFailureListener(e -> {
            Log.w(TAG,"Creation Visit is failure");
            listener.onFailureCreateVisit();
        });
    }

    public void deleteVisit(Visit visit){
        collectionVisit.document(visit.getFirebaseID())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    public void editVisit(Visit visit,OnVisitEditListener listener){
        DocumentReference reference = collectionAnimalVisit.document(visit.getAnimal().getFirebaseID());

        Map<String, Object> editedVisit = new HashMap<>();

        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_ANIMAL, reference);
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_TYPE, visit.getType().ordinal());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DATE, visit.getDate());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_NAME, visit.getName());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DIAGNOSIS, visit.getDiagnosis().ordinal());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DOCTOR_NAME,visit.getDoctorName());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_MEDICAL_NOTE, visit.getMedicalNotes());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_STATE, visit.getState().ordinal());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_DOCTOR_ID,visit.getDoctorID());
        editedVisit.put(AnimalAppDB.Visit.COLUMN_NAME_OWNER_ID,visit.getAnimal().getOwnerReference());

        collectionVisit.document(visit.getFirebaseID())
                .update(editedVisit)
                .addOnSuccessListener(command -> {
                    listener.onSuccessEdit();
                })
                .addOnFailureListener(e -> {
                    listener.onFailureEdit();
                });

    }

    final private CollectionReference collectionAnimalVisit = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);

    public interface VisitListener {
        void onVisitLoadSuccesfull(Visit visit);

        void onVisitLoadFailed(Exception e);
    }

    public interface OnVisitCreateListener{
        void onCreateVisit(Visit visit);
        void onFailureCreateVisit();
    }

    public interface OnVisitEditListener{
        void onSuccessEdit();
        void onFailureEdit();
    }

    public interface OnVisitDeleteListener{
        void onSuccessDelete();
        void onFailureDelete();
    }
}
