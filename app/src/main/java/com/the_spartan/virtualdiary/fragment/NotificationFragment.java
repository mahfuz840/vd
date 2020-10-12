package com.the_spartan.virtualdiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.MainActivity;
import com.the_spartan.virtualdiary.preferences.NotificationPreference;

public class NotificationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.setToolbar(toolbar);
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
//        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.notification_fragment_container, new NotificationPreference()).commit();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
//            finish();
        }

        return true;
    }
}
