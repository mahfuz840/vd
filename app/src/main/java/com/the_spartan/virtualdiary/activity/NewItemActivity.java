package com.the_spartan.virtualdiary.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.ToDoContract;
import com.the_spartan.virtualdiary.data.ToDoProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.the_spartan.virtualdiary.util.TimeUtil.formatTime;
import static com.the_spartan.virtualdiary.util.TimeUtil.getTwelveHourFormattedTime;
import static com.the_spartan.virtualdiary.util.TimeUtil.getTwentyFourHourFormattedTime;

public class NewItemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int INVALID = -1;

    private final int PRIORITY_HIGH = 2;
    private final int PRIORITY_MEDIUM = 1;
    private final int PRIORITY_LOW = 0;
    int isDone;
    int position;
    private EditText etNewTask;
    private EditText etTime;
    private EditText etDate;
    private AutoCompleteTextView priorityDropDown;
    private int ID;
    private int priority;

    private List<String> priorityValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_item);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initView();
        setListener();;
    }

    private void initView() {
        priorityDropDown = findViewById(R.id.dropdown_priority);
        priorityValues = new ArrayList<>();
        priorityValues.add("Low");
        priorityValues.add("Medium");
        priorityValues.add("High");

        etNewTask = findViewById(R.id.etNewTask);
        etTime = findViewById(R.id.etDisplayTime);
        etDate = findViewById(R.id.etDisplayDate);

        ID = getIntent().getIntExtra("ID", INVALID);
        String subject = getIntent().getStringExtra("subject");
        position = getIntent().getIntExtra("position", -1);
        priority = getIntent().getIntExtra("priority", 1);
        final String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        isDone = getIntent().getIntExtra("done", 0);

        if (!TextUtils.isEmpty(subject)) {
            etNewTask.append(subject);
        }

//        if (priority == PRIORITY_HIGH) {
//            priorityDropDown.setText(priorityValues.get(2));
//        } else if (priority == PRIORITY_MEDIUM) {
//            priorityDropDown.setText(priorityValues.get(1));
//        } else {
//            priorityDropDown.setText(priorityValues.get(0));
//        }

        if (!TextUtils.isEmpty(date)) {
            etDate.setText(date);
        }

        if (!TextUtils.isEmpty(time)) {
            etTime.setText(getTwelveHourFormattedTime(time));
        }
    }

    private void setListener() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, priorityValues);
        priorityDropDown.setAdapter(adapter);
        adapter.getFilter().filter(null);
        priorityDropDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                adapter.getFilter().filter(null);
                priorityDropDown.showDropDown();

                return true;
            }
        });

        priorityDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                priority = i;
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void saveOrUpdate(MenuItem item) {
        if (TextUtils.isEmpty(etNewTask.getText().toString().trim())) {
            Toast.makeText(NewItemActivity.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String subject = etNewTask.getText().toString().trim();
        String dueDate = etDate.getText().toString();
        String time = getTwentyFourHourFormattedTime(etTime.getText().toString());

        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_SUBJECT, subject);
        values.put(ToDoContract.ToDoEntry.COLUMN_DUE, dueDate);
        values.put(ToDoContract.ToDoEntry.COLUMN_TIME, time);
        values.put(ToDoContract.ToDoEntry.COLUMN_PRIORITY, priority);
        values.put(ToDoContract.ToDoEntry.COLUMN_ISDONE, isDone);

        if (ID == INVALID) {
            save(values);
        } else {
            update(values);
        }

        this.finish();
    }

    private void save(ContentValues values) {
        Uri todoUri = Uri.withAppendedPath(ToDoProvider.CONTENT_URI, "200");
        getBaseContext().getContentResolver().insert(todoUri, values);
    }

    private void update(ContentValues values) {
        String[] selectionArgs = new String[]{String.valueOf(ID)};
        String selection = ToDoContract.ToDoEntry.COLUMN_ID + "=?";

        Uri uri = ToDoProvider.CONTENT_URI;

        getContentResolver().update(uri, values, selection, selectionArgs);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(NewItemActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = (dayOfMonth)+"/"+(monthOfYear+1)+"/"+year;
                        etDate.setText(date);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(NewItemActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String formattedTime = formatTime(hourOfDay, minute);
                        etTime.setText(formattedTime);
                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }


    public void onShareClick(MenuItem view) {
        if (!TextUtils.isEmpty(etNewTask.getText().toString())) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "";
            shareBody += etNewTask.getText().toString();
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

}
