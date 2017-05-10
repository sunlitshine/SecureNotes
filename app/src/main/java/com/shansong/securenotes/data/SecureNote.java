package com.shansong.securenotes.data;

/**
 * Created by sunshine on 7/5/17.
 */

public final class SecureNote {

    private int mId;
    private String mTitle;
    private String mContent;
    private String mDateTime;
    private String mUserName;


    // constructors

    public SecureNote(){
        //empty constructor
    }

    public SecureNote(final int id, final String title, final String content, final String dateTime, final String
            userName){
        this.mId = id;
        this.mTitle = title;
        this.mContent = content;
        this.mDateTime = dateTime;
        this.mUserName = userName;
    }

    public SecureNote(final String title, final String content){
        this.mTitle = title;
        this.mContent = content;
    }


    //setters
    public void setId(final int id){
        this.mId = id;
    }

    public void setTitle(final String title){
        this.mTitle = title;
    }

    public void setContent(final String content){
        this.mContent = content;
    }

    public void setDateTime(final String dateTime){
        this.mDateTime = dateTime;
    }

    public void setUserName(final String userName){
        this.mUserName = userName;
    }


    //getters
    public int getId(){
        return this.mId;
    }

    public String getTitle(){
        return this.mTitle;
    }

    public String getContent(){
        return this.mContent;
    }

    public String getDateTime(){
        return this.mDateTime;
    }

    public String getUserName(){
        return this.mUserName;
    }
}

