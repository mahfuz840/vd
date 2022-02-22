package com.the_spartan.virtualdiary.fragment;

import static com.the_spartan.virtualdiary.model.ToDo.TODO;
import static com.the_spartan.virtualdiary.util.ListViewUtil.setListViewHeightBasedOnChildren;
import static com.the_spartan.virtualdiary.util.ViewUtil.hideViewsOfaDay;
import static com.the_spartan.virtualdiary.util.ViewUtil.setVisibility;
import static com.the_spartan.virtualdiary.util.ViewUtil.showViewsOfaDay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.NewToDoActivity;
import com.the_spartan.virtualdiary.adapter.ToDoAdapter;
import com.the_spartan.virtualdiary.model.ToDo;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;

public class ToDoFragment extends Fragment {

    private static final String TODO_FRAGMENT_TAG = "ToDoFragment";

    private static ToDoFragment instance;

    private LinearLayout todoEmptyLayout;

    private ToDoAdapter todayAdapter;
    private ToDoAdapter tomorrowAdapter;
    private ToDoAdapter nextAdapter;

    private ListView lvToday;
    private ListView lvTomorrow;
    private ListView lvUpcoming;

    private FloatingActionButton fabAddTodo;

    private ArrayList<ToDo> todayItems = new ArrayList<>();
    private ArrayList<ToDo> tomorrowItems = new ArrayList<>();
    private ArrayList<ToDo> nextItems = new ArrayList<>();

    private CardView emptyCardViewToday;
    private CardView emptyCardViewTomorrow;
    private CardView emptyCardViewNext;

    private TextView tvToday;
    private TextView tvTomorrow;
    private TextView tvNext;

    private SearchView svTodo;
    private Spinner spnTodoType;
    private ImageButton ivDelete;

    private ToDoFragment() {
    }

    public static ToDoFragment getInstance() {
        if (instance == null) {
            instance = new ToDoFragment();
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);

        initViews(view);
        setHasOptionsMenu(true);

        return view;
    }

    private void initViews(View view) {
        fabAddTodo = view.findViewById(R.id.floating_btn_add_todo);
        lvToday = view.findViewById(R.id.list_view_today);
        lvTomorrow = view.findViewById(R.id.list_view_tomorrow);
        lvUpcoming = view.findViewById(R.id.list_view_next);
        emptyCardViewToday = view.findViewById(R.id.empty_todo_today);
        lvToday.setEmptyView(emptyCardViewToday);
        emptyCardViewTomorrow = view.findViewById(R.id.empty_todo_tomorrow);
        lvTomorrow.setEmptyView(emptyCardViewTomorrow);
        emptyCardViewNext = view.findViewById(R.id.empty_todo_next);
        lvUpcoming.setEmptyView(emptyCardViewNext);

        tvToday = view.findViewById(R.id.tv_today);
        tvTomorrow = view.findViewById(R.id.tv_tomorrow);
        tvNext = view.findViewById(R.id.tv_next);

        svTodo = view.findViewById(R.id.search_view_todo);
        spnTodoType = view.findViewById(R.id.spinner_todo_type);
        ivDelete = view.findViewById(R.id.iv_todo_delete);

        todoEmptyLayout = view.findViewById(R.id.active_todo_empty_layout);
    }

