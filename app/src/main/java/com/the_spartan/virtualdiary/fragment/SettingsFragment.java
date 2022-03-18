package com.the_spartan.virtualdiary.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.adapter.FontSizeAdapter;
import com.the_spartan.virtualdiary.adapter.FontsAdapter;
import com.the_spartan.virtualdiary.service.NotificationService;
import com.the_spartan.virtualdiary.service.PasswordService;
import com.the_spartan.virtualdiary.validator.PasswordValidator;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;
import java.util.Arrays;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static SettingsFragment instance;

    private SettingsFragment() {
    }

    public static SettingsFragment getInstance() {
        if (instance == null) {
            instance = new SettingsFragment();
        }

        return instance;
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        registerListeners();
    }

    private void registerListeners() {
        findPreference(getString(R.string.pref_key_font))
                .setOnPreferenceClickListener(preference -> {
                    showFontChooserDialog(preference);

                    return true;
                });

        findPreference(getString(R.string.pref_key_font_size))
                .setOnPreferenceClickListener(preference -> {
                    showFontSizeChooserDialog(preference);

                    return true;
                });

        findPreference(getString(R.string.pref_key_color))
                .setOnPreferenceClickListener(preference -> {
                    showColorPickerDialog(preference);

                    return true;
                });

        findPreference(getString(R.string.pref_key_password))
                .setOnPreferenceClickListener(preference -> {
                    showPasswordSetterDialog(preference);

                    return true;
                });

        findPreference(getString(R.string.pref_key_about))
                .setOnPreferenceClickListener(preference -> {
                    showAboutDialog(preference);

                    return true;
                });

        findPreference(getString(R.string.pref_key_notification_switch))
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    handleNotificationPrefChange(preference, (boolean) newValue);

                    return true;
                });
    }

    private void showFontChooserDialog(Preference preference) {
        String[] fontPaths = getContext().getResources().getStringArray(R.array.font_values);
        String[] fontNames = getContext().getResources().getStringArray(R.array.font_names);
        ArrayList<String> fontTitles = new ArrayList<>(Arrays.asList(fontNames));

        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        CustomDialog fontChooserDialog = new CustomDialog(
                getContext(),
                viewGroup,
                R.layout.dialog,
                R.string.dialog_title_choose_font,
                -1,
                -1,
                R.string.dialog_btn_cancel,
                true
        );

        fontChooserDialog.setListAdapter(new FontsAdapter(getContext(), fontTitles));
        fontChooserDialog.getListView().setOnItemClickListener((adapterView, view, i, l) -> {
            SharedPreferences preferences = preference.getSharedPreferences();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.pref_key_font), fontPaths[i]);
            editor.apply();

            fontChooserDialog.dismiss();
        });
        fontChooserDialog.show();
    }

    private void showFontSizeChooserDialog(Preference preference) {
        String[] sizeString = getContext().getResources().getStringArray(R.array.font_size_array);
        String[] sizeValueString = getContext().getResources().getStringArray(R.array.font_size_values);
        ArrayList<String> size = new ArrayList<>(Arrays.asList(sizeString));

        ViewGroup viewGroup = getView().findViewById(R.id.content);
        CustomDialog fontSizeChooserDialog = new CustomDialog(
                getContext(),
                viewGroup,
                R.layout.dialog,
                R.string.dialog_title_choose_font,
                -1,
                -1,
                R.string.dialog_btn_cancel,
                true
        );

        fontSizeChooserDialog.setListAdapter(new FontSizeAdapter(getContext(), size));
        fontSizeChooserDialog.getListView().setOnItemClickListener((adapterView, view, i, l) -> {
            SharedPreferences preferences = preference.getSharedPreferences();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.pref_key_font_size), sizeValueString[i]);
            editor.apply();

            fontSizeChooserDialog.dismiss();
        });

        fontSizeChooserDialog.show();
    }

    private void showColorPickerDialog(Preference preference) {
        int mDefaultColor = preference.getSharedPreferences().getInt(getString(R.string.pref_key_color), 0);
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(getContext(), mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.pref_key_color), color);
                editor.apply();
            }
        });
        colorPicker.show();
    }

    private void showPasswordSetterDialog(Preference preference) {
        ViewGroup viewGroup = getView().findViewById(R.id.content);
        CustomDialog passwordSetterDialog = new CustomDialog(
                getContext(),
                viewGroup,
                R.layout.dialog_password_change,
                R.string.pref_title_set_change_password,
                -1,
                R.string.dialog_btn_ok,
                R.string.dialog_btn_cancel
        );

        PasswordService passwordService = new PasswordService(getContext(), preference);
        if (passwordService.getPassword().isEmpty()) {
            passwordSetterDialog.getOldPasswordTextInputLayout().setVisibility(View.GONE);
        }

        passwordSetterDialog.posBtn.setOnClickListener(view -> {
            boolean valid = PasswordValidator.validate(
                    getContext(),
                    preference,
                    passwordSetterDialog.getEdOldPassword(),
                    passwordSetterDialog.getEdNewPassword(),
                    passwordSetterDialog.getEdConfirmPassword()
            );

            if (valid) {
                String passwordToSave = passwordSetterDialog.getEdNewPassword().getText().toString();
                passwordService.savePassword(passwordToSave);

                passwordSetterDialog.dismiss();
            }
        });

        passwordSetterDialog.show();
    }

    private void handleNotificationPrefChange(Preference preference, boolean enabled) {
        Intent notificationIntent = new Intent(getContext(), NotificationService.class);

        if (enabled) {
            getContext().startService(notificationIntent);
        } else {
            getContext().stopService(notificationIntent);
        }
    }

    private void showAboutDialog(Preference preference) {
        ViewGroup viewGroup = getView().findViewById(R.id.content);
        CustomDialog aboutDialog = new CustomDialog(
                getContext(),
                viewGroup,
                R.layout.dialog,
                R.string.dialog_title_about,
                R.string.dialog_msg_about,
                R.string.dialog_btn_rate_this_app,
                R.string.dialog_btn_cancel
        );

        aboutDialog.posBtn.setOnClickListener(view -> {
            Toast.makeText(getContext(), "App will be redirected to playstore", Toast.LENGTH_SHORT).show();
            aboutDialog.dismiss();
        });
        aboutDialog.show();
    }
}
