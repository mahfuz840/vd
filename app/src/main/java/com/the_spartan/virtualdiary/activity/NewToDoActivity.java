package com.the_spartan.virtualdiary.activity;

import static com.the_spartan.virtualdiary.model.ToDo.TODO;
import static com.the_spartan.virtualdiary.util.DateUtil.getCurrentFormattedDateStr;
import static com.the_spartan.virtualdiary.util.TimeUtil.formatTime;
import static com.the_spartan.virtualdiary.util.TimeUtil.getTwelveHourFormattedTime;
import static com.the_spartan.virtualdiary.util.TimeUtil.getTwentyFourHourFormattedTime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.model.Priority;
import com.the_spartan.virtualdiary.model.ToDo;
import com.the_spartan.virtualdiary.service.ToDoService;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class NewToDoActivity extends AppCompatActivity {

    private EditText etTaskName;
    private EditText etTime;
    private EditText etDate;
    private AutoCompleteTextView priorityDropDown;
    private ImageButton ibBack;
    private ImageButton ibDone;
    private ImageButton ibDelete;

    private int priority;

    private ToDoService toDoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_todo);

        initView();
        populateViews();
        setListeners();

        toDoService = new ToDoService();
    }

    private void initView() {
        etTaskName = findViewById(R.id.et_new_todo);
        etTime = findViewById(R.id.etDisplayTime);
        etDate = findViewById(R.id.et_new_todo_date);
        priorityDropDown = findViewById(R.id.dropdown_priority);

        ibBack = findViewById(R.id.ib_new_todo_back);
        ibDone = findViewById(R.id.ib_new_todo_done);
        ibDelete = findViewById(R.id.ib_new_todo_delete);
        if (getIntent().getSerializableExtra(TODO) == null) {
            ibDelete.setVisibility(View.GONE);
        }
    }

    private void populateViews() {
        ToDo todo = getOrCreate();

        if (!TextUtils.isEmpty(todo.getSubject())) {
            etTaskName.append(todo.getSubject());
        }

        if (!TextUtils.isEmpty(todo.getDueDate())) {
            etDate.setText(todo.getDueDate());
        } else {
            etDate.setText(getCurrentFormattedDateStr());
        }

        if (!TextUtils.isEmpty(todo.getTime())) {
            etTime.setText(getTwelveHourFormattedTime(todo.getTime()));
        }

        ArrayList<String> priorityValues = new ArrayList<>();
        for (Priority priority : Priority.values()) {
            priorityValues.add(priority.getDisplayName());
        }

        priorityDropDown.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        priorityValues)
        );

        priorityDropDown.setText(todo.getPriority().getDisplayName(), false);
    }

    private void setListeners() {
        priorityDropDown.setOnItemClickListener((adapterView, view, i, l) -> priority = i);
        etDate.setOnClickListener(view -> showDatePicker());
        etTime.setOnClickListener(view -> showTimePicker());

        ibBack.setOnClickListener(view -> finish());
        ibDone.setOnClickListener(view -> saveOrUpdate());
        ibDelete.setOnClickListener(view -> showDeleteDialog());
    }

    private void saveOrUpdate() {
        if (TextUtils.isEmpty(etTaskName.getText().toString().trim())) {
            Toast.makeText(NewToDoActivity.this, "Task Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ToDo todo = getOrCreate();
        todo.setSubject(etTaskName.getText().toString().trim());
        todo.setDueDate(etDate.getText().toString());
        todo.setTime(getTwentyFourHourFormattedTime(etTime.getText().toString()));
        todo.setPriority(Arrays.asList(Priority.values()).get(priority));

        toDoService.saveOrUpdate(todo);

        this.finish();
    }

    private void showDeleteDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        CustomDialog customDialog = new CustomDialog(this,
                viewGroup,
                R.layout.dialog,
                R.string.confirm_delete,
                R.string.dialog_msg_todo_single_delete,
                R.string.dialog_btn_yes,
                R.string.dialog_btn_cancel);

        customDialog.posBtn.setOnClickListener(view -> {
            ToDo todo = (ToDo) getIntent().getSerializableExtra(TODO);
            toDoService.delete(todo);
            finish();
        });

        customDialog.show();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(NewToDoActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String date = (dayOfMonth) + "/" + (monthOfYear + 1) + "/" + year;
                    etDate.setText(date);
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(NewToDoActivity.this,
                (view, hourOfDay, minute) -> {
                    String formattedTime = formatTime(hourOfDay, minute);
                    etTime.setText(formattedTime);
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private ToDo getOrCreate() {
        ToDo todo = (ToDo) getIntent().getSerializableExtra(TODO);
        if (todo != null) {
            return todo;
        }

        return new ToDo();
    }
}
