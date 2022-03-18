package com.the_spartan.virtualdiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.the_spartan.virtualdiary.R;

import java.util.ArrayList;

public class FontSizeAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] fontSize;
    private String[] fontSizeValues;

    public FontSizeAdapter(Context context, ArrayList<String> size) {
        super(context, 0, size);
        this.context = context;
        this.fontSizeValues = this.context.getResources().getStringArray(R.array.font_size_values);
        this.fontSize = this.context.getResources().getStringArray(R.array.font_size_array);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.font_size_chooser_list_view, null);
        }

        TextView tvFontSizeChooser = convertView.findViewById(R.id.font_size_chooser_item);
        tvFontSizeChooser.setText(fontSize[position]);
        tvFontSizeChooser.setTextSize(Float.parseFloat(fontSizeValues[position]));

        return convertView;
    }
}
