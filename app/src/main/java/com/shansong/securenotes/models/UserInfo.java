package com.shansong.securenotes.models;


/**
 * Created by sunshine on 7/5/17.
 */

public class UserInfo {
    private String mUserName;
    private String mPassword;
    private boolean mIsLoggedIn;


    // constructors

    public UserInfo(){
        //empty constructor
    }

    public UserInfo(final String userName, final String password, final boolean isLoggedIn){
        this.mUserName = userName;
        this.mPassword = password;
        this.mIsLoggedIn = isLoggedIn;

    }

    //setters
    public void setUserName(final String userName){
        this.mUserName = userName;
    }

    public void setPassword(final String password){
        this.mPassword = password;
    }

    public void setIsLoggedIn(final boolean isLoggedIn){
        this.mIsLoggedIn = isLoggedIn;
    }

    //getters
    public String getUserName(){
        return this.mUserName;
    }

    public String getPassword(){
        return this.mPassword;
    }

    public boolean isIsLoggedIn(){
        return  this.mIsLoggedIn;
    }
}
