package com.egcoders.technologysolutions.netclub;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SaveUserInstance {

    private static Boolean isFirstLoad = true;
    private static DocumentSnapshot documentSnapshot;
    private static ArrayList<User> list = new ArrayList<>();
    private static String name;
    private static String email;
    private static String birthday;
    private static String profile_url;
    private static String profileThumb_url;
    private static String phone;
    private static String id;
    private static Boolean categorySelected;

    public SaveUserInstance(){

    }

    public Boolean getIsFirstLoad() {
        return isFirstLoad;
    }

    public void setIsFirstLoad(Boolean isFirstLoad) {
        SaveUserInstance.isFirstLoad = isFirstLoad;
    }

    public DocumentSnapshot getDocumentSnapshot() {
        return documentSnapshot;
    }

    public void setDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        SaveUserInstance.documentSnapshot = documentSnapshot;
    }

    public ArrayList<User> getList() {
        return list;
    }

    public void setList(ArrayList<User> list) {
        SaveUserInstance.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        SaveUserInstance.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        SaveUserInstance.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        SaveUserInstance.birthday = birthday;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        SaveUserInstance.profile_url = profile_url;
    }

    public String getProfileThumb_url() {
        return profileThumb_url;
    }

    public void setProfileThumb_url(String profileThumb_url) {
        SaveUserInstance.profileThumb_url = profileThumb_url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        SaveUserInstance.phone = phone;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        SaveUserInstance.id = id;
    }

    public Boolean getCategorySelected(){
        return categorySelected;
    }

    public void setCategorySelected(Boolean categorySelected){
        SaveUserInstance.categorySelected = categorySelected;
    }
}
