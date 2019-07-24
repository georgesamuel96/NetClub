package com.egcoders.technologysolution.netclub.model;

public class Comment {

    private String userId;
    private String userName;
    private String userProfile;
    private String userProfileThumb;
    private String timeStamp;
    private String content;
    private String userStatue;

    public Comment(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserProfileThumb() {
        return userProfileThumb;
    }

    public void setUserProfileThumb(String userProfileThumb) {
        this.userProfileThumb = userProfileThumb;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserStatue() {
        return userStatue;
    }

    public void setUserStatue(String userStatue) {
        this.userStatue = userStatue;
    }
}
