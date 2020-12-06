package com.the_spartan.virtualdiary.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.NewItemActivity;
import com.the_spartan.virtualdiary.adapter.ToDoAdapter;
import com.the_spartan.virtualdiary.data.ToDoContract;
import com.the_spartan.virtualdiary.data.ToDoDbHelper;
import com.the_spartan.virtualdiary.data.ToDoProvider;
import com.the_spartan.virtualdiary.interfacing.DeleteListCollector;
import com.the_spartan.virtualdiary.model.ToDoItem;
import com.the_spartan.virtualdiary.util.DateUtil;
import com.the_spartan.virtualdiary.util.ListViewUtil;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.the_spartan.virtualdiary.util.ListViewUtil.setListViewHeightBasedOnChildren;
import static com.the_spartan.virtualdiary.util.ViewUtil.hideViewsOfaDay;
import static com.the_spartan.virtualdiary.util.ViewUtil.setVisibility;
import static com.the_spartan.virtualdiary.util.ViewUtil.showViewsOfaDay;

public class ToDoActiveFragment extends Fragment {

    private static final String TAG = "ToDoFragment";

    public static ArrayList<ToDoItem> deleteList = new ArrayList<>();
    private Context mContext;
    private LinearLayout todoEmptyLayout;
    private ToDoAdapter todayAdapter;
    private ToDoAdapter tomorrowAdapter;
    private ToDoAdapter nextAdapter;
    private ListView listViewToday;
    private ListView listViewTomorrow;
    private ListView listViewNext;
    private Button quickAddButton;
    private EditText editText;
    private MenuItem deleteMenu;
    private MenuItem searchItem;
    private ArrayList<ToDoItem> todayItems = new ArrayList<>();
    private ArrayList<ToDoItem> tomorrowItems = new ArrayList<>();
    private ArrayList<ToDoItem> nextItems = new ArrayList<>();
    private CardView emptyCardViewToday;
    private CardView emptyCardViewTomorrow;
    private CardView emptyCardViewNext;
    private TextView tvToday;
    private TextView tvTomorrow;
    private TextView tvNext;

    private DeleteListCollector deleteListCollector;

    public ToDoActiveFragment() {

    }

    public ToDoActiveFragment(DeleteListCollector deleteListCollector) {
        this.deleteListCollector = deleteListCollector;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_to_do, container, false);

        editText = view.findViewById(R.id.edit_text);
        quickAddButton = view.findViewById(R.id.quick_add_button);
        listViewToday = view.findViewById(R.id.list_view_today);
        listViewTomorrow = view.findViewById(R.id.list_view_tomorrow);
        listViewNext = view.findViewById(R.id.list_view_next);
        emptyCardViewToday = view.findViewById(R.id.empty_todo_today);
        listViewToday.setEmptyView(emptyCardViewToday);
        emptyCardViewTomorrow = view.findViewById(R.id.empty_todo_tomorrow);
        listViewTomorrow.setEmptyView(emptyCardViewTomorrow);
        emptyCardViewNext = view.findViewById(R.id.empty_todo_next);
        listViewNext.setEmptyView(emptyCardViewNext);

        tvToday = view.findViewById(R.id.tv_today);
        tvTomorrow = view.findViewById(R.id.tv_tomorrow);
        tvNext = view.findViewById(R.id.tv_next);

        todoEmptyLayout = view.findViewById(R.id.active_todo_empty_layout);

//        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();

        quickAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(newItem)) {
                    int priority = 0;
                    ContentValues values = new ContentValues();
                    values.put(ToDoContract.ToDoEntry.COLUMN_SUBJECT, newItem);
                    values.put(ToDoContract.ToDoEntry.COLUMN_DUE, "");
                    values.put(ToDoContract.ToDoEntry.COLUMN_TIME, "");
                    values.put(ToDoContract.ToDoEntry.COLUMN_PRIORITY, priority);
                    values.put(ToDoContract.ToDoEntry.COLUMN_ISDONE, 0);

                    Uri todoUri = Uri.withAppendedPath(ToDoProvider.CONTENT_URI, "200");

                    Uri uri = mContext.getContentResolver().insert(todoUri, values);
                    editText.setText("");
                    onResume();
                }
            }
        });
    }

    public void populateItems() {
        ToDoDbHelper helper = new ToDoDbHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        Parcelable todayState = listViewToday.onSaveInstanceState();
        Parcelable tomorrowState = listViewTomorrow.onSaveInstanceState();
        Parcelable nextState = listViewNext.onSaveInstanceState();
        todayItems.clear();
        tomorrowItems.clear();
        nextItems.clear();

        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM todos", null);
        } catch (Exception e) {
            Toast.makeText(mContext, "Couldn't load To-Do items", Toast.LENGTH_SHORT).show();
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
                    Date tomorrowDate = DateUtil.getTomorrowFormattedDate();

                    if (itemDate != null && itemDate.equals(currentDate)) {
                        todayItems.add(item);
                    } else if (itemDate != null && itemDate.equals(tomorrowDate)) {
                        tomorrowItems.add(item);
                    } else if (itemDate == null || itemDate.after(currentDate)) {
                        nextItems.add(item);
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            cursor.close();
            db.close();
        }

        initViews();
        registerOnClickListeners();
        registerOnLongClickListeners();

//        listViewToday.onRestoreInstanceState(todayState);
//        listViewTomorrow.onRestoreInstanceState(tomorrowState);
//        listViewNext.onRestoreInstanceState(nextState);
    }

    private void initViews() {
        if (todayItems.isEmpty() && tomorrowItems.isEmpty() && nextItems.isEmpty()) {
            setVisibility(View.GONE,
                    listViewToday, listViewTomorrow, listViewNext,
                    emptyCardViewToday, emptyCardViewTomorrow, emptyCardViewNext,
                    tvToday, tvTomorrow, tvNext);
            todoEmptyLayout.setVisibility(View.VISIBLE);

            return;
        }

        todoEmptyLayout.setVisibility(View.GONE);

        ListViewUtil.sortByTime(todayItems);
        ListViewUtil.sortByTime(tomorrowItems);
        ListViewUtil.sortByDateAsc(nextItems);

        if (todayItems.size() > 0) {
            todayAdapter = populateViewsOfaDay(emptyCardViewToday, tvToday, listViewToday, todayAdapter, todayItems);
        } else {
            hideViewsOfaDay(emptyCardViewToday, tvToday, listViewToday);
        }

        if (tomorrowItems.size() > 0) {
            tomorrowAdapter = populateViewsOfaDay(emptyCardViewTomorrow, tvTomorrow, listViewTomorrow, tomorrowAdapter, tomorrowItems);
        } else {
            hideViewsOfaDay(emptyCardViewTomorrow, tvTomorrow, listViewTomorrow);
        }

        if (nextItems.size() > 0) {
            nextAdapter = populateViewsOfaDay(emptyCardViewNext, tvNext, listViewNext, nextAdapter, nextItems);
        } else {
            hideViewsOfaDay(emptyCardViewNext, tvNext, listViewNext);
        }

        setListViewHeightBasedOnChildren(listViewToday);
        setListViewHeightBasedOnChildren(listViewTomorrow);
        setListViewHeightBasedOnChildren(listViewNext);
    }

    private ToDoAdapter populateViewsOfaDay(CardView emptyCardView, TextView textView, ListView listView,
                                            ToDoAdapter adapter, ArrayList<ToDoItem> items) {
        if (adapter == null) {
            adapter = new ToDoAdapter(mContext, items, deleteListCollector, true);
            listView.setAdapter(adapter);
        }

        showViewsOfaDay(emptyCardView, textView, listView);
        adapter.notify(items);

        return adapter;
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

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                editText.setVisibility(View.INVISIBLE);
                quickAddButton.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                editText.setVisibility(View.VISIBLE);
                quickAddButton.setVisibility(View.VISIBLE);
                return true;
            }
        });

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
                if (todayAdapter != null) {
                    todayAdapter.getFilter().filter(newText);
                }

                if (tomorrowAdapter != null) {
                    tomorrowAdapter.getFilter().filter(newText);
                }

                if (nextAdapter != null) {
                    nextAdapter.getFilter().filter(newText);
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

    @Override
    public void onResume() {
        populateItems();

        super.onResume();
    }

    private void checkForDeleteVisibility() {
        checkForDeleteVisibility(deleteList);
    }

    private void checkForDeleteVisibility(List<ToDoItem> deleteList) {
        if (deleteMenu == null) {
            return;
        }

        if (deleteList.size() > 0) {
            deleteMenu.setVisible(true);
        } else {
            deleteMenu.setVisible(false);
        }
    }

    @Override
    public void onDestroy() {
        deleteList.clear();
        super.onDestroy();
    }

    private void registerOnClickListeners() {
        if (todayAdapter != null && listViewToday.getOnItemClickListener() == null) {
            setOnClickListeners(listViewToday, todayAdapter);
        }

        if (tomorrowAdapter != null && listViewTomorrow.getOnItemClickListener() == null) {
            setOnClickListeners(listViewTomorrow, tomorrowAdapter);
        }

        if (nextAdapter != null && listViewNext.getOnItemClickListener() == null) {
            setOnClickListeners(listViewNext, nextAdapter);
        }
    }

    private void setOnClickListeners(ListView listView, final ToDoAdapter adapter) {
        Log.d(TAG, "listener adapter" + adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(mContext, NewItemActivity.class);
                i.putExtra("subject", adapter.getItem(position).getSubject());
                i.putExtra("priority", adapter.getItem(position).getPriority());
                i.putExtra("date", adapter.getItem(position).getDueDate());
                i.putExtra("time", adapter.getItem(position).getTime());
                i.putExtra("done", adapter.getItem(position).isDone());
                i.putExtra("ID", adapter.getItem(position).getID());
                startActivity(i);
            }
        });
    }

    private void registerOnLongClickListeners() {
        if (todayAdapter != null && listViewToday.getOnItemLongClickListener() == null) {
            setOnLongClickListeners(listViewToday, todayAdapter);
        }

        if (tomorrowAdapter != null && listViewTomorrow.getOnItemLongClickListener() == null) {
            setOnLongClickListeners(listViewTomorrow, tomorrowAdapter);
        }

        if (nextAdapter != null && listViewNext.getOnItemLongClickListener() == null) {
            setOnLongClickListeners(listViewNext, nextAdapter);
        }
    }

    private void setOnLongClickListeners(ListView listView, final ToDoAdapter adapter) {
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
                        ToDoItem itemToBeDeleted = adapter.getItem(pos);
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