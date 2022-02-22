package com.the_spartan.virtualdiary.activity;

import static com.the_spartan.virtualdiary.model.ToDo.TODO;
import static com.the_spartan.virtualdiary.util.TimeUtil.formatTime;
import static com.the_spartan.virtualdiary.util.TimeUtil.getTwelveHourFormattedTime;
import static com.the_spartan.virtualdiary.util.TimeUtil.getTwentyFourHourFormattedTime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.model.Priority;
import com.the_spartan.virtualdiary.model.ToDo;
import com.the_spartan.virtualdiary.service.ToDoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NewToDoActivity extends AppCompatActivity {

    private static final int INVALID = -1;

    int position;
    private EditText etTaskName;
    private EditText etTime;
    private EditText etDate;

    private AutoCompleteTextView priorityDropDown;

    private int ID;
    private int priority;

    private List<String> priorityValues;

    private ToDoService toDoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_item);

        initView();
        populateViews();
        setListener();

        toDoService = new ToDoService();
    }

    private void initView() {
        priorityDropDown = findViewById(R.id.dropdown_priority);
        priorityValues = new ArrayList<>();
        for (Priority priority : Priority.values()) {
            priorityValues.add(priority.getDisplayName());
        }

        etTaskName = findViewById(R.id.et_new_todo);
        etTime = findViewById(R.id.etDisplayTime);
        etDate = findViewById(R.id.et_new_todo_date);
    }

    private void populateViews() {
        ToDo todo = getOrCreate();

        if (!TextUtils.isEmpty(todo.getSubject())) {
            etTaskName.append(todo.getSubject());
        }

        if (!TextUtils.isEmpty(todo.getDueDate())) {
            etDate.setText(todo.getDueDate());
        }

        if (!TextUtils.isEmpty(todo.getTime())) {
            etTime.setText(getTwelveHourFormattedTime(todo.getTime()));
        }
    }

    private void setListener() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, priorityValues);
        priorityDropDown.setAdapter(adapter);

        priorityDropDown.setOnItemClickListener((adapterView, view, i, l) -> priority = i);
        etDate.setOnClickListener(view -> showDatePicker());
        etTime.setOnClickListener(view -> showTimePicker());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.to_do_menu_new, menu);
        Drawable drawable = menu.findItem(R.id.newadd).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        Drawable drawable1 = menu.findItem(R.id.shareNew).getIcon();
        if (drawable1 != null) {
            drawable1.mutate();
            drawable1.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    public void saveOrUpdate(MenuItem item) {
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


    public void onShareClick(MenuItem view) {
        if (!TextUtils.isEmpty(etTaskName.getText().toString())) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "";
            shareBody += etTaskName.getText().toString();
            if (!TextUtils.isEmpty(etTime.getText().toString())) {
                shareBody += "\n" + etTime.getText().toString();
                if (!TextUtils.isEmpty(etDate.getText().toString())) {
                    shareBody += "\n" + etDate.getText().toString();
                }
            }
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ToDo task");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
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
