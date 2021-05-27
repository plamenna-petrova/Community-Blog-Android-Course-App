package com.example.communityblog.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.communityblog.Models.Comment;
import com.example.communityblog.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>
{

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData)
    {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position)
    {
        Glide.with(mContext).load(mData.get(position).getUserImg()).into(holder.imgUser);
        holder.username.setText(mData.get(position).getUserName());
        holder.commentContent.setText(mData.get(position).getContent());
        holder.commentTime.setText(timeStampToString((Long) mData.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgUser;
        TextView username;
        TextView commentContent;
        TextView commentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.comment_user_img);
            username = itemView.findViewById(R.id.comment_username);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentTime = itemView.findViewById(R.id.comment_date);
        }
    }

    public String timeStampToString(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        return DateFormat.format("hh:mm", calendar).toString();
    }
}
