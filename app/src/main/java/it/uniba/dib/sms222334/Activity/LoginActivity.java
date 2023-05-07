package it.uniba.dib.sms222334.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import it.uniba.dib.sms222334.Presenters.LoginPresenter;
import it.uniba.dib.sms222334.R;

public class LoginActivity extends AppCompatActivity {

    final static String TAG="LoginActivity";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initView();
        initPresenter();
        initListeners();
    }

    private void initView() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
    }

    private void initPresenter() {
        loginPresenter = new LoginPresenter(this);
    }

    private void initListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                loginPresenter.onLogin(email, password);
            }
        });
    }

    public void showLoginSuccess() {
        Toast.makeText(this, "Accesso effettuato con successo", Toast.LENGTH_SHORT).show();
    }

    public void showLoginError() {
        Toast.makeText(this, "Errore nell'accesso", Toast.LENGTH_SHORT).show();
    }
}