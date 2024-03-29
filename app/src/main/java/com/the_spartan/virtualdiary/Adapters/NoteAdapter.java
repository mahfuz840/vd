package com.the_spartan.virtualdiary.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.the_spartan.virtualdiary.objects_and_others.Note;
import com.the_spartan.virtualdiary.R;

import java.util.ArrayList;

/**
 * Created by the_spartan on 4/1/18.
 */

public class NoteAdapter extends ArrayAdapter<Note> {

    Context mContext;
    ArrayList<Note> notes;
    int contentLines;
    TextView contentView;
    TextView titleView;
    TextView dateView;
    TextView monthView;

    public NoteAdapter(@NonNull Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
        this.notes = notes;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.note_recycler_search, null);
        }
        Note currentNoteAdapter = notes.get(position);

        dateView = listItemView.findViewById(R.id.search_date_view);
        String[] splittedDates = currentNoteAdapter.getDateTimeFormatted(mContext).split("/");
        dateView.setText(splittedDates[0]);

        monthView = listItemView.findViewById(R.id.search_month_view);

        String monthString;
        int month = currentNoteAdapter.getmMonth();
        switch (month) {
            case 1:
                monthString = "Jan";
                break;
            case 2:
                monthString = "Feb";
                break;
            case 3:
                monthString = "Mar";
                break;
            case 4:
                monthString = "Apr";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "Jun";
                break;
            case 7:
                monthString = "Jul";
                break;
            case 8:
                monthString = "Aug";
                break;
            case 9:
                monthString = "Sep";
                break;
            case 10:
                monthString = "Oct";
                break;
            case 11:
                monthString = "Nov";
                break;
            case 12:
                monthString = "Dec";
                break;
            default:
                monthString = "Unknown";
        }

        monthView.setText(monthString);

        titleView = listItemView.findViewById(R.id.search_title_view);
        String title = currentNoteAdapter.getMtitle();
        if (title == null || title.equals("")) {
            titleView.setText("(no title)");
        } else {
            titleView.setText(currentNoteAdapter.getMtitle());
        }

        contentView = listItemView.findViewById(R.id.search_content_view);
        String content = currentNoteAdapter.getmContent();
        if (content.isEmpty())
            contentView.setText("(no description)");
        else
            contentView.setText(currentNoteAdapter.getmContent());

        return listItemView;
    }
}
