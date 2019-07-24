package com.egcoders.technologysolution.netclub.model;

public class ForgetPasswordResponse {

    private int status_code;
    private Boolean success;
    private String message;
    private UserData data;

    public int getStatus_code() {
        return status_code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserData getData() {
        return data;
    }
}
