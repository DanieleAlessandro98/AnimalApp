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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Fragmets.ListFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.AnimalStates;
import it.uniba.dib.sms222334.Utils.UserRole;

public class VisitDao {
    private final String TAG="VisitDao";
    final private CollectionReference collectionVisit = FirebaseFirestore.getInstance().collection(AnimalAppDB.Visit.TABLE_NAME);

    private boolean returnValue = true;
    public boolean createVisit(Visit visit){
        Map<String, Object> newVisit = new HashMap<>();

        DocumentReference animalReference = FirebaseFirestore.getInstance()
                .collection(AnimalAppDB.Animal.TABLE_NAME)
                .document(visit.getAnimal().getFirebaseID());

        newVisit.put("animalID", animalReference);
        newVisit.put("Visit Type", visit.getType().toString());
        newVisit.put("Date", visit.getDate().toString());
        newVisit.put("name", visit.getName());
        newVisit.put("diagnosis", "");
        newVisit.put("doctor name","");
        newVisit.put("medical_note", "");
        newVisit.put("state", "");
        newVisit.put("idDoctor",visit.getDoctorFirebaseID());
        newVisit.put("idOwner",visit.getAnimal().getOwnerReference());

        collectionVisit.add(newVisit).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"Visit is create");
                visit.setFirebaseID(documentReference.getId());
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

    public void editVisit(Visit visit,String idAnimal,String name){
        DocumentReference reference = collectionAnimalVisit.document(idAnimal);
        collectionVisit.whereEqualTo("animalID",reference).whereEqualTo("name",name)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            DocumentReference animalid = (DocumentReference) document.get("animalID");

                            System.out.println(animalid.getPath());

                            visit.setDate(convertDate(document.getString("Date")));
                            visit.setType(Visit.visitType.valueOf(document.getString("Visit Type")));
                            visit.setFirebaseID(animalid.getPath());
                            visit.setName(document.getString("name"));

                            Map<String,Object> updateMap = new HashMap<>();

                            updateMap.put("Date",visit.getDate().toString());
                            updateMap.put("Visit Type",visit.getType().toString());
                            updateMap.put("animalID",animalid);
                            updateMap.put("diagnosis",visit.getDiagnosis().toString());
                            updateMap.put("medical_note", visit.getMedicalNotes());
                            updateMap.put("name", visit.getName());
                            updateMap.put("state",visit.getState().toString());
                            updateMap.put("doctor name",visit.getDoctorFirebaseID());

                            collectionVisit.document(document.getId())
                                    .update(updateMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "update fatto");
                                            System.out.println("update fatto");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("fallito");
                                            Log.d(TAG, "errore update");
                                        }
                                    });
                        }else{
                            System.out.println("documento non trovato");
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

    public void viewVisitListDao(UserRole id,final OnVisitListener listener){
        ArrayList <Visit> visits = new ArrayList<>();


        if (id == UserRole.PRIVATE || id == UserRole.PUBLIC_AUTHORITY){
            collectionVisit.whereEqualTo("idOwner", ListFragment.currentUser.getFirebaseID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String visitID = document.getId();
                                String visitName = document.getString("name");
                                String visitType = document.getString("Visit Type");
                                Date time = convertDate(document.getString("Date"));
                                DocumentReference animalID = (DocumentReference) document.get("animalID");
                                getAnimalClass(animalID.getId(), new RelationDao.OnRelationClassAnimalListener() {
                                    @Override
                                    public void onRelationClassAnimalListener(Animal animalList) {
                                        visits.add(Visit.Builder.create(visitID,visitName, Visit.visitType.valueOf(visitType),time)
                                                        .setAnimal(animalList)
                                                        .build());
                                        listener.onGetVisitListener(visits);
                                    }
                                });
                            }
                        } else {
                            Log.w("W","Nessun dato trovato");
                            listener.onGetVisitListener(new ArrayList<>());
                        }
                    } else {
                        Log.w("W","La query non ha funzionato");
                        listener.onGetVisitListener(new ArrayList<>());
                    }
                }
            });
        }else{
            System.out.println("id doctor: "+ListFragment.currentUser.getFirebaseID());
            collectionVisit.whereEqualTo("idDoctor", ListFragment.currentUser.getFirebaseID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String visitID = document.getId();
                                String visitName = document.getString("name");
                                String visitType = document.getString("Visit Type");
                                Date time = convertDate(document.getString("Date"));
                                DocumentReference animalID = (DocumentReference) document.get("animalID");

                                getAnimalClass(animalID.getId(), new RelationDao.OnRelationClassAnimalListener() {
                                    @Override
                                    public void onRelationClassAnimalListener(Animal animalList) {
                                        visits.add(Visit.Builder.create(visitID,visitName, Visit.visitType.valueOf(visitType),time)
                                                .setAnimal(animalList)
                                                .build());
                                        System.out.println("preso tutto dottore");
                                        listener.onGetVisitListener(visits);
                                    }
                                });
                            }
                            listener.onGetVisitListener(visits);
                        } else {
                            Log.w("W","Nessun dato trovato");
                            listener.onGetVisitListener(new ArrayList<>());
                        }
                    } else {
                        Log.w("W","La query non ha funzionato");
                        listener.onGetVisitListener(new ArrayList<>());
                    }
                }
            });
        }
    }
    final private CollectionReference collectionAnimalVisit = FirebaseFirestore.getInstance().collection(AnimalAppDB.Animal.TABLE_NAME);
    private void getAnimalClass(String idAnimal, final RelationDao.OnRelationClassAnimalListener listener) {
        collectionAnimalVisit.document(idAnimal).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Timestamp timestamp = document.getTimestamp("birthdate");
                        if (timestamp != null) {
                            Date birthDate = timestamp.toDate();
                            //TODO da capire come sistemare lo stato dell'animale al suo tipo
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
                    } else {
                        Log.d(TAG, "Il documento non esiste.");
                    }
                } else {
                    Log.w(TAG, "Errore nel recupero del documento.", task.getException());
                }
            }
        });
    }

    public interface OnVisitListener {
        void onGetVisitListener(List<Visit> visitList);
    }
}
