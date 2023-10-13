package it.uniba.dib.sms222334.Database.Dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Utils.Media;

public class ReportDao {
    private final String TAG="ReportDao";
    final private CollectionReference collectionReport = FirebaseFirestore.getInstance().collection(AnimalAppDB.Report.TABLE_NAME);

    public void createReport(Report report, final DatabaseCallbackResult callbackModel) {
        Map<String, Object> new_report = new HashMap<>();

        new_report.put(AnimalAppDB.Report.COLUMN_NAME_TYPE, report.getType());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_SPECIES, report.getAnimalSpecies());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_DESCRIPTION, report.getDescription());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_LOCATION, report.getLocation());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_NAME, report.getAnimalName());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_AGE, report.getAnimalAge());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_ID, report.getAnimalID());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_SHOW_ANIMAL_PROFILE, report.isShowAnimalProfile());

        collectionReport.add(new_report)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentID = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentID);

                        setPhotoPath(documentID);
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

    public void setPhotoPath(String documentID) {
        Map<String, Object> report = new HashMap<>();
        report.put(AnimalAppDB.Report.COLUMN_NAME_PHOTO, Media.REPORT_PHOTO_PATH + documentID + Media.PROFILE_PHOTO_EXTENSION);

        collectionReport.document(documentID)
                .update(report)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

}
