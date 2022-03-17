package com.the_spartan.virtualdiary.validator;

import android.content.Context;
import android.widget.EditText;

import androidx.preference.Preference;

import com.the_spartan.virtualdiary.service.PasswordService;

public class PasswordValidator {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static boolean validate(Context context,
                                   Preference preference,
                                   EditText edOldPassword,
                                   EditText edNewPassword,
                                   EditText edConfirmPassword) {

        PasswordService passwordService = new PasswordService(context, preference);
        String oldPassword = passwordService.getPassword();

        String oldPasswordInp = edOldPassword.getText().toString();
        String newPasswordInp = edNewPassword.getText().toString();
        String confirmPasswordInp = edConfirmPassword.getText().toString();

        if (!oldPassword.isEmpty() && oldPasswordInp.isEmpty()) {
            edOldPassword.setError("Please enter the old password first");

            return false;
        }

        if (newPasswordInp.isEmpty()) {
            edNewPassword.setError("Please enter a password");

            return false;
        }

        if (confirmPasswordInp.isEmpty()) {
            edConfirmPassword.setError("Please confirm the password");

            return false;
        }

        if (!oldPassword.isEmpty() && !oldPasswordInp.equals(oldPassword)) {
            edOldPassword.setError("Incorrect old password");

            return false;
        }

        if (!oldPassword.isEmpty() && oldPasswordInp.equals(newPasswordInp)) {
            edNewPassword.setError("New password can't be same as old password");

            return false;
        }

        if (newPasswordInp.contains(" ")) {
            edNewPassword.setError("Password can't contain space");

            return false;
        }

        if (newPasswordInp.length() < PASSWORD_MIN_LENGTH) {
            edNewPassword.setError("Password should be at least 4 digits long");

            return false;
        }

        if (!newPasswordInp.equals(confirmPasswordInp)) {
            edConfirmPassword.setError("Passwords don't match");

            return false;
        }

        return true;
    }
}
