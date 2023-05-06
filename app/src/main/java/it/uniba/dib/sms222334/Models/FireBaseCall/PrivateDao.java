package it.uniba.dib.sms222334.Models.FireBaseCall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.R;

public final class PrivateDao {
    private final String TAG="PrivateDao";
    final private CollectionReference collectionPrivate = FirebaseFirestore.getInstance().collection("Private");
    final private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    final private Context context;

    public PrivateDao(Context context){
        this.context=context;
    }

    //gestire lista animali
    public Private getPrivateByEmail(String email){
        final Private[] requested_private = new Private[1];

        collectionPrivate.whereEqualTo("email",email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                Private.Builder private_requested_builder=Private.Builder.create(document.getString("name"),document.getString("surname"))
                                        .setPassword(document.getString("password"))
                                        .setEmail(document.getString("email"))
                                        .setDate(document.getDate("birthdate"))
                                        .setPhoneNumber(document.getLong("phone_number"));

                                StorageReference cane = storageRef.child(document.getString("photo"));

                                final long ONE_MEGABYTE = 1024 * 1024;
                                cane.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        private_requested_builder.setPhoto(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Bitmap no_photo_profile = BitmapFactory.decodeResource(context.getResources(), R.drawable.baseline_profile_24);
                                        private_requested_builder.setPhoto(no_photo_profile);
                                    }
                                });

                                requested_private[0] =private_requested_builder.build();
                                requested_private[0].setFirebaseID(document.getId());


                            } else {
                                requested_private[0]=null;
                                Log.d(TAG, "non esiste", task.getException());
                            }
                        } else {
                            Log.w(TAG, "Errore query.", task.getException());
                        }
                    }
                });

        return requested_private[0];
    }

    public void createPrivate(Private Private){
        List<DocumentReference> dr= new ArrayList<>();

        Map<String, Object> new_private = new HashMap<>();
        new_private.put("name", Private.getName());
        new_private.put("surname", Private.getSurname());
        new_private.put("animalList", dr);
        new_private.put("birthdate", new Timestamp(Private.getDate()));
        new_private.put("email", Private.getEmail());
        new_private.put("password", Private.getPassword());
        new_private.put("phone_number", Private.getPhoneNumber());
        new_private.put("photo", "");
        new_private.put("role", Private.getRole());
        new_private.put("tax_id_code", Private.getTax_id_code());

        collectionPrivate.add(new_private)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void deletePrivate(Private Private){
        collectionPrivate.document(Private.getFirebaseID())
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

    public void updatePrivate(Private Private){

    }

}
