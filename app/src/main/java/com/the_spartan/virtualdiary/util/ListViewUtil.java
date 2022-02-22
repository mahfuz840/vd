package com.the_spartan.virtualdiary.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.the_spartan.virtualdiary.model.ToDo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ListViewUtil {

    public static void sortByDateAsc(ArrayList<ToDo> itemList) {
        Collections.sort(itemList, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                if (o1.getDueDate().length() == 0 && o2.getDueDate().length() == 0) {
                    return 0;
                } else if (o1.getDueDate().length() == 0) {
                    return -1;
                } else if (o2.getDueDate().length() == 0) {
                    return 1;
                }

                String[] date1 = o1.getDueDate().split("/");
                String[] date2 = o2.getDueDate().split("/");
                int valYear = Integer.parseInt(date1[2]) - Integer.parseInt(date2[2]);
                if (valYear > 0) {
                    return 1;
                } else if (valYear < 0) {
                    return -1;
                }

                int valMonth = Integer.parseInt(date1[1]) - Integer.parseInt(date2[1]);
                if (valMonth > 0) {
                    return 1;
                } else if (valMonth < 0) {
                    return -1;
                }

                int dayVal = Integer.parseInt(date1[0]) - Integer.parseInt(date2[0]);
                if (dayVal > 0) {
                    return 1;
                } else if (dayVal < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    public static void sortByTime(ArrayList<ToDo> itemList) {
        Collections.sort(itemList, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                long difference = 0;
                try {
                    Date date1 = sdf.parse(o1.getTime());
                    Date date2 = sdf.parse(o2.getTime());

                    difference = date1.getTime() - date2.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return difference > 0
                        ? 1
                        : difference == 0 ? 0 : -1;
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
