package it.uniba.dib.sms222334.Database.Dao;

import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.Media;
import it.uniba.dib.sms222334.Utils.ReportType;

public class ReportDao {
    private final String TAG="ReportDao";
    final private CollectionReference collectionReport = FirebaseFirestore.getInstance().collection(AnimalAppDB.Report.TABLE_NAME);

    public void createReport(Report report, final DatabaseCallbackResult callbackModel) {
        Map<String, Object> new_report = new HashMap<>();

        new_report.put(AnimalAppDB.Report.COLUMN_NAME_USER_ID, (report.getUser() != null) ? report.getUser().getFirebaseID() : "");
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_TYPE, report.getType().ordinal());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_SPECIES, report.getAnimalSpecies().ordinal());
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

                        AnimalDao animalDao = new AnimalDao();
                        animalDao.updateState(report.getAnimalID(), Animal.findAnimalStateByReport(report.getUser(), report.getType(), false));

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

    public void getAllReports(final DatabaseCallbackResult callback) {
        collectionReport.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Report> reports = new ArrayList<>();
                        List<QueryDocumentSnapshot> documentSnapshots = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            if (document instanceof QueryDocumentSnapshot)
                                documentSnapshots.add((QueryDocumentSnapshot) document);
                        }

                        processReports(reports, documentSnapshots, 0, callback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onDataQueryError(e);
                    }
                });
    }

    private void processReports(ArrayList<Report> report, List<QueryDocumentSnapshot> documentSnapshots, int currentIndex, DatabaseCallbackResult callback) {
        if (currentIndex >= documentSnapshots.size()) {
            callback.onDataNotFound();
            return;
        }

        QueryDocumentSnapshot document = documentSnapshots.get(currentIndex);
        getReportFromDocument(document, new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                callback.onDataRetrieved(result);
                processReports(report, documentSnapshots, currentIndex + 1, callback);
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {
                processReports(report, documentSnapshots, currentIndex + 1, callback);
            }
        });
    }

    private void getReportFromDocument(QueryDocumentSnapshot document, DatabaseCallbackResult callback) {
        final long MAX_SIZE = 1024 * 1024;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/reports/" + document.getId() + ".jpg");

        storageRef.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String userID = document.getString(AnimalAppDB.Report.COLUMN_NAME_USER_ID);

                if (userID.equals("")) {
                    Report report = Report.Builder.create(document.getId(),
                                    null,
                                    ReportType.values()[document.getLong(AnimalAppDB.Report.COLUMN_NAME_TYPE).intValue()],
                                    AnimalSpecies.values()[document.getLong(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_SPECIES).intValue()],
                                    document.getString(AnimalAppDB.Report.COLUMN_NAME_DESCRIPTION),
                                    document.getGeoPoint(AnimalAppDB.Report.COLUMN_NAME_LOCATION),
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
                            .setAnimalName(document.getString(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_NAME))
                            .setAnimalAge(document.getDate(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_AGE))
                            .setAnimalID(document.getString(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_ID))
                            .setShowAnimalProfile(document.getBoolean(AnimalAppDB.Report.COLUMN_NAME_SHOW_ANIMAL_PROFILE))
                            .build();

                    callback.onDataRetrieved(report);
                } else {
                    UserDao userDao = new UserDao();
                    userDao.findUser(document.getString(AnimalAppDB.Report.COLUMN_NAME_USER_ID), new AuthenticationDao.FindUserListenerResult() {
                        @Override
                        public void onUserFound(User user) {
                            Report report = Report.Builder.create(document.getId(),
                                            user,
                                            ReportType.values()[document.getLong(AnimalAppDB.Report.COLUMN_NAME_TYPE).intValue()],
                                            AnimalSpecies.values()[document.getLong(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_SPECIES).intValue()],
                                            document.getString(AnimalAppDB.Report.COLUMN_NAME_DESCRIPTION),
                                            document.getGeoPoint(AnimalAppDB.Report.COLUMN_NAME_LOCATION),
                                            BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
                                    .setAnimalName(document.getString(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_NAME))
                                    .setAnimalAge(document.getDate(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_AGE))
                                    .setAnimalID(document.getString(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_ID))
                                    .setShowAnimalProfile(document.getBoolean(AnimalAppDB.Report.COLUMN_NAME_SHOW_ANIMAL_PROFILE))
                                    .build();

                            callback.onDataRetrieved(report);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onDataQueryError(exception);
            }
        });
    }

    public void deleteReport(Report report, DatabaseCallbackResult callbackPresenter) {
        collectionReport.document(report.getFirebaseID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AnimalDao animalDao = new AnimalDao();
                        animalDao.updateState(report.getAnimalID(), Animal.findAnimalStateByReport(report.getUser(), report.getType(), true));

                        callbackPresenter.onDataRetrieved(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackPresenter.onDataRetrieved(false);
                    }
                });
    }

    public void updateReport(Report report, DatabaseCallbackResult callbackPresenter) {
        Map<String, Object> new_report = new HashMap<>();

        new_report.put(AnimalAppDB.Report.COLUMN_NAME_USER_ID, (report.getUser() != null) ? report.getUser().getFirebaseID() : "");
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_TYPE, report.getType().ordinal());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_SPECIES, report.getAnimalSpecies().ordinal());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_DESCRIPTION, report.getDescription());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_LOCATION, report.getLocation());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_NAME, report.getAnimalName());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_AGE, report.getAnimalAge());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_ANIMAL_ID, report.getAnimalID());
        new_report.put(AnimalAppDB.Report.COLUMN_NAME_SHOW_ANIMAL_PROFILE, report.isShowAnimalProfile());

        collectionReport.document(report.getFirebaseID())
                .update(new_report)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbackPresenter.onDataRetrieved(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackPresenter.onDataRetrieved(false);
                    }
                });
    }
}