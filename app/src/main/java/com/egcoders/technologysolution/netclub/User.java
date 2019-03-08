package com.egcoders.technologysolution.netclub;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 10L;
    private String userName;
    private String userImageUrl;
    private String userImageThumbUrl;
    private String userPhone;
    private String userBirthday;
    private Boolean userSelectCategories;
    private String userEmail;

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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public Boolean getUserSelectCategories() {
        return userSelectCategories;
    }

    public void setUserSelectCategories(Boolean userSelectCategories) {
        this.userSelectCategories = userSelectCategories;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
