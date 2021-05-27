package com.example.communityblog.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.communityblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class PostDetailsActivity extends AppCompatActivity {

    ImageView imgPost;
    ImageView imgUserPost;
    ImageView imgCurrentUser;
    TextView textPostDescending;
    TextView textPostDateName;
    TextView textPostTitle;
    EditText editTextComment;
    Button btnAddComment;

    String postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // let's set the status background to transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Objects.requireNonNull(getSupportActionBar()).hide();

        // ini Views
        imgPost = findViewById(R.id.post_details_img);
        imgUserPost = findViewById(R.id.post_details_user_image);
        imgCurrentUser = findViewById(R.id.post_details_current_user_img);

        textPostTitle = findViewById(R.id.post_details_title);
        textPostDescending = findViewById(R.id.post_details_descending);
        textPostDateName = findViewById(R.id.post_details_date_name);

        editTextComment = findViewById(R.id.post_details_comment);
        btnAddComment = findViewById(R.id.post_details_add_comment_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // now we need to bind all data into those views
        // first we need to get post data
        // we need to send post details data to this activity first...
        // now we can get post data

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        textPostTitle.setText(postTitle);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        textPostDescending.setText(postDescription);

        // set comment user image
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        // get post id
        postKey = getIntent().getExtras().getString("postKey");

        long date = getIntent().getExtras().getLong("postDetailsDate");
//        String date = timeStampToString(1622119044);

        textPostDateName.setText(timeStampToString(date));

    }

    private String timeStampToString(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String dateToFormat = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return dateToFormat;
    }
}