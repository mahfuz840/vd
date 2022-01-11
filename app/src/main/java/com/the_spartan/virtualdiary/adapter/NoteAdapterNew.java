package com.the_spartan.virtualdiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.model.Note;

import java.util.ArrayList;

public class NoteAdapterNew extends ArrayAdapter<Note> {

    private Context context;
    private ArrayList<Note> notes;
    private TextView tvTitle;
    private TextView tvContent;

    public NoteAdapterNew(@NonNull Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.note_list_view_item, null);
        }

        tvTitle = listItemView.findViewById(R.id.note_list_item_title);
        tvContent = listItemView.findViewById(R.id.note_list_item_content);

        Note currentNote = notes.get(position);

        tvTitle.setText(currentNote.getTitle());
        tvContent.setText(currentNote.getDescription());

        return listItemView;
    }

    public ArrayList<Note> getItems() {
        return notes;
    }
}
