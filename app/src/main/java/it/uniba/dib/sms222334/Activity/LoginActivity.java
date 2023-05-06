package it.uniba.dib.sms222334.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
<<<<<<< HEAD
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
=======
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
>>>>>>> 54b31f7063d419d26bab3a7fae7532265ec58e37

import it.uniba.dib.sms222334.R;

public class LoginActivity extends AppCompatActivity {

<<<<<<< HEAD
    final static String TAG="LoginActivitybochicchio";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
=======
    final static String TAG="LoginActivity";
>>>>>>> 54b31f7063d419d26bab3a7fae7532265ec58e37

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.private_register);

        db.collection("Private")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
=======
        setContentView(R.layout.login_layout);
>>>>>>> 54b31f7063d419d26bab3a7fae7532265ec58e37

    }
}