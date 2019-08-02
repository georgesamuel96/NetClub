package com.egcoders.technologysolution.netclub.data.interfaces;

import com.egcoders.technologysolution.netclub.model.profile.UserData;

public interface Register {

    interface View{
        void showMessage(String message);
    }

    interface Presenter{
        void setUser(UserData user, String imagePath);
    }
}
