package com.egcoders.technologysolution.netclub.model.profile;

public class UserData {

    private String name;
    private String email;
    private String birth_date;
    private String phone;
    private String photo_max;
    private String verifyCode;
    private String updated_at;
    private String created_at;
    private int id;
    private String userStatus;
    private Boolean selectedCategory;
    private String token;
    private String activate;
    private String password;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto_max() {
        return photo_max;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getId() {
        return id;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public Boolean getSelectedCategory() {
        return selectedCategory;
    }

    public String getToken() {
        return token;
    }

    public String getActivate() {
        return activate;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoto_max(String photo_max) {
        this.photo_max = photo_max;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setSelectedCategory(Boolean selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
