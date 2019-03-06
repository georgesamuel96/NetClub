package com.example.georgesamuel.netclub;

public class ChooseCategory {

    private String categoryId;
    private String categoryName;
    private Boolean categoryChecked;

    public ChooseCategory(){

    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getcategoryChecked() {
        return categoryChecked;
    }

    public void setcategoryChecked(Boolean categoryChecked) {
        this.categoryChecked = categoryChecked;
    }
}
