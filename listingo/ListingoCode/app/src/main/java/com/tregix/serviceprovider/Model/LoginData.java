package com.tregix.serviceprovider.Model;

/**
 * Created by Tregix on 12/28/2017.
 */

public class LoginData {

    String username;
    String password;
    boolean googleLogin;

    public LoginData(String foo, String bar) {
        this.username = foo;
        this.password = bar;
    }

    public void setGoogleLogin(boolean googleLogin) {
        this.googleLogin = googleLogin;
    }
}
