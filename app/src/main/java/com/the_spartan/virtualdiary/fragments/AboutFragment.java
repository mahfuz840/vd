package com.the_spartan.virtualdiary.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activities.MainActivity;


public class AboutFragment extends Fragment {

    public Toolbar toolbar;
    private final String TAG = "About Activity";

    public AboutFragment() {

    }

    static Fragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        toolbar = view.findViewById(R.id.my_toolbar);

        ((MainActivity)getActivity()).setToolbar(toolbar);

        ActionBar bar = ((MainActivity)getActivity()).getSupportActionBar();

        if(bar == null)
            Log.d("Toolbar", "null");

        ((MainActivity)getActivity()).setTitle("");

        setHasOptionsMenu(true);
        Log.d(TAG, "onCreateView");


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

        Log.d(TAG, "onCreateOptionsMenu");

    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }
}
