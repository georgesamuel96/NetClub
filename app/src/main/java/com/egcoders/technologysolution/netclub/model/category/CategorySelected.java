package com.egcoders.technologysolution.netclub.model.category;

import java.util.List;

public class CategorySelected {

    private int status_code;
    private Boolean success;
    private String message;
    private List<List<CategorySelectedData>> data;

    public int getStatus_code() {
        return status_code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<List<CategorySelectedData>> getData() {
        return data;
    }
}
