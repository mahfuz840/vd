package com.the_spartan.virtualdiary.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.ToDoContract;
import com.the_spartan.virtualdiary.data.ToDoProvider;
import com.the_spartan.virtualdiary.model.ToDoItem;
import com.the_spartan.virtualdiary.util.DeleteListCollector;
import com.the_spartan.virtualdiary.util.FontUtil;

import java.util.ArrayList;

import static com.the_spartan.virtualdiary.fragment.ToDoFragment.deleteList;

//import com.the_spartan.virtualdiary.models.Item;

public class CustomItemsAdapter extends ArrayAdapter<ToDoItem> implements Filterable {

    private ArrayList<ToDoItem> original;
    public ArrayList<ToDoItem> fitems;
    private Filter filter;
    private Context context;
    private DeleteListCollector deleteListCollector;

    public CustomItemsAdapter(Context context, ArrayList<ToDoItem> items, DeleteListCollector deleteListCollector) {
        super(context, 0, items);
        this.original = new ArrayList<>();
        this.original.addAll(items);
        this.fitems = new ArrayList<>();
        this.fitems.addAll(items);
        this.deleteListCollector = deleteListCollector;

        this.context = context;
    }

    @Override
    public int getCount() {
        return fitems == null ? 0 : fitems.size();
    }

    @Override
    public ToDoItem getItem(int position) {
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

        Typeface myFont = FontUtil.initializeFonts(context);

        ToDoItem item = fitems.get(position);
        if (item != null) {
            tvName = v.findViewById(R.id.tvItem);
            tvPriority = v.findViewById(R.id.tvItemPriority);

            int isChecked = item.getIsDone();
            boolean isCheckedBool = isChecked == 1;

            CheckBox cb = v.findViewById(R.id.cbItemCheck);
            cb.setTag(position);
            cb.setChecked(isCheckedBool);

            boolean found = false;
            int index = 0;
            for (int i = 0; i < deleteList.size(); i++) {
                if (item.getID() == deleteList.get(i).getID()) {
                    found = true;
                    index = i;
                }
            }

            if (isCheckedBool && !found) {
                deleteList.add(item);
            } else if (!isCheckedBool && found) {
                deleteList.remove(index);
            }
            deleteListCollector.updateDeleteList(deleteList);

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                    int pos = (int) button.getTag();
                    ToDoItem item = getItem(pos);

                    int isCheckedInt = isChecked ? 1 : 0;

                    ContentValues values = new ContentValues();
                    values.put(ToDoContract.ToDoEntry.COLUMN_ISDONE, isCheckedInt);

                    String selection = ToDoContract.ToDoEntry.COLUMN_ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(item.getID())};

                    Uri uri = ToDoProvider.CONTENT_URI;

                    getContext().getContentResolver().update(uri, values, selection, selectionArgs);
                    item.setIsDone(isCheckedInt);
                    original.set(pos, item);
                    notifyDataSetChanged();
                }
            });

            tvName.setText(item.getSubject());

            if (item.getIsDone() == 1) {
                tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvName.setPaintFlags(0);
            }

            switch (item.getPriority()) {
                case 0:
                    tvPriority.setText(R.string.priority_low);
                    break;
                case 1:
                    tvPriority.setText(R.string.priority_med);
                    break;
                default:
                    tvPriority.setText(R.string.priority_high);
                    break;
            }

//            tvPriority.setTextColor(item.priority.getColor());
            dueDate = v.findViewById(R.id.tvDueDate);
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

            int color = FontUtil.initializeColor(context);

            if (color != 0) {
                tvName.setTextColor(color);
//                EtTitle.setTextColor(color);
            }

            String fontSize = FontUtil.initializeFontSize(context);

            if (fontSize != null) {
                tvName.setTextSize(Float.parseFloat(fontSize));
                tvPriority.setTextSize(Float.parseFloat(String.valueOf(Integer.parseInt(fontSize) - 4)));
                dueDate.setTextSize(Float.parseFloat(String.valueOf(Integer.parseInt(fontSize) - 6)));
            }

        }

//        v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_in));

        return v;
    }

    public void filter(String query) {
        query = query.toLowerCase();
        fitems.clear();
        if (query.length() == 0) {
            fitems.addAll(original);
        } else {
            for (int i = 0; i < original.size(); i++)
                if (original.get(i).getSubject().toLowerCase().contains(query))
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
