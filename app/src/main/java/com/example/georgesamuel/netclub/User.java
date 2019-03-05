package com.example.georgesamuel.netclub;

public class User {

    private String userName;
    private String userImageUrl;
    private String userImageThumbUrl;

    public User(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserImageThumbUrl() {
        return userImageThumbUrl;
    }

    public void setUserImageThumbUrl(String userImageThumbUrl) {
        this.userImageThumbUrl = userImageThumbUrl;
    }
}
