package com.example.communityblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.communityblog.Activities.PostDetailsActivity;
import com.example.communityblog.Models.Post;
import com.example.communityblog.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>
{
    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData)
    {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.postTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);

        String userImg = mData.get(position).getUserPhoto();
        if (userImg != null)
        {
            Glide.with(mContext).load(userImg).into(holder.imgPostProfile);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.userphoto).into(holder.imgPostProfile);
        }

        holder.postUsername.setText(mData.get(position).getUserName());
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView postTitle;
        ImageView imgPost;
        ImageView imgPostProfile;
        TextView postUsername;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            postTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_image);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            postUsername = itemView.findViewById(R.id.row_post_username);

            itemView.setOnClickListener(view -> {
                Intent postDetailsActivityIntent = new Intent(mContext, PostDetailsActivity.class);
                int position = getAdapterPosition();
                postDetailsActivityIntent.putExtra("title", mData.get(position).getTitle());
                postDetailsActivityIntent.putExtra("postImage", mData.get(position).getPicture());
                postDetailsActivityIntent.putExtra("description", mData.get(position).getDescription());
                postDetailsActivityIntent.putExtra("postKey", mData.get(position).getPostKey());
                postDetailsActivityIntent.putExtra("userPhoto", mData.get(position).getUserPhoto());
                postDetailsActivityIntent.putExtra("userName", mData.get(position).getUserName());
                // user name not added to post object
                // postDetailsActivityIntent.putExtra("userName", mData.get(position).getUsername());
                long timestamp = (long) mData.get(position).getTimeStamp();
                postDetailsActivityIntent.putExtra("postDetailsDate", timestamp);
                mContext.startActivity(postDetailsActivityIntent);
            });
        }
    }
}
