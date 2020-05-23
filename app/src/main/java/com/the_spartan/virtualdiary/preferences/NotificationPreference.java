package com.the_spartan.virtualdiary.preferences;

import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.service.NotificationService;

public class NotificationPreference extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.notification_preference);

        findPreference("notification_switch_value").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((boolean)newValue){
                    Intent startServiceIntent = new Intent(getContext(), NotificationService.class);
                    getContext().startService(startServiceIntent);
                } else {
                    Intent stopServiceIntent = new Intent(getContext(), NotificationService.class);
                    getContext().stopService(stopServiceIntent);
                }
                return true;
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
