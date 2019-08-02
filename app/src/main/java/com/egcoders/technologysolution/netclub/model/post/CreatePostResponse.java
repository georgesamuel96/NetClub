package com.egcoders.technologysolution.netclub.model.post;

public class CreatePostResponse {

    private int status_code;
    private Boolean success;
    private String message;
    private Post data;

    public int getStatus_code() {
        return status_code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Post getData() {
        return data;
    }
}
