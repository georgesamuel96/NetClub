package com.egcoders.technologysolution.netclub.model;

public class PostResponse {

    private int status_code;
    private Boolean success;
    private String message;
    private PostData data;

    public int getStatus_code() {
        return status_code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public PostData getData() {
        return data;
    }
}
