package com.example.communityblog.Activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.communityblog.Fragments.HomeFragment;
import com.example.communityblog.Fragments.ProfileFragment;
import com.example.communityblog.Fragments.SettingsFragment;
import com.example.communityblog.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popupAddPost;

    ImageView popupUserImage;
    ImageView popupPostImage;
    ImageView popupAddBtn;
    TextView popupTitle;
    TextView popupDescription;
    ProgressBar popupClickProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    }

    private void iniPopup() {
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

        Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(popupUserImage);


        // Add post click Listener

        popupAddBtn.setOnClickListener(view -> {
            popupAddBtn.setVisibility(View.INVISIBLE);
            popupClickProgress.setVisibility(View.VISIBLE);
        });
    }

    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(view -> {
            // here when the image is clicked we need to open the gallery
            // before we open the gallery we need to check if our app has the access to user files
            // made before in register activity

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public boolean onNavigationItemSelected(MenuItem item) {
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

    public void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        // now we will use Glide to load user image
        // first we need to import the library

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);

    }
}