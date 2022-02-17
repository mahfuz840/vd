package com.the_spartan.virtualdiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.model.Note;

import java.util.ArrayList;

public class NoteAdapterNew extends ArrayAdapter<Note> implements Filterable {

    private Context context;
    private TextView tvTitle;
    private TextView tvContent;
    private ArrayList<Note> notes;
    private ArrayList<Note> filteredNotes;

    public NoteAdapterNew(@NonNull Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
        this.context = context;
        this.notes = notes;
        this.filteredNotes = notes;
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

        Note currentNote = filteredNotes.get(position);

        tvTitle.setText(currentNote.getTitle());
        tvContent.setText(currentNote.getDescription());

        return listItemView;
    }

    public ArrayList<Note> getItems() {
        return filteredNotes;
    }

    public ArrayList<Note> getOriginalItems() {
        return notes;
    }

    @Override
    public int getCount() {
        return filteredNotes.size();
    }

    @Nullable
    @Override
    public Note getItem(int position) {
        return filteredNotes.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.count = notes.size();
                    results.values = notes;

                    return results;
                }

                ArrayList<Note> matchedNotes = new ArrayList<>();
                String queryText = constraint.toString().toLowerCase();

                for (Note note : notes) {
                    if (note.getTitle().toLowerCase().contains(queryText)
                            || note.getDescription().toLowerCase().contains(queryText)) {

                        matchedNotes.add(note);
                    }
                }

                results.count = matchedNotes.size();
                results.values = matchedNotes;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                filteredNotes = (ArrayList<Note>) results.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
