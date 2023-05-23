package it.uniba.dib.sms222334.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Fragmets.RegisterFragment;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;

public class LoginActivity extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final static String TAG="LoginActivity";

    TextView link_register;

    private ActivityResultLauncher<Intent> registerResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);


        link_register=findViewById(R.id.sign_link);

        link_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        this.registerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            authSuccesfull(result.getData().getIntExtra("user-role",0));
                        }
                    }
                });

    }

    public void authSuccesfull(@IntRange(from = 0,to = 2) int Role){
        final Intent authIntent =new Intent();
        authIntent.putExtra("user-role",Role);
        setResult(RESULT_OK,authIntent);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
        setResult(RESULT_CANCELED);
        finish();
    }

    public void openRegister(){
        Intent registerIntent= new Intent(this,RegisterActivity.class);

        this.registerResultLauncher.launch(registerIntent);
        overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
    }
}