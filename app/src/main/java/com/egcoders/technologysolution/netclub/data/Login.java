package com.egcoders.technologysolution.netclub.data;

public interface Login {

    interface View{

    }

    interface Presenter {

        void loginUser(String email, String password);
    }
}
