package com.the_spartan.virtualdiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.the_spartan.virtualdiary.R;

import java.util.ArrayList;



public class AddOptionsAdapter extends ArrayAdapter<String> {

    private ArrayList<String> addOptions;
    private Context mContext;

    public AddOptionsAdapter(Context context, ArrayList<String> options) {
        super(context, 0, options);

        addOptions = options;
        mContext = context;
    }


    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        View optionsView = convertView;
        if (optionsView == null)
            optionsView = LayoutInflater.from(mContext).inflate(R.layout.add_options_list_view, null);

        TextView optionsTv = optionsView.findViewById(R.id.option_text_view);
        optionsTv.setText(addOptions.get(position));


        return optionsView;
    }
}
