package com.example.communityblog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.communityblog.Fragments.HomeFragment;
import com.example.communityblog.Fragments.ProfileFragment;
import com.example.communityblog.Fragments.SettingsFragment;
import com.example.communityblog.Models.Post;
import com.example.communityblog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private static final int PReqCode = 2;
    private static final int REQUESTCODE = 2;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popupAddPost;

    ImageView popupUserImage;
    ImageView popupPostImage;
    ImageView popupAddBtn;
    TextView popupTitle;
    TextView popupDescription;
    ProgressBar popupClickProgress;

    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ini

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // ini popup
        iniPopup();
        setupPopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->
            popupAddPost.show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        // set the home fragment as the default done

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

    private void setupPopupImageClick()
    {
        popupPostImage.setOnClickListener(view -> {
            // here when image clicked we need to open the gallery
            // before we open the gallery we need to check if our app have the access to user files
            // we did this before in register activity I'm just going to copy the code to save time ...
            checkAndRequestForPermission();
        });
    }

    private void checkAndRequestForPermission()
    {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(HomeActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();
    }

    private void openGallery()
    {
        //open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null ) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostImage.setImageURI(pickedImgUri);
        }
    }

    private void iniPopup()
    {
        popupAddPost = new Dialog(this);
        popupAddPost.setContentView(R.layout.popup_add_post);
        popupAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        popupUserImage = popupAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popupAddPost.findViewById(R.id.popup_img);
        popupTitle = popupAddPost.findViewById(R.id.popup_title);
        popupDescription = popupAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popupAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popupAddPost.findViewById(R.id.popup_progressBar);

        // load current user profile photo
        if (currentUser.getPhotoUrl() != null)
        {
            Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(popupUserImage);
        }
        else
        {
            Glide.with(HomeActivity.this).load(R.drawable.userphoto).into(popupUserImage);
        }

        // Add post click Listener
        popupAddBtn.setOnClickListener(view -> {

            popupAddBtn.setVisibility(View.INVISIBLE);
            popupClickProgress.setVisibility(View.VISIBLE);

            // we need to test all input fields (Title and description) and post image

            if (!popupTitle.getText().toString().isEmpty() && !popupDescription.getText().toString().isEmpty() && pickedImgUri != null) {
                //everything is okay, no empty or null values
                // Creating Post Object and adding it to Firebase Realtime Database
                // first we need to upload the post image
                // access firebase storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("community_blog_images");
                StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());

                imageFilePath.putFile(pickedImgUri).addOnSuccessListener(taskSnapshot -> imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageDownloadLink = uri.toString();
                    // create post Object

                    if (currentUser.getPhotoUrl() != null)
                    {
                        Post post = new Post(popupTitle.getText().toString(), popupDescription.getText().toString(), imageDownloadLink, currentUser.getUid(), currentUser.getPhotoUrl().toString());
                        // Add post to Firebase Database
                        addPost(post);
                    }
                    else
                    {
                        Post post = new Post(popupTitle.getText().toString(), popupDescription.getText().toString(), imageDownloadLink, currentUser.getUid(), null);
                        // Add post to Firebase Database
                        addPost(post);
                    }

                }).addOnFailureListener(exception -> {
                    // something goes wrong when uploading the picture
                    showMessage(exception.getMessage());
                    popupClickProgress.setVisibility(View.INVISIBLE);
                    popupAddBtn.setVisibility(View.VISIBLE);
                }));
            }
            else
            {
                showMessage("Please verify all input fields and choose Post Image") ;
                popupAddBtn.setVisibility(View.VISIBLE);
                popupClickProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void addPost(Post post)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://communityblog-e2892-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID and update post key
        String key = myRef.getKey();
        post.setPostKey(key);

        // add post data to firebase database
        myRef.setValue(post).addOnSuccessListener(aVoid -> {
            showMessage("Post Added successfully");
            popupClickProgress.setVisibility(View.INVISIBLE);
            popupAddBtn.setVisibility(View.VISIBLE);
            popupAddPost.dismiss();
        });
    }

    private void showMessage(String message)
    {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return  super.onOptionsItemSelected(item);
    }

    @SuppressWarnings({"StatementWithEmptyBody", "RedundantSuppression"})
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        }
        else if (id == R.id.nav_profile) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
        }
        else if (id == R.id.nav_settings) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
        }
        else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        // now we will use Glide to load user image
        // first we need to import the library

        if (currentUser.getPhotoUrl() != null)
        {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
        }
        else
        {
            Glide.with(this).load(R.drawable.userphoto).into(navUserPhoto);
        }

    }
}