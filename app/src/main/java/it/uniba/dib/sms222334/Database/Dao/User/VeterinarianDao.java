package it.uniba.dib.sms222334.Database.Dao.User;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import it.uniba.dib.sms222334.Models.Veterinarian;
public class VeterinarianDao {
    private final String TAG = "VeterinarianDao";
    final private CollectionReference collectionVeterinarian = FirebaseFirestore.getInstance().collection(AnimalAppDB.Veterinarian.TABLE_NAME);

    public Veterinarian findVeterinarian(DocumentSnapshot document) {
        Veterinarian.Builder veterinarian_requested_builder = Veterinarian.Builder.
                create(
                        document.getId(),
                        document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_COMPANY_NAME),
                        document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_EMAIL))  //TODO: document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_PHOTO))
                .setLegalSite(document.getGeoPoint(AnimalAppDB.Veterinarian.COLUMN_NAME_SITE))
                .setPassword(document.getString(AnimalAppDB.Veterinarian.COLUMN_NAME_PASSWORD))
                .setPhone(document.getLong(AnimalAppDB.Veterinarian.COLUMN_NAME_PHONE_NUMBER).intValue())
                //.setLatitude(document.getDouble(AnimalAppDB.Veterinarian.COLUMN_NAME_BIRTH_DATE)) // TODO: Langitude
                //.setLongitude(document.getDouble(AnimalAppDB.Veterinarian.COLUMN_NAME_BIRTH_DATE)) // TODO: Longitude
                ;

        return veterinarian_requested_builder.build();
    }

    public void createVeterinarian(Veterinarian Veterinarian, final UserCallback.UserRegisterCallback callback){
        Map<String, Object> new_veterinarian = new HashMap<>();
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_COMPANY_NAME, Veterinarian.getName());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_EMAIL, Veterinarian.getEmail());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_PASSWORD, Veterinarian.getPassword());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_LOGO, "/images/profiles/users/default.jpg");
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_PHONE_NUMBER, Veterinarian.getPhone());
        new_veterinarian.put(AnimalAppDB.Veterinarian.COLUMN_NAME_SITE, Veterinarian.getLegalSite());

        collectionVeterinarian.add(new_veterinarian)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
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

    public void getVeterinariansDao(final OnVeterinarianListener listener){
        ArrayList<Veterinarian> list = new ArrayList<>();
        collectionVeterinarian.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String documentId = document.getId();
                        String companyName = document.getString("company_name");
                        String email = document.getString("email");
                        GeoPoint site = document.getGeoPoint("site");

                        assert site != null;
                        Veterinarian veterinarian = Veterinarian.Builder.create(documentId,companyName,email)
                                .setLegalSite(new GeoPoint(site.getLatitude(),site.getLongitude())).build();

                        System.out.println("ID: " + documentId);
                        System.out.println("Company Name: " + companyName);
                        System.out.println("Email: " + email);
                        System.out.println("Site (Latitude): " + site.getLatitude());
                        System.out.println("Site (Longitude): " + site.getLongitude());
                        list.add(veterinarian);
                    }
                    listener.onGetVeterinarianListener(list);
                }else{
                    Log.w("W","vuoto");
                    listener.onGetVeterinarianListener(new ArrayList<>());
                }
            } else {
                Log.w("W","query fallito");
                listener.onGetVeterinarianListener(new ArrayList<>());
            }
        });
    }

    public interface OnVeterinarianListener {
        void onGetVeterinarianListener(List<Veterinarian> veterinarianList);
    }
}
