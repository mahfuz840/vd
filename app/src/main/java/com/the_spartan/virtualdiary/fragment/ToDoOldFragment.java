package com.the_spartan.virtualdiary.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import java.util.List;

public class ToDoOldFragment extends Fragment implements DeleteListCollector{

    private static final String TAG = "ToDoOldFragment";

    public static ArrayList<ToDoItem> deleteList = new ArrayList<>();

    private ListView listView;
    private ToDoAdapter toDoAdapter;
    private ArrayList<ToDoItem> oldItemList;

    private TextView emptyLayout;
    private MenuItem deleteMenu;
    private MenuItem searchItem;

    private Context mContext;

    public ToDoOldFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_to_do, container, false);

        listView = view.findViewById(R.id.list_view_old_todo);
        emptyLayout = view.findViewById(R.id.old_todo_empty_layout);
        listView.setEmptyView(emptyLayout);
        oldItemList = new ArrayList<>();

        setHasOptionsMenu(true);

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
            toDoAdapter = new ToDoAdapter(mContext, oldItemList, this, false);
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
                        onResume();
                        Toast.makeText(mContext, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                        customDialog.dismiss();
                    }
                });

                customDialog.show();

                return true;
            }
        });
    }

    @Override
    public void updateDeleteList(ArrayList<ToDoItem> newDeleteList) {
        deleteList.clear();
        deleteList.addAll(newDeleteList);
        checkForDeleteVisibility();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.to_do_menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        Drawable drawable = menu.findItem(R.id.search).getIcon();
        if (drawable != null) {
            drawable.mutate();
        }

        Drawable drawableDelete = menu.findItem(R.id.deleteItems).getIcon();
        if (drawableDelete != null) {
            drawableDelete.mutate();
        }

        setupSearchView(menu);

        deleteMenu = menu.findItem(R.id.deleteItems);
        deleteMenu.setVisible(false);

        deleteMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showDeleteConfirmDialog();
                return true;
            }
        });

        checkForDeleteVisibility();
    }

    private void setupSearchView(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchItem = menu.findItem(R.id.search);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        searchView.setMaxWidth(width / 2);
        View searchplate = (View) searchView.findViewById(R.id.search_plate);
        searchplate.setBackgroundResource(R.color.colorAccent);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (toDoAdapter != null) {
                    toDoAdapter.getFilter().filter(newText);
                }

                return true;
            }
        });
    }

    public void showDeleteConfirmDialog() {
        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        final CustomDialog dialog = new CustomDialog(mContext,
                viewGroup,
                R.layout.dialog,
                R.string.confirm_delete,
                R.string.dialog_msg_todo_multi_delete,
                R.string.dialog_btn_delete,
                R.string.dialog_btn_cancel);

        dialog.posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItems();
                dialog.dismiss();
                onResume();
            }
        });

        dialog.show();
    }

    public void deleteItems() {
        for (ToDoItem item : deleteList) {
            String[] selectionArgs = new String[]{String.valueOf(item.getID())};

            Uri uri = Uri.withAppendedPath(ToDoProvider.CONTENT_URI, String.valueOf(ToDoProvider.DELETE_A_TODO));
            mContext.getContentResolver().delete(uri, null, selectionArgs);
        }

        deleteList.clear();
        deleteMenu.setVisible(false);
    }

    private void checkForDeleteVisibility() {
        if (deleteMenu == null) {
            return;
        }

        if (deleteList.size() > 0) {
            deleteMenu.setVisible(true);
        } else {
            deleteMenu.setVisible(false);
        }
    }
}
