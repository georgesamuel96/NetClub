package com.example.georgesamuel.netclub;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Mentor implements Serializable {

    private static final long serialVersionUID = 10L;
    private String id;
    private String name;
    private String content;
    private String image_url;
    private String imageThumb_url;
    private String description;
    private String email;
    private String phone;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<Pair<String, String>> categories = new ArrayList<>();

    public Mentor(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImageThumb_url() {
        return imageThumb_url;
    }

    public void setImageThumb_url(String imageThumb_url) {
        this.imageThumb_url = imageThumb_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }

    public ArrayList<Pair<String, String>> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Pair<String, String>> categories) {
        this.categories = categories;
    }
}
