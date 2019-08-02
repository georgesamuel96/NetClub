package com.egcoders.technologysolution.netclub.data.interfaces;

public interface ResetPassword {

    interface View{

        void showMessage();
    }

    interface Presenter{

        void sendCode(String email);
        void verifyCode(String code, String password);
    }
}
