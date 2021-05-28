package com.example.communityblog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.communityblog.Adapters.CommentAdapter;
import com.example.communityblog.Models.Comment;
import com.example.communityblog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PostDetailsActivity extends AppCompatActivity {

    ImageView imgPost;
    ImageView imgUserPost;
    ImageView imgCurrentUser;
    TextView textPostDescription;
    TextView textPostDateName;
    TextView textPostTitle;
    TextView textPostUsername;
    EditText editTextComment;
    Button btnAddComment;

    String postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;

    RecyclerView recyclerViewComments;
    CommentAdapter commentAdapter;
    List<Comment> commentsList;

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
        textPostDescription = findViewById(R.id.post_details_description);
        textPostDateName = findViewById(R.id.post_details_date_name);
        textPostUsername = findViewById(R.id.post_details_username);

        editTextComment = findViewById(R.id.post_details_comment);
        btnAddComment = findViewById(R.id.post_details_add_comment_btn);

        // recycler view for comments
        recyclerViewComments = findViewById(R.id.recycler_view_comments);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://communityblog-e2892-default-rtdb.europe-west1.firebasedatabase.app/");

        // add Comment button listener
        btnAddComment.setOnClickListener(v -> {

            btnAddComment.setVisibility(View.INVISIBLE);

            DatabaseReference commentReference = firebaseDatabase.getReference("Comments").child(postKey).push();
            String commentContent = editTextComment.getText().toString();
            String uId = firebaseUser.getUid();
            String username = firebaseUser.getDisplayName();
            String uImg = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
            Comment comment = new Comment(commentContent, uId, uImg, username);
                commentReference.setValue(comment).addOnSuccessListener(aVoid -> {
                    showMessage("Comment added");
                    editTextComment.setText("");
                    btnAddComment.setVisibility(View.VISIBLE);
                }).addOnFailureListener(exception -> showMessage("Failed to add comment" +exception.getMessage()));
        });

        // now we need to bind all data into those views
        // first we need to get post data
        // we need to send post details data to this activity first...
        // now we can get post data

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        textPostTitle.setText(postTitle);

        String userPostImage = getIntent().getExtras().getString("userPhoto");

        if (userPostImage != null)
        {
            Glide.with(this).load(userPostImage).into(imgUserPost);
        }
        else
        {
            Glide.with(this).load(R.drawable.userphoto).into(imgUserPost);
        }

        String postDescription = getIntent().getExtras().getString("description");
        textPostDescription.setText(postDescription);

        // set comment user image
        if (firebaseUser.getPhotoUrl() != null)
        {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);
        }
        else
        {
            Glide.with(this).load(R.drawable.userphoto).into(imgCurrentUser);
        }

        // get post id
        postKey = getIntent().getExtras().getString("postKey");

        long date = getIntent().getExtras().getLong("postDetailsDate");
        textPostDateName.setText(timeStampToString(date));

        String postUsername = getIntent().getExtras().getString("userName");
        textPostUsername.setText(postUsername);

        // ini RecyclerView Comments
        iniRecyclerViewComments();
    }

    private void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String timeStampToString(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        return DateFormat.format("dd-MM-yyyy",calendar).toString();
    }

    private void iniRecyclerViewComments()
    {
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase = FirebaseDatabase.getInstance("https://communityblog-e2892-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference recyclerViewCommentsReference = firebaseDatabase.getReference("Comments").child(postKey);
        recyclerViewCommentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList = new ArrayList<>();
                for (DataSnapshot userCommentSnapshot: dataSnapshot.getChildren())
                {
                    Comment userComment = userCommentSnapshot.getValue(Comment.class);
                    commentsList.add(userComment);
                }
                commentAdapter = new CommentAdapter(getApplicationContext(), commentsList);
                recyclerViewComments.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}