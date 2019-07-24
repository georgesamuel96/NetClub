package com.egcoders.technologysolution.netclub.data;

import com.egcoders.technologysolution.netclub.model.UserData;

public interface Register {

    interface View{
        void showMessage(String message);
    }

    interface Presenter{
        void setUser(UserData user, String imagePath);
    }
}
