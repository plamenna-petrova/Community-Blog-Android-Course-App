package com.example.communityblog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communityblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;

    private EditText userEmail;
    private EditText userPassword;
    private EditText userPasswordConfirmation;
    private EditText userName;
    private ProgressBar loadingProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ini views
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPasswordConfirmation = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(view -> {
            regBtn.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);
            final String email = userEmail.getText().toString();
            final String password = userPassword.getText().toString();
            final String passwordToConfirm = userPasswordConfirmation.getText().toString();
            final String name = userName.getText().toString();

            if ( email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(passwordToConfirm) ) {
                // something goes wrong : all fields must be filled
                // we need to display an error message
                showMessage("Please Verify all fields");
                regBtn.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.INVISIBLE);
            }
            else
            {
                // everything is ok and all fields are filled now we can start creating user account
                // CreateUserAccount method will try to create the user if the email is valid
                CreateUserAccount(email, name, password);
            }
        });

        ImgUserPhoto = findViewById(R.id.regUserPhoto);

        ImgUserPhoto.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission();
            }
            else
            {
                openGallery();
            }
        });
    }

    private void CreateUserAccount(String email, String name, String password) {
        // this method creates user account with specific email and password

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // user account created successfully
                        showMessage("Account created");
                        // after we created user account we need to update the profile picture and name
                        updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());
                    }
                    else
                    {
                        // failed to create an account
                        showMessage("Failed to create an account" + Objects.requireNonNull(task.getException()).getMessage());
                        regBtn.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.INVISIBLE);
                    }
                });

    }

    // update user photo and name
    private void updateUserInfo(String name, Uri pickedImgUri, FirebaseUser currentUser) {
        // first we need to upload the user photo to the Firebase storage and get the url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(taskSnapshot -> {
            // image uploaded successfully
            // now we can get our image url

            imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                // uri contains the user image url
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                        setDisplayName(name)
                        .setPhotoUri(uri)
                        .build();

                currentUser.updateProfile(profileUpdate)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // the user info is updated successfully
                                showMessage("The registration is completed");
                                updateUI();
                            }
                        });

            });

        });

    }

    // simple method to show toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        // TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        }
        else
        {
            openGallery();
        }
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            // the user has successfully picked an image
            // we need to save it's reference to an Uri variable
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), Home.class);
        startActivity(homeActivity);
        finish();
    }

}