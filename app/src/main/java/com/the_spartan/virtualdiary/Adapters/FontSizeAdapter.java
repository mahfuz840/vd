package com.the_spartan.virtualdiary.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.the_spartan.virtualdiary.R;

import java.util.ArrayList;

public class FontSizeAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> size;
    private String[] fontSize;
    private String[] fontSizeValues;
    public FontSizeAdapter(Context context, ArrayList<String> size) {
        super(context, 0, size);
        mContext = context;
        this.size = size;
        fontSizeValues = mContext.getResources().getStringArray(R.array.font_size_values);
        fontSize = mContext.getResources().getStringArray(R.array.font_size_array);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if(listView == null)
            listView = LayoutInflater.from(mContext).inflate(R.layout.font_size_chooser_list_view, null);
        TextView fontSizeChooserTextView = listView.findViewById(R.id.font_size_chooser_item);
        fontSizeChooserTextView.setText(fontSize[position]);
        fontSizeChooserTextView.setTextSize(Float.parseFloat(fontSizeValues[position]));


        return listView;
    }
}