    private void populateTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.todo_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTodoType.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fabAddTodo.setOnClickListener(view -> startActivity(new Intent(getContext(), NewToDoActivity.class)));
        populateItems();
        populateListViews();
        registerOnClickListeners();
        registerOnLongClickListeners();
        populateTypeSpinner();
    }

    public void populateItems() {
        todayItems.clear();
        tomorrowItems.clear();
        nextItems.clear();

        for (int i = 0; i < 5; i++) {
            ToDo todo = new ToDo();
            todo.setSubject("Test");
            todo.setDueDate("23/11/2021");

            todayItems.add(todo);
        }
    }

    private void populateListViews() {
        if (todayItems.isEmpty() && tomorrowItems.isEmpty() && nextItems.isEmpty()) {
            setVisibility(View.GONE,
                    lvToday, lvTomorrow, lvUpcoming,
                    emptyCardViewToday, emptyCardViewTomorrow, emptyCardViewNext,
                    tvToday, tvTomorrow, tvNext);
            todoEmptyLayout.setVisibility(View.VISIBLE);

            return;
        }

        todoEmptyLayout.setVisibility(View.GONE);

//        ListViewUtil.sortByTime(todayItems);
//        ListViewUtil.sortByTime(tomorrowItems);
//        ListViewUtil.sortByDateAsc(nextItems);

        if (todayItems.size() > 0) {
            todayAdapter = populateViewsOfaDay(emptyCardViewToday, tvToday, lvToday, todayAdapter, todayItems);
        } else {
            hideViewsOfaDay(emptyCardViewToday, tvToday, lvToday);
        }

        if (tomorrowItems.size() > 0) {
            tomorrowAdapter = populateViewsOfaDay(emptyCardViewTomorrow, tvTomorrow, lvTomorrow, tomorrowAdapter, tomorrowItems);
        } else {
            hideViewsOfaDay(emptyCardViewTomorrow, tvTomorrow, lvTomorrow);
        }

        if (nextItems.size() > 0) {
            nextAdapter = populateViewsOfaDay(emptyCardViewNext, tvNext, lvUpcoming, nextAdapter, nextItems);
        } else {
            hideViewsOfaDay(emptyCardViewNext, tvNext, lvUpcoming);
        }

        setListViewHeightBasedOnChildren(lvToday);
        setListViewHeightBasedOnChildren(lvTomorrow);
        setListViewHeightBasedOnChildren(lvUpcoming);
    }

    private ToDoAdapter populateViewsOfaDay(CardView emptyCardView, TextView textView, ListView listView,
                                            ToDoAdapter adapter, ArrayList<ToDo> items) {
        if (adapter == null) {
            adapter = new ToDoAdapter(getContext(), items);
        }

        showViewsOfaDay(emptyCardView, textView, listView);
        listView.setAdapter(adapter);
        adapter.notify(items);

        return adapter;
    }

    public void showDeleteConfirmDialog() {
        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        final CustomDialog dialog = new CustomDialog(getContext(),
                viewGroup,
                R.layout.dialog,
                R.string.confirm_delete,
                R.string.dialog_msg_todo_multi_delete,
                R.string.dialog_btn_delete,
                R.string.dialog_btn_cancel);

        dialog.posBtn.setOnClickListener(view -> {
            todayAdapter.deleteCompletedTodos();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void registerOnClickListeners() {
        if (todayAdapter != null && lvToday.getOnItemClickListener() == null) {
            setOnClickListeners(lvToday, todayAdapter);
        }

        if (tomorrowAdapter != null && lvTomorrow.getOnItemClickListener() == null) {
            setOnClickListeners(lvTomorrow, tomorrowAdapter);
        }

        if (nextAdapter != null && lvUpcoming.getOnItemClickListener() == null) {
            setOnClickListeners(lvUpcoming, nextAdapter);
        }
    }

    private void setOnClickListeners(ListView listView, final ToDoAdapter adapter) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent todoIntent = new Intent(getContext(), NewToDoActivity.class);
            todoIntent.putExtra(TODO, adapter.getItem(position));
            startActivity(todoIntent);
        });

        svTodo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String queryText = svTodo.getQuery().toString();
                todayAdapter.getFilter().filter(queryText);

                return true;
            }
        });

        ivDelete.setOnClickListener(view -> showDeleteConfirmDialog());
    }

    private void registerOnLongClickListeners() {
        if (todayAdapter != null && lvToday.getOnItemLongClickListener() == null) {
            setOnLongClickListeners(lvToday, todayAdapter);
        }

        if (tomorrowAdapter != null && lvTomorrow.getOnItemLongClickListener() == null) {
            setOnLongClickListeners(lvTomorrow, tomorrowAdapter);
        }

        if (nextAdapter != null && lvUpcoming.getOnItemLongClickListener() == null) {
            setOnLongClickListeners(lvUpcoming, nextAdapter);
        }
    }

    private void setOnLongClickListeners(ListView listView, final ToDoAdapter adapter) {
        listView.setOnItemLongClickListener((parent, view, position, id) -> {

            View parentView = (View) view.getParent();
            if (parentView == null) {
                return false;
            }

            ViewGroup viewGroup = parentView.findViewById(android.R.id.content);
            final CustomDialog customDialog = new CustomDialog(getContext(),
                    viewGroup,
                    R.layout.dialog,
                    R.string.confirm_delete,
                    R.string.dialog_msg_todo_single_delete,
                    R.string.dialog_btn_yes,
                    R.string.dialog_btn_cancel);

            customDialog.posBtn.setOnClickListener(view1 -> {

            });

            customDialog.show();

            return true;
        });
    }
}