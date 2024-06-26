package it.uniba.dib.sms222334.Database.Dao;

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
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.RequestType;

public class RequestDao {
    private final String TAG="RequestDao";
    final private CollectionReference collectionRequest = FirebaseFirestore.getInstance().collection(AnimalAppDB.Request.TABLE_NAME);

    public void createRequest(Request request, final DatabaseCallbackResult callbackModel) {
        Map<String, Object> new_request = new HashMap<>();

        new_request.put(AnimalAppDB.Request.COLUMN_NAME_USER_ID, request.getUser().getFirebaseID());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_TYPE, request.getType().ordinal());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_DESCRIPTION, request.getDescription());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_LOCATION, request.getLocation());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_SPECIES, request.getAnimalSpecies().ordinal());
        new_request.put(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_ID, (request.getAnimal() != null) ? request.getAnimal().getFirebaseID() : "");
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

    public void getAllRequests(final DatabaseCallbackResult callback, boolean hideOfferBedsRequest) {
        collectionRequest.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<QueryDocumentSnapshot> documentSnapshots = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            if (document instanceof QueryDocumentSnapshot)
                                documentSnapshots.add((QueryDocumentSnapshot) document);
                        }

                        processRequests(hideOfferBedsRequest, documentSnapshots, 0, callback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onDataQueryError(e);
                    }
                });
    }

    private void processRequests(boolean hideOfferBedsRequest, List<QueryDocumentSnapshot> documentSnapshots, int currentIndex, DatabaseCallbackResult callback) {
        if (currentIndex >= documentSnapshots.size()) {
            callback.onDataNotFound();
            return;
        }

        QueryDocumentSnapshot document = documentSnapshots.get(currentIndex);
        getRequestFromDocument(document, hideOfferBedsRequest, new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                callback.onDataRetrieved(result);
                processRequests(hideOfferBedsRequest, documentSnapshots, currentIndex + 1, callback);
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {
                processRequests(hideOfferBedsRequest, documentSnapshots, currentIndex + 1, callback);
            }

            @Override
            public void onDataQueryError(Exception e) {
                processRequests(hideOfferBedsRequest, documentSnapshots, currentIndex + 1, callback);
            }
        });
    }

    private void getRequestFromDocument(QueryDocumentSnapshot document, boolean hideOfferBedsRequest, DatabaseCallbackResult callback) {
        RequestType type = RequestType.values()[document.getLong(AnimalAppDB.Request.COLUMN_NAME_TYPE).intValue()];
        String userID = document.getString(AnimalAppDB.Request.COLUMN_NAME_USER_ID);

        if (hideOfferBedsRequest && type == RequestType.OFFER_BEDS) {
            if (!SessionManager.getInstance().isLogged() || !SessionManager.getInstance().getCurrentUser().getFirebaseID().equals(userID))  {
                callback.onDataNotFound();
                return;
            }
        }

        UserDao userDao = new UserDao();
        userDao.findUser(userID, new AuthenticationDao.FindUserListenerResult() {

            @Override
            public void onUserFound(User user) {
                AnimalDao animalDao = new AnimalDao();
                DocumentReference animalRef = animalDao.findAnimalRef(document.getString(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_ID));

                if (animalRef == null) {
                    Request request = Request.Builder.create(document.getId(),
                                    user,
                                    type,
                                    document.getString(AnimalAppDB.Request.COLUMN_NAME_DESCRIPTION),
                                    document.getGeoPoint(AnimalAppDB.Request.COLUMN_NAME_LOCATION)
                            )
                            .setAnimalSpecies(AnimalSpecies.values()[document.getLong(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_SPECIES).intValue()])
                            .setAnimal(null)
                            .setNBeds(document.getLong(AnimalAppDB.Request.COLUMN_NAME_BEDS_NUMBER).intValue()).build();

                    callback.onDataRetrieved(request);
                } else {
                    animalDao.getAnimalByReference(animalRef, new DatabaseCallbackResult<Animal>() {
                        @Override
                        public void onDataRetrieved(Animal result) {
                            Request request = Request.Builder.create(document.getId(),
                                            user,
                                            type,
                                            document.getString(AnimalAppDB.Request.COLUMN_NAME_DESCRIPTION),
                                            document.getGeoPoint(AnimalAppDB.Request.COLUMN_NAME_LOCATION)
                                    )
                                    .setAnimalSpecies(AnimalSpecies.values()[document.getLong(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_SPECIES).intValue()])
                                    .setAnimal(result)
                                    .setNBeds(document.getLong(AnimalAppDB.Request.COLUMN_NAME_BEDS_NUMBER).intValue()).build();

                            callback.onDataRetrieved(request);
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
            }
        });
    }

    public void deleteRequest(Request request, DatabaseCallbackResult callbackPresenter) {
        collectionRequest.document(request.getFirebaseID())
                .delete()
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

    public void updateRequest(Request request, DatabaseCallbackResult callback) {
        Map<String, Object> updatedRequest = new HashMap<>();

        updatedRequest.put(AnimalAppDB.Request.COLUMN_NAME_USER_ID, request.getUser().getFirebaseID());
        updatedRequest.put(AnimalAppDB.Request.COLUMN_NAME_TYPE, request.getType().ordinal());
        updatedRequest.put(AnimalAppDB.Request.COLUMN_NAME_DESCRIPTION, request.getDescription());
        updatedRequest.put(AnimalAppDB.Request.COLUMN_NAME_LOCATION, request.getLocation());
        updatedRequest.put(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_SPECIES, request.getAnimalSpecies().ordinal());
        updatedRequest.put(AnimalAppDB.Request.COLUMN_NAME_ANIMAL_ID, (request.getAnimal() != null) ? request.getAnimal().getFirebaseID() : "");
        updatedRequest.put(AnimalAppDB.Request.COLUMN_NAME_BEDS_NUMBER, request.getNBeds());

        collectionRequest.document(request.getFirebaseID())
                .update(updatedRequest)
                .addOnSuccessListener(aVoid -> {
                    callback.onDataRetrieved(true);
                })
                .addOnFailureListener(e -> {
                    callback.onDataQueryError(e);
                });
    }
}
