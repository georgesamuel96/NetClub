package com.egcoders.technologysolution.netclub.data.interfaces;

public interface Login {

    interface View{

    }

    interface Presenter {

        void loginUser(String email, String password);
    }
}
