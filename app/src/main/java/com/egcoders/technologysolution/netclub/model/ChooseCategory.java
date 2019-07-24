package com.egcoders.technologysolution.netclub.model;

public class ChooseCategory {

    private int categoryId;
    private String categoryName;
    private Boolean categoryChecked;

    public ChooseCategory(){

    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
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
