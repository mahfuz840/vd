package com.the_spartan.virtualdiary.util;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GoogleSignin {

    private static GoogleSignin mInstance;
    private static GoogleSignInAccount signInAccount;

    public static GoogleSignin getInstance(){
        if(mInstance == null){
            mInstance = new GoogleSignin();
            return mInstance;
        } else{
            return mInstance;
        }
    }

    public static void setAccount(GoogleSignInAccount account){
        signInAccount = account;
    }

    public static GoogleSignInAccount getAccount(){
        return signInAccount;
    }
}
