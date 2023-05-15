package it.uniba.dib.sms222334.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import it.uniba.dib.sms222334.Presenters.LoginPresenter;
import it.uniba.dib.sms222334.R;

public class LoginActivity extends AppCompatActivity {

    final static String TAG="LoginActivity";
    TextView link_register;

    private ActivityResultLauncher<Intent> registerResultLauncher;

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
        initRegisterActivity();
    }

    private void initView() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        link_register=findViewById(R.id.sign_link);
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

        link_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });
    }

    private void initRegisterActivity() {
        registerResultLauncher = registerForActivityResult(
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

    public void showLoginSuccess() {
        Toast.makeText(this, "Accesso effettuato con successo", Toast.LENGTH_SHORT).show();
    }

    public void showLoginError() {
        Toast.makeText(this, "Errore nell'accesso", Toast.LENGTH_SHORT).show();
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