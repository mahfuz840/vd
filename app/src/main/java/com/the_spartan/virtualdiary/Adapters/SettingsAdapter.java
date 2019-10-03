package com.the_spartan.virtualdiary.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.the_spartan.virtualdiary.R;

import java.util.ArrayList;

public class SettingsAdapter extends ArrayAdapter<String> {

    private Context mContext;
    ArrayList<String> titles;

    public SettingsAdapter(Context context, ArrayList<String> titles) {
        super(context, 0, titles);
        mContext = context;
        this.titles = titles;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.font_style_list_view, null);
        }

        String currentString = titles.get(position);
        TextView tv = listItemView.findViewById(R.id.title);
        tv.setText(currentString);

        if(position == 2){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            int color = preferences.getInt("color", 1);
            ImageView colorPreview = listItemView.findViewById(R.id.color_preview_image_view);
            colorPreview.setVisibility(View.VISIBLE);
            colorPreview.setColorFilter(color);
        }

        return listItemView;
    }
}
