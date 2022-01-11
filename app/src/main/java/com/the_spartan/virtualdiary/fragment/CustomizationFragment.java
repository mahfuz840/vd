package com.the_spartan.virtualdiary.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.onegravity.colorpicker.ColorPickerDialog;
import com.onegravity.colorpicker.ColorPickerListener;
import com.onegravity.colorpicker.SetColorPickerListenerEvent;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.MainActivity;
import com.the_spartan.virtualdiary.adapter.FontSizeAdapter;
import com.the_spartan.virtualdiary.adapter.FontsAdapter;
import com.the_spartan.virtualdiary.adapter.SettingsAdapter;

import java.util.ArrayList;

public class CustomizationFragment extends Fragment {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ListView listView;
    Toolbar toolbar;
    SettingsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customization, container, false);
        listView = view.findViewById(R.id.settings_list_view);
        toolbar = view.findViewById(R.id.my_toolbar);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        ((MainActivity)getActivity()).setToolbar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("");

        final ArrayList<String> titles = new ArrayList<>();
        titles.add(this.getString(R.string.label_font_style));
        titles.add(this.getString(R.string.label_font_size));
        titles.add(this.getString(R.string.label_font_color));

        adapter = new SettingsAdapter(getContext(), titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        showFontChooserDialog();
                        break;
                    case 1:
                        showSizeChooserDialog();
                        break;
                    case 2:
                        showColorChooserDialog();
                        break;
                }
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showFontChooserDialog(){
        ArrayList<String> fontTitles = new ArrayList<>();

        final String[] fontPaths = CustomizationFragment.this.getResources().getStringArray(R.array.font_values);
        String[] fontNames = CustomizationFragment.this.getResources().getStringArray(R.array.font_names);
        for(int i = 0; i<fontNames.length; i++){
            fontTitles.add(fontNames[i]);
        }

        FontsAdapter adapter = new FontsAdapter(getContext(), fontTitles);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_choose_font)
                .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.putString("font", fontPaths[i]);
                        editor.apply();
                    }
                });
        builder.show();
    }

    private void showSizeChooserDialog(){
        String[] sizeString = CustomizationFragment.this.getResources().getStringArray(R.array.font_size_array);
        final String[] sizeValueString = CustomizationFragment.this.getResources().getStringArray(R.array.font_size_values);
        ArrayList<String> size = new ArrayList<>();
        for(int i = 0; i < sizeString.length; i++){
            size.add(sizeString[i]);
        }
        FontSizeAdapter adapter = new FontSizeAdapter(getContext(), size);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editor.putString("font_size", sizeValueString[i]);
                editor.apply();
            }
        })
                .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }

    private void showColorChooserDialog(){
        int mDefaultColor = preferences.getInt("color", 0);

        int dialogId = new ColorPickerDialog(getContext(), R.color.lightGrey, true).show();
        SetColorPickerListenerEvent.setListener(dialogId, new ColorPickerListener() {
            @Override
            public void onDialogClosing() {

            }

            @Override
            public void onColorChanged(int color) {
                SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("color", color);
                editor.apply();
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onDestroyView() {
//        ((MainActivity)getActivity()).setToolbar(null);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP || keyCode == KeyEvent.KEYCODE_BACK){
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }
}