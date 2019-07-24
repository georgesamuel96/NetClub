package com.egcoders.technologysolution.netclub.model;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("title")
    private String category;

    @SerializedName("description")
    private String content;

    private String likeNumber;

    @SerializedName("photo")
    private String photoUrl;

    private String photoThumbUrl;
    private Long timeStamp;

    private String userId;
    private String userName;
    private String userProfileUrl;
    private String userProfileThumbUrl;
    private String userStatue;
    private String postId;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private UserData userData;



    public Post(){

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(String likeNumber) {
        this.likeNumber = likeNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoThumbUrl() {
        return photoThumbUrl;
    }

    public void setPhotoThumbUrl(String photoThumbUrl) {
        this.photoThumbUrl = photoThumbUrl;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
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

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    public String getUserProfileThumbUrl() {
        return userProfileThumbUrl;
    }

    public void setUserProfileThumbUrl(String userProfileThumbUrl) {
        this.userProfileThumbUrl = userProfileThumbUrl;
    }

    public String getUserStatue() {
        return userStatue;
    }

    public void setUserStatue(String userStatue) {
        this.userStatue = userStatue;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
