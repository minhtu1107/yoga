package com.tregix.serviceprovider.Interface;

import com.tregix.serviceprovider.Model.Login.User;

/**
 * Created by Tregix on 12/24/2017.
 */

public interface OnSignupLoginListener  {
    void onSignup(User data);
    void onLoginUser(User data);
    void OnError(String error);
}
