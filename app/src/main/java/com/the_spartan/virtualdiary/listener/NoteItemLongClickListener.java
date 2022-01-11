package com.the_spartan.virtualdiary.listener;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.adapter.NoteAdapterNew;
import com.the_spartan.virtualdiary.data.FirebaseHelper;
import com.the_spartan.virtualdiary.model.Note;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;

public class NoteItemLongClickListener implements AdapterView.OnItemLongClickListener {

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDialog(adapterView, view, i);
        return true;
    }

    public void showDialog(AdapterView<?> adapterView, View itemView, int position) {
        NoteAdapterNew noteAdapter = (NoteAdapterNew) adapterView.getAdapter();
        ArrayList<Note> notes = noteAdapter.getItems();
        Context context = itemView.getContext();

        ViewGroup viewGroup = itemView.findViewById(android.R.id.content);
        final CustomDialog customDialog = new CustomDialog(context,
                viewGroup,
                R.layout.dialog,
                R.string.confirm_delete,
                R.string.dialog_msg_note_single_delete,
                R.string.dialog_btn_yes,
                R.string.dialog_btn_cancel);

        customDialog.posBtn.setOnClickListener(v -> {
            notes.remove(position);
            noteAdapter.notifyDataSetChanged();

            FirebaseHelper.removeNote(position);

            customDialog.dismiss();
        });

        customDialog.show();
    }
}
