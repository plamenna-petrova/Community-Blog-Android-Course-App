package com.example.communityblog.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communityblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity
{

    private EditText userMailLogin;
    private EditText userPasswordLogin;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView loginPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMailLogin = findViewById(R.id.login_mail);
        userPasswordLogin = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login_btn);
        loginProgress = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, com.example.communityblog.Activities.HomeActivity.class);
        loginPhoto = findViewById(R.id.login_photo);

        loginPhoto.setOnClickListener(view -> {
            Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(registerActivity);
            finish();
        });

        loginProgress.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(view -> {
            loginProgress.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);

            final String loginMail = userMailLogin.getText().toString();
            final String loginPassword = userPasswordLogin.getText().toString();

            if (loginMail.isEmpty() || loginPassword.isEmpty()) {
                showLoginMessage("Please Verify All Fields");
                btnLogin.setVisibility(View.VISIBLE);
                loginProgress.setVisibility(View.INVISIBLE);
            }
            else
            {
                signIn(loginMail, loginPassword);
            }
        });
    }

    private void signIn(String mail, String password)
    {
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginProgress.setVisibility(View.INVISIBLE);
                        btnLogin.setVisibility(View.VISIBLE);
                        updateUI();
                    }
                    else
                    {
                        showLoginMessage(Objects.requireNonNull(task.getException()).getMessage());
                        btnLogin.setVisibility(View.VISIBLE);
                        loginProgress.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private void showLoginMessage(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void updateUI()
    {
        startActivity(HomeActivity);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // User is already connected so we need to redirect him to the home page
            updateUI();
        }
    }
}