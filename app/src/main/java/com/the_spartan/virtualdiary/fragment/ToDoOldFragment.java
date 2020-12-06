package com.the_spartan.virtualdiary.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.adapter.ToDoAdapter;
import com.the_spartan.virtualdiary.data.ToDoContract;
import com.the_spartan.virtualdiary.data.ToDoDbHelper;
import com.the_spartan.virtualdiary.data.ToDoProvider;
import com.the_spartan.virtualdiary.interfacing.DeleteListCollector;
import com.the_spartan.virtualdiary.model.ToDoItem;
import com.the_spartan.virtualdiary.util.DateUtil;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;
import java.util.Date;

public class ToDoOldFragment extends Fragment {

    private static final String TAG = "ToDoOldFragment";

    private DeleteListCollector deleteListCollector;

    private ListView listView;
    private ToDoAdapter toDoAdapter;
    private ArrayList<ToDoItem> oldItemList;

    private TextView emptyLayout;

    private Context mContext;

    public ToDoOldFragment() {

    }

    public ToDoOldFragment(DeleteListCollector deleteListCollector) {
        this.deleteListCollector = deleteListCollector;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_to_do, container, false);

        listView = view.findViewById(R.id.list_view_old_todo);
        emptyLayout = view.findViewById(R.id.old_todo_empty_layout);
        listView.setEmptyView(emptyLayout);
        oldItemList = new ArrayList<>();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();

        populateItems();
    }

    @Override
    public void onResume() {
        populateItems();

        if (toDoAdapter != null) {
            toDoAdapter.notify(oldItemList);
        }

        super.onResume();
    }

    public void populateItems() {
        oldItemList.clear();
        ToDoDbHelper helper = new ToDoDbHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM todos", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            while (cursor.moveToNext()) {
                ToDoItem item = new ToDoItem(cursor.getInt(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_ID))
                        , cursor.getString(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_SUBJECT))
                        , cursor.getString(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_DUE))
                        , cursor.getString(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_TIME))
                        , cursor.getInt(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_PRIORITY))
                        , cursor.getInt(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_ISDONE)));

                try {
                    Date itemDate = DateUtil.getDateFromString(item.getDueDate());
                    Date currentDate = DateUtil.getCurrentFormattedDate();
                    Date lastValidDate = DateUtil.getThirtyDaysEarlierDate(currentDate);
                    if (itemDate != null && itemDate.before(currentDate) &&
                            (itemDate.equals(lastValidDate) || itemDate.after(lastValidDate))) {
                        oldItemList.add(item);
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.getMessage());
                }
            }

            cursor.close();
            db.close();
        }

        initViews();
    }

    private void initViews() {
        if (oldItemList.size() > 0 && toDoAdapter == null) {
            toDoAdapter = new ToDoAdapter(mContext, oldItemList, deleteListCollector, false);
            listView.setAdapter(toDoAdapter);
        }

        if (toDoAdapter != null) {
            setOnLongClickListener();
        }
    }

    private void setOnLongClickListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;

                View parentView = (View) view.getParent();
                if (parentView == null) {
                    return false;
                }

                ViewGroup viewGroup = parentView.findViewById(android.R.id.content);
                final CustomDialog customDialog = new CustomDialog(mContext,
                        viewGroup,
                        R.layout.dialog,
                        R.string.confirm_delete,
                        R.string.dialog_msg_todo_single_delete,
                        R.string.dialog_btn_yes,
                        R.string.dialog_btn_cancel);

                customDialog.posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToDoItem itemToBeDeleted = toDoAdapter.getItem(pos);
                        String[] selectionArgs = new String[]{String.valueOf(itemToBeDeleted.getID())};
                        Uri uri = Uri.withAppendedPath(ToDoProvider.CONTENT_URI, String.valueOf(ToDoProvider.DELETE_A_TODO)); //300 is for deleting a single todo_list_item
                        mContext.getContentResolver().delete(uri, null, selectionArgs);
                        populateItems();
                        Toast.makeText(mContext, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                        customDialog.dismiss();
                    }
                });

                customDialog.show();

                return true;
            }
        });
    }
}
