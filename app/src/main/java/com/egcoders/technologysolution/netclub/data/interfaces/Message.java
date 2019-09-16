package com.egcoders.technologysolution.netclub.data.interfaces;

import com.egcoders.technologysolution.netclub.model.profile.UserData;

public interface Message {
    void successMessage(UserData data);
    void failMessage();
}
