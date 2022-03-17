package com.the_spartan.virtualdiary.service;

import android.content.Context;

import androidx.preference.Preference;

import com.the_spartan.virtualdiary.R;

public class PasswordService {

    private Context context;
    private Preference preference;

    public PasswordService(Context context, Preference preference) {
        this.context = context;
        this.preference = preference;
    }

    public String getPassword() {
        return preference.getSharedPreferences()
                .getString(context.getString(R.string.pref_key_password), "");
    }

    public void savePassword(String password) {
        preference.getSharedPreferences()
                .edit()
                .putString(context.getString(R.string.pref_key_password), password)
                .apply();
    }
}
