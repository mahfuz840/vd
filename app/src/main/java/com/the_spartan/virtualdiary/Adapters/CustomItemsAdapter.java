package com.the_spartan.virtualdiary.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activities.ToDoFragment;
//import com.the_spartan.virtualdiary.models.Item;
import com.the_spartan.virtualdiary.objects_and_others.ToDoItem;
import com.the_spartan.virtualdiary.objects_and_others.Utils;

import java.util.ArrayList;

import static com.the_spartan.virtualdiary.activities.ToDoFragment.deleteList;

public class CustomItemsAdapter extends ArrayAdapter<ToDoItem> implements Filterable {

    private ArrayList<ToDoItem> original;
    public ArrayList<ToDoItem> fitems;
    private Filter filter;
    private Context context;

    public CustomItemsAdapter(Context context, ArrayList<ToDoItem> items) {
        super(context, 0, items);
        this.original = new ArrayList<>();
        this.original.addAll(items);
        this.fitems = new ArrayList<>();
        this.fitems.addAll(items);

        this.context = context;
    }

    @Override
    public int getCount() {
        if (fitems == null)
            return 0;
        else
            return fitems.size();
    }

    @Override
    public ToDoItem getItem(int position) {
        Log.d("size", fitems.size()+"");
        Log.d("size pos", position+"");
        return fitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView tvName;
        TextView tvPriority;
        TextView dueDate;

        if (v == null)
            v = LayoutInflater.from(context).inflate(R.layout.item, null);


        Typeface myFont = Utils.initializeFonts(context);

        ToDoItem item = fitems.get(position);
        if (item != null) {
            tvName = v.findViewById(R.id.tvItem);
            tvPriority = v.findViewById(R.id.tvItemPriority);


            int isChecked = item.getIsDone();
            boolean isCheckedBool;
            if (isChecked == 1)
                isCheckedBool = true;
            else
                isCheckedBool = false;

            CheckBox cb = v.findViewById(R.id.cbItemCheck);
            cb.setTag(position);
            cb.setChecked(isCheckedBool);

            boolean found = false;
            int index = 0;
            for (int i = 0; i < deleteList.size(); i++)
                if (item.getID() == deleteList.get(i).getID()) {
                    found = true;
                    index = i;
                }

            if (isCheckedBool && !found)
                deleteList.add(item);
            else if (!isCheckedBool && found)
                deleteList.remove(index);

//            cb.setOnCheckedChangeListener(ToDoFragment);

            tvName.setText(item.getSubject());

            if (isCheckedBool)
                tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (item.getPriority() == 0)
                tvPriority.setText("Low");
            else if (item.getPriority() == 1)
                tvPriority.setText("Medium");
            else
                tvPriority.setText("High");

//            tvPriority.setTextColor(item.priority.getColor());
            dueDate = (TextView) v.findViewById(R.id.tvDueDate);
            if (!TextUtils.isEmpty(item.getDueDate())) {
                dueDate.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(item.getTime())) {
                    dueDate.setText(item.getDueDate());
                } else {
                    dueDate.setText(item.getDueDate());
                }
            } else {
                dueDate.setVisibility(View.GONE);
            }

            if (myFont != null) {
                tvName.setTypeface(myFont);
                tvPriority.setTypeface(myFont);
                dueDate.setTypeface(myFont);

            }

            int color = Utils.initializeColor(context);

            if (color != 0) {
                tvName.setTextColor(color);
//                EtTitle.setTextColor(color);
            }

            String fontSize = Utils.initializeFontSize(context);

            if (fontSize != null) {
                tvName.setTextSize(Float.parseFloat(fontSize));
                tvPriority.setTextSize(Float.parseFloat(String.valueOf(Integer.parseInt(fontSize)-4)));
                dueDate.setTextSize(Float.parseFloat(String.valueOf(Integer.parseInt(fontSize)-6)));
            }

        }

        v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_in));

        return v;
    }


    public void filter(String query)
    {
        query = query.toLowerCase();
        fitems.clear();
        if(query.length() == 0)
        {
            fitems.addAll(original);
        }
        else
        {
            for(int i = 0; i < original.size(); i++)
                if(original.get(i).getSubject().toLowerCase().contains(query))
                    fitems.add(original.get(i));
        }

        this.notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
