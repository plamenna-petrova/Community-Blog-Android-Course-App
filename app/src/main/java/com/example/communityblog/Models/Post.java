package com.example.communityblog.Models;

import com.google.firebase.database.ServerValue;

public class Post {

    private String title;
    private String description;
    private String picture;
    private String userId;
    private String userPhoto;
    private String userName;
    private Object timeStamp;
    private String postKey;

    public Post(String title, String description, String picture, String userId, String userPhoto, String userName)
    {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Post()
    {

    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPicture()
    {
        return picture;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserPhoto()
    {
        return userPhoto;
    }

    public String getUserName() { return userName; }

    public Object getTimeStamp()
    {
        return timeStamp;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto)
    {
        this.userPhoto = userPhoto;
    }

    public void setUserName(String userName) { this.userName = userName; }

    public void setTimeStamp(Object timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public void setPostKey(String postKey)
    {
        this.postKey = postKey;
    }
}
