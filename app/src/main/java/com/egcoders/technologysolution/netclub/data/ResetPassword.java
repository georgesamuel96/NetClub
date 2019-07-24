package com.egcoders.technologysolution.netclub.data;

public interface ResetPassword {

    interface View{

        void showMessage();
    }

    interface Presenter{

        void sendCode(String email);
        void verifyCode(String code, String password);
    }
}
