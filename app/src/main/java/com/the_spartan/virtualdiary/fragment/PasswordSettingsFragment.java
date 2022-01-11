package com.the_spartan.virtualdiary.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.MainActivity;

public class PasswordSettingsFragment extends Fragment {

    EditText setPasswordEditText;
    EditText confirmPasswordEditText;
    EditText oldPasswordEditText;
    TextView setPasswordTextView;
    TextView setOrChangePassTextView;
    TextView confirmPasswordTextView;
    View lowerDivider;
    Button saveButton;
    Switch passwordSwitch;
    LinearLayout oldPasswordLinearLayout;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String oldPassword;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_settings, container, false);

        setPasswordEditText = view.findViewById(R.id.set_password_edittext);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edittext);
        saveButton = view.findViewById(R.id.save_password_button);

        oldPasswordEditText = view.findViewById(R.id.old_password_edittext);
        setPasswordTextView = view.findViewById(R.id.set_password_textview);
        setOrChangePassTextView = view.findViewById(R.id.set_change_pass_tv);
        confirmPasswordTextView = view.findViewById(R.id.confirm_password_tv);
        lowerDivider = view.findViewById(R.id.lower_divider);

        oldPasswordLinearLayout = view.findViewById(R.id.old_password_linear_layout);
        passwordSwitch = view.findViewById(R.id.password_switch);

        toolbar = view.findViewById(R.id.my_toolbar);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.setToolbar(toolbar);
        mainActivity.setTitle("");

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        oldPassword = preferences.getString("pass", null);
        boolean isPasswordOn = preferences.getBoolean("isPasswordOn", false);

        if (isPasswordOn)
            passwordSwitch.setChecked(true);
        else
            passwordSwitch.setChecked(false);

        if (oldPassword != null) {
            setOrChangePassTextView.setText("Change PIN");
        }

        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editor.putBoolean("isPasswordOn", true);
                } else {
                    editor.putBoolean("isPasswordOn", false);
                }
                editor.apply();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldPasswordInput = oldPasswordEditText.getText().toString();
                String savedOldPassword = preferences.getString("pass", null);
                String pass1 = setPasswordEditText.getText().toString();
                String pass2 = confirmPasswordEditText.getText().toString();

                if (savedOldPassword == null)
                    savedOldPassword = "null";

                if (pass1 == null || pass2 == null) {
                    Toast.makeText(getContext(), R.string.toast_field_empty, Toast.LENGTH_SHORT).show();
                } else if (pass1.length() < 4 || pass2.length() < 4) {
                    Toast.makeText(getContext(), R.string.toast_four_digit, Toast.LENGTH_SHORT).show();
                } else if (oldPassword == null) {
                    if (pass1.equals(pass2)) {
                        editor.putString("pass", pass1);
                        editor.apply();
                        Toast.makeText(getContext(), R.string.toast_pin_saved, Toast.LENGTH_SHORT).show();
//                        finish();
                    } else {
                        Toast.makeText(getContext(), R.string.toast_pin_mismatch, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (oldPasswordInput == null) {
                        Toast.makeText(getContext(), R.string.toast_old_pin_empty, Toast.LENGTH_SHORT).show();
                    } else if (!savedOldPassword.equals(oldPasswordInput))
                        Toast.makeText(getContext(), R.string.toast_incorrect_old_pin, Toast.LENGTH_SHORT).show();
                    else if (!pass1.equals(pass2))
                        Toast.makeText(getContext(), R.string.toast_pin_mismatch, Toast.LENGTH_SHORT).show();
                    else {
                        editor.putString("pass", pass1);
                        editor.apply();
                        Toast.makeText(getContext(), R.string.toast_pin_saved, Toast.LENGTH_SHORT).show();

                        setPasswordTextView.setVisibility(View.INVISIBLE);
                        setPasswordEditText.setVisibility(View.INVISIBLE);
                        confirmPasswordEditText.setVisibility(View.INVISIBLE);
                        confirmPasswordTextView.setVisibility(View.INVISIBLE);
                        saveButton.setVisibility(View.INVISIBLE);
                        oldPasswordLinearLayout.setVisibility(View.INVISIBLE);
                        lowerDivider.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        setOrChangePassTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPasswordTextView.setVisibility(View.VISIBLE);
                setPasswordEditText.setVisibility(View.VISIBLE);
                confirmPasswordEditText.setVisibility(View.VISIBLE);
                confirmPasswordTextView.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                lowerDivider.setVisibility(View.INVISIBLE);

                if (oldPassword != null) {
                    oldPasswordLinearLayout.setVisibility(View.VISIBLE);
                    setPasswordTextView.setText("New PIN");
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            try {
                String pass = null;
                pass = setPasswordEditText.getText().toString();

                if (pass.length() == 0 && oldPassword == null) {
                    editor.putBoolean("isPasswordOn", false);
                    editor.apply();

                    passwordSwitch.setChecked(false);
                }

            } finally {
//                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
