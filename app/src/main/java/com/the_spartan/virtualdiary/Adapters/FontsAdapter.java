package com.the_spartan.virtualdiary.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.objects_and_others.Utils;

import java.util.ArrayList;

public class FontsAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> fontTitles;
    private String[] fontPaths;

    public FontsAdapter(Context context, ArrayList<String> fontTitles) {
        super(context, 0, fontTitles);
        mContext = context;
        this.fontTitles = fontTitles;
        fontPaths = mContext.getResources().getStringArray(R.array.font_values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View fontListView = convertView;
        if (fontListView == null) {
            fontListView = LayoutInflater.from(mContext).inflate(R.layout.font_titles_list_view, null);
        }
//        String[] fontPaths = mContext.getResources().getStringArray(R.array.font_values);

        Log.d("font", fontPaths[position]);

        TextView fonTitleTextView = fontListView.findViewById(R.id.font_chooser_title_text_view);
        if (fontPaths[position].equals("null")){
            fonTitleTextView.setTypeface(Typeface.DEFAULT);
        } else {
            fonTitleTextView.setTypeface(Utils.loadFontForFontChooserDialog(mContext, fontPaths[position]));
        }

        return fontListView;
    }
}
