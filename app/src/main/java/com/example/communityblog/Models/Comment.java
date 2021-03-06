package com.example.communityblog.Models;

import com.google.firebase.database.ServerValue;

public class Comment
{

    private String content;
    private String userId;
    private String userImg;
    private String userName;

    private int defaultPhoto;

    private Object timestamp;

    public Comment()
    {

    }

    public Comment(String content, String userId, String userImg, String userName)
    {
        this.content = content;
        this.userId = userId;
        this.userImg = userImg;
        this.userName = userName;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Comment(String content, String userId, String userImg, String userName, Object timestamp)
    {
        this.content = content;
        this.userId = userId;
        this.userImg = userImg;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserImg()
    {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Object getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Object timestamp)
    {
        this.timestamp = timestamp;
    }

}
