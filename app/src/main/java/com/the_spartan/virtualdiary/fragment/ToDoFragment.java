package com.the_spartan.virtualdiary.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.the_spartan.virtualdiary.model.ToDo.TODO;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.NewToDoActivity;
import com.the_spartan.virtualdiary.adapter.ToDoAdapter;
import com.the_spartan.virtualdiary.animation.WidthAnimation;
import com.the_spartan.virtualdiary.model.ToDo;
import com.the_spartan.virtualdiary.service.ToDoService;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;

public class ToDoFragment extends Fragment {

    private static final String TODO_FRAGMENT_TAG = "ToDoFragment";

    private static ToDoFragment instance;

    private ToDoAdapter todoAdapter;
    private ListView lvTodo;

    private FloatingActionButton fabAddTodo;

    private CardView cvEmpty;

    private SearchView svTodo;
    private ImageButton ivDelete;

    private ToDoService todoService;

    private int searchViewOriginalWidth;

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

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        todoService = new ToDoService();

        populateListView();
        registerOnClickListeners();
        registerOnLongClickListeners();
    }

    private void initViews(View view) {
        fabAddTodo = view.findViewById(R.id.fab_add_todo);
        lvTodo = view.findViewById(R.id.list_view_today);
        cvEmpty = view.findViewById(R.id.empty_todo_today);
        lvTodo.setEmptyView(cvEmpty);

        svTodo = view.findViewById(R.id.search_view_todo);
        ivDelete = view.findViewById(R.id.ib_todo_delete);
    }

    private void populateListView() {
        ArrayList<ToDo> todos = new ArrayList<>();
        todoAdapter = new ToDoAdapter(getContext(), todos);
        todoService.findAll(todos, todoAdapter);
        lvTodo.setAdapter(todoAdapter);
    }

    private void showDeleteConfirmDialog() {
        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        final CustomDialog dialog = new CustomDialog(getContext(),
                viewGroup,
                R.layout.dialog,
                R.string.confirm_delete,
                R.string.dialog_msg_todo_multi_delete,
                R.string.dialog_btn_delete,
                R.string.dialog_btn_cancel);

        dialog.posBtn.setOnClickListener(view -> {
            todoAdapter.deleteCompletedTodos();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void registerOnClickListeners() {
        lvTodo.setOnItemClickListener((parent, view, position, id) -> {
            Intent todoIntent = new Intent(getContext(), NewToDoActivity.class);
            todoIntent.putExtra(TODO, todoAdapter.getItem(position));
            startActivity(todoIntent);
        });

        svTodo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                todoAdapter.getFilter().filter(svTodo.getQuery());

                return true;
            }
        });

        svTodo.setOnQueryTextFocusChangeListener((view, focused) -> {
            if (focused) {
                searchViewOriginalWidth = svTodo.getLayoutParams().width;

                WidthAnimation widthAnimation = new WidthAnimation(svTodo);
                widthAnimation.setDuration(300);
                widthAnimation.setParams(1000);
                svTodo.startAnimation(widthAnimation);

            } else {
                WidthAnimation widthAnimation = new WidthAnimation(svTodo);
                widthAnimation.setDuration(300);
                widthAnimation.setParams(searchViewOriginalWidth);
                svTodo.startAnimation(widthAnimation);
            }
        });


        fabAddTodo.setOnClickListener(view -> startActivity(new Intent(getContext(), NewToDoActivity.class)));
        ivDelete.setOnClickListener(view -> showDeleteConfirmDialog());
    }

    private void registerOnLongClickListeners() {
        lvTodo.setOnItemLongClickListener((adapterView, view, i, l) -> {
            View parentView = (View) view.getParent();
            if (parentView == null) {
                return false;
            }

            ArrayList<ToDo> filteredTodos = todoAdapter.getItems();
            ArrayList<ToDo> originalTodos = todoAdapter.getOriginalToDoList();
            Context context = view.getContext();

            ViewGroup viewGroup = parentView.findViewById(android.R.id.content);
            final CustomDialog customDialog = new CustomDialog(getContext(),
                    viewGroup,
                    R.layout.dialog,
                    R.string.confirm_delete,
                    R.string.dialog_msg_todo_single_delete,
                    R.string.dialog_btn_yes,
                    R.string.dialog_btn_cancel);

            customDialog.posBtn.setOnClickListener(view1 -> {
                ToDo todoToDelete = filteredTodos.get(i);
                originalTodos.remove(todoToDelete);
                filteredTodos.remove(todoToDelete);
                todoService.delete(todoToDelete);

                todoAdapter.notifyDataSetChanged();
                customDialog.dismiss();
            });

            customDialog.show();

            return true;
        });
    }

    public void hideListView() {
        cvEmpty.setVisibility(VISIBLE);
        lvTodo.setVisibility(GONE);
    }

    public void showListView() {
        cvEmpty.setVisibility(GONE);
        lvTodo.setVisibility(VISIBLE);
    }
}
