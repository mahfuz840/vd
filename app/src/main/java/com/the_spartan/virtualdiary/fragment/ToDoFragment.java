package com.the_spartan.virtualdiary.fragment;

import android.app.SearchManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.MainActivity;
import com.the_spartan.virtualdiary.activity.NewItemActivity;
import com.the_spartan.virtualdiary.adapter.CustomItemsAdapter;
import com.the_spartan.virtualdiary.data.ToDoContract;
import com.the_spartan.virtualdiary.data.ToDoDbHelper;
import com.the_spartan.virtualdiary.data.ToDoProvider;
import com.the_spartan.virtualdiary.model.ToDoItem;
import com.the_spartan.virtualdiary.util.DeleteListCollector;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ToDoFragment extends Fragment implements DeleteListCollector {

    public static ArrayList<ToDoItem> deleteList = new ArrayList<>();
    private static Context mContext;
    LinearLayout todoEmptyLayout;
    private CustomItemsAdapter toDoAdapter;
    private ListView listView;
    private Button quickAddButton;
    private EditText editText;
    private MenuItem deleteItems;
    private MenuItem searchItem;
    private ArrayList<ToDoItem> listItems;

    public ToDoFragment() {

    }

    public static Context getToDoContext() {
        return mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        ((MainActivity) getActivity()).setToolbar(toolbar);


        editText = view.findViewById(R.id.edit_text);
        quickAddButton = view.findViewById(R.id.quick_add_button);
        listView = view.findViewById(R.id.lvDisplay);
        todoEmptyLayout = view.findViewById(R.id.todo_empty_layout);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listItems = new ArrayList<>();
        mContext = getContext();

        populateItems();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;

                View parentView = (View) view.getParent();
                if (parentView == null) {
                    return false;
                }

                ViewGroup viewGroup = parentView.findViewById(android.R.id.content);
                CustomDialog customDialog = new CustomDialog(mContext,
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
                        Uri uri = Uri.withAppendedPath(ToDoProvider.CONTENT_URI, String.valueOf(ToDoProvider.DELETE_A_TODO)); //300 is for deleting a single item
                        mContext.getContentResolver().delete(uri, null, selectionArgs);
                        populateItems();
                        Toast.makeText(mContext, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    }
                });

                customDialog.show();

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(mContext, NewItemActivity.class);
                i.putExtra("subject", toDoAdapter.getItem(position).getSubject());
                i.putExtra("priority", toDoAdapter.getItem(position).getPriority());
                i.putExtra("date", toDoAdapter.getItem(position).getDueDate());
                i.putExtra("time", toDoAdapter.getItem(position).getTime());
                i.putExtra("done", toDoAdapter.getItem(position).getIsDone());
                i.putExtra("ID", toDoAdapter.getItem(position).getID());
                startActivity(i);
            }
        });

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
                    populateItems();
                }
            }
        });
    }

    private void populateItems() {

        ToDoDbHelper helper = new ToDoDbHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        Parcelable state = listView.onSaveInstanceState();
        listItems.clear();

        Cursor cursor = null;
        int i = 0;

        try {
            cursor = db.rawQuery("select * from todos", null);
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

                i++;
                listItems.add(item);
            }

            cursor.close();
            db.close();
        }

        if (listItems.isEmpty()) {
            listView.setVisibility(View.GONE);
            todoEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            todoEmptyLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        Collections.sort(listItems, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem o1, ToDoItem o2) {
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

        if (listItems.size() > 0) {
            toDoAdapter = new CustomItemsAdapter(mContext, listItems, this);
            listView.setAdapter(toDoAdapter);
        }
//        if (state != null)
        listView.onRestoreInstanceState(state);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case android.R.id.home:
//                onBackPressed();
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.to_do_menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        Drawable drawable = menu.findItem(R.id.search).getIcon();
        if (drawable != null) {
            drawable.mutate();
//            drawable.setTint(Color.WHITE);
        }
        Drawable drawableDelete = menu.findItem(R.id.deleteItems).getIcon();
        if (drawableDelete != null) {
            drawableDelete.mutate();
        }

        searchItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) mContext.getSystemService((Context.SEARCH_SERVICE));
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                toDoAdapter.filter(newText);
                return true;
            }
        });

        deleteItems = menu.findItem(R.id.deleteItems);
        deleteItems.setVisible(false);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
//                fab.setVisibility(View.INVISIBLE);
//                spkBtn.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);
                quickAddButton.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
//                fab.setVisibility(View.VISIBLE);
//                spkBtn.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                quickAddButton.setVisibility(View.VISIBLE);
                return true;
            }
        });

        deleteItems.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showDeleteConfirmDialog();
                return true;
            }
        });

        checkForDeleteVisibility();
    }

    public void onShareClick(MenuItem view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "";
        for (int i = 0; i < listItems.size(); i++) {
            shareBody += i + 1 + ". " + listItems.get(i).getSubject();
            shareBody += "\n";
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ToDo tasks");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void showDeleteConfirmDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
//                .setTitle("Do you really want to delete?")
//                .setMessage("The marked items will be deleted")
//                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteItems();
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//        builder.show();

        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        CustomDialog dialog = new CustomDialog(mContext,
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
        deleteItems.setVisible(false);

        populateItems();
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
        if (deleteList.size() > 0) {
            deleteItems.setVisible(true);
//            shareItem.setVisible(false);
        } else {
            deleteItems.setVisible(false);
//            shareItem.setVisible(true);
        }
    }

    @Override
    public void onDestroy() {
        deleteList.clear();
        super.onDestroy();
    }

    @Override
    public void updateDeleteList(List<ToDoItem> deleteList) {
        checkForDeleteVisibility(deleteList);
    }
}