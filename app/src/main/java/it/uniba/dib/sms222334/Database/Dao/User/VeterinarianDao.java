package it.uniba.dib.sms222334.Database.Dao.User;

import android.content.Context;
import android.graphics.Bitmap;
import android.telephony.TelephonyScanManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.VisitDao;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Utils.Media;

public class VeterinarianDao {
    private final String TAG = "VeterinarianDao";
    public static CollectionReference collectionVeterinarian = FirebaseFirestore.getInstance().collection(AnimalAppDB.Veterinarian.TABLE_NAME);

    Context context;

    public VeterinarianDao(){

    }

    public VeterinarianDao(Context context){
        this.context=context;
    }

    public void findVeterinarian(DocumentSnapshot document, VeterinarianCallback callback) {
        MediaDao mediaDao= new MediaDao();

        mediaDao.downloadPhoto(document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_LOGO), new MediaDao.PhotoDownloadListener() {
            @Override
            public void onPhotoDownloaded(Bitmap bitmap) {
                Veterinarian.Builder veterinarian_requested_builder = Veterinarian.Builder.
                        create(
                                document.getId(),
                                document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_COMPANY_NAME),
                                document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_EMAIL))
                        .setPhoto(bitmap)
                        .setLocation(document.getGeoPoint(AnimalAppDB.Veterinarian.COLUMN_NAME_SITE))
                        .setPassword(document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_PASSWORD))
                        .setPhone(document.getLong(AnimalAppDB.Veterinarian.COLUMN_NAME_PHONE_NUMBER));


                Veterinarian veterinarian= veterinarian_requested_builder.build();

                callback.onVeterinarianFound(veterinarian);
            }

            @Override
            public void onPhotoDownloadFailed(Exception exception) {
                callback.onVeterinarianFindFailed(exception);
            }
        });

    }

    public void createVeterinarian(Veterinarian Veterinarian, final UserCallback.UserRegisterCallback callback){
        Map<String, Object> new_veterinarian = new HashMap<>();
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_COMPANY_NAME, Veterinarian.getName());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_EMAIL, Veterinarian.getEmail());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_PASSWORD, Veterinarian.getPassword());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_LOGO, "/images/profiles/users/default.jpg");
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_PHONE_NUMBER, Veterinarian.getPhone());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_SITE, Veterinarian.getLocation());

        collectionVeterinarian.add(new_veterinarian)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        // L'inserimento del DocumentSnapshot ha avuto successo, quindi procedi con l'autenticazione
                        AuthenticationDao.fireAuth(Veterinarian.getEmail(), Veterinarian.getPassword(), documentReference, callback);
                        callback.onRegisterSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        callback.onRegisterFail();
                    }
                });
    }

    public void loadVeterinarianVisits(Veterinarian resultVeterinarian){
        new VisitDao().getVisitsByDoctorID(resultVeterinarian.getFirebaseID(), new VisitDao.VisitListener() {
            @Override
            public void onVisitLoadSuccesfull(Visit visit) {
                resultVeterinarian.addVisit(visit);
            }

            @Override
            public void onVisitLoadFailed(Exception e) {
                if(context!=null)
                    Toast.makeText(context, "Impossible to load visits", Toast.LENGTH_SHORT).show();
            }
        });
    }




    public void updateVeterinarian(Veterinarian updateVeterinarian, UserCallback.UserUpdateCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*
        List<DocumentReference> dr= new ArrayList<>();


        for(Animal a: updateVeterinarian.getAnimalList()){
            DocumentReference documentReference = AnimalDao.collectionAnimal.document(a.getFirebaseID());
            dr.add(documentReference);
        }
        */

        user.updateEmail(updateVeterinarian.getEmail())
                .addOnCompleteListener(task -> {});

        Map<String, Object> newVeterinarianData = new HashMap<>();
        newVeterinarianData.put(AnimalAppDB.Veterinarian.COLUMN_NAME_COMPANY_NAME, updateVeterinarian.getName());
        newVeterinarianData.put(AnimalAppDB.Veterinarian.COLUMN_NAME_EMAIL, updateVeterinarian.getEmail());
        newVeterinarianData.put(AnimalAppDB.Veterinarian.COLUMN_NAME_PASSWORD, updateVeterinarian.getPassword());
        newVeterinarianData.put(AnimalAppDB.Veterinarian.COLUMN_NAME_PHONE_NUMBER, updateVeterinarian.getPhone());
        newVeterinarianData.put(AnimalAppDB.Veterinarian.COLUMN_NAME_SITE, updateVeterinarian.getLocation());

        collectionVeterinarian.document(updateVeterinarian.getFirebaseID())
                .update(newVeterinarianData)
                .addOnSuccessListener(
                        aVoid -> {
                            callback.notifyUpdateSuccesfull();
                            Log.d(TAG, "update fatto");
                        })
                .addOnFailureListener(
                        e -> {
                            callback.notifyUpdateFailed();
                            Log.d(TAG, "errore update");
                        });
    }

    public void updatePhoto(String userID) {
        Map<String, Object> newVeterinarianData = new HashMap<>();
        newVeterinarianData.put(AnimalAppDB.Veterinarian.COLUMN_NAME_LOGO, Media.PROFILE_PHOTO_PATH + userID + Media.PROFILE_PHOTO_EXTENSION);
        collectionVeterinarian.document(userID)
                .update(newVeterinarianData);
    }

    public void deleteVeterinarian(Veterinarian veterinarian){
        collectionVeterinarian.document(veterinarian.getFirebaseID())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }


    public interface VeterinarianCallback {
        void onVeterinarianFound(Veterinarian resultVeterinarian);
        void onVeterinarianFindFailed(Exception exception);
    }

}
