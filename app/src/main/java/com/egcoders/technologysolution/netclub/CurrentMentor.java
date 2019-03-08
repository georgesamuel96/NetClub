package com.egcoders.technologysolution.netclub;

import android.util.Pair;

import java.util.ArrayList;

public class CurrentMentor {

    private static String name;
    private static String content;
    private static String image_url;
    private static String imageThumb_url;
    private static String description;
    private static String email;
    private static String phone;
    private static ArrayList<String> dates = new ArrayList<>();
    private static ArrayList<Pair<String, String>> categories = new ArrayList<>();

    public CurrentMentor(){

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
