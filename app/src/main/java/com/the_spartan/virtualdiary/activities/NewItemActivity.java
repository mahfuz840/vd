package com.the_spartan.virtualdiary.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.ToDoContract;
import com.the_spartan.virtualdiary.data.ToDoProvider;
import com.the_spartan.virtualdiary.models.Priority;

import java.util.ArrayList;
import java.util.Calendar;

public class NewItemActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etNewTask;
    private EditText timeTextView;
    private EditText dateTextView;
    private Spinner spinner;
    int isDone;
    private int ID;

    int position;
    int priorityHigh = 2;
    int priorityMedium = 1;
    int priorityLow = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_item);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> priorityValues = new ArrayList<>();
        priorityValues.add("low");
        priorityValues.add("medium");
        priorityValues.add("high");
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorityValues));

        // Find our View instances
        etNewTask = (EditText)findViewById(R.id.etNewTask);
        timeTextView = (EditText)findViewById(R.id.etDisplayTime);
        dateTextView = (EditText)findViewById(R.id.etDisplayDate);
        ImageView timeButton = (ImageView)findViewById(R.id.imgTime);
        ImageView dateButton = (ImageView)findViewById(R.id.imgDate);


        ID = getIntent().getIntExtra("ID", 0);
        String subject = getIntent().getStringExtra("subject");
        position = getIntent().getIntExtra("position", -1);
        int priority = getIntent().getIntExtra("priority", 1);
        Log.d("Priority", ""+priority);
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        isDone = getIntent().getIntExtra("done", 0);


        if(!TextUtils.isEmpty(subject)) {
            etNewTask.append(subject);
        }

        if(priority == priorityHigh) {
            spinner.setSelection(2);
        } else if(priority == priorityMedium) {
            spinner.setSelection(1);
        } else {
            spinner.setSelection(0);
        }

            dateTextView.setText(date);
            timeTextView.setText(time);

        // Show a timepicker when the timeButton is clicked
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeSet();
            }
        });

        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateSet();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void clearDate(View view) {
        dateTextView.setText("");
    }

    public void clearTime(View view) {
        timeTextView.setText("");
    }

    public void onAddNewSaveClick(MenuItem item) {
        if(TextUtils.isEmpty(etNewTask.getText().toString().trim())) {
            Toast.makeText(NewItemActivity.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            String subject = etNewTask.getText().toString().trim();
            String dueDate = dateTextView.getText().toString();
            String time = timeTextView.getText().toString();
            int priority = spinner.getSelectedItemPosition();

            ContentValues values = new ContentValues();
            values.put(ToDoContract.ToDoEntry.COLUMN_SUBJECT, subject);
            values.put(ToDoContract.ToDoEntry.COLUMN_DUE, dueDate);
            values.put(ToDoContract.ToDoEntry.COLUMN_TIME, time);
            values.put(ToDoContract.ToDoEntry.COLUMN_PRIORITY, priority);
            values.put(ToDoContract.ToDoEntry.COLUMN_ISDONE, isDone);

            String[] selectionArgs = new String[]{String.valueOf(ID)};
            String selection = ToDoContract.ToDoEntry.COLUMN_ID + "=?";

            Uri uri = ToDoProvider.CONTENT_URI;

            getContentResolver().update(uri, values, selection, selectionArgs);

            this.finish();
        }
    }

    public void onDateSet(View view) {
        onDateSet();
    }

    public void onTimeSet(View view) {
        onTimeSet();
    }


    private void onDateSet(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(NewItemActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = (dayOfMonth)+"/"+(monthOfYear+1)+"/"+year;
                        Log.w("MyApp", "onDateSet: " + date);
                        dateTextView.setText(date);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void onTimeSet(){
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewItemActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                        String minuteString = minute < 10 ? "0"+minute : ""+minute;
                        String time = hourString+":"+minuteString;
                        timeTextView.setText(time);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }



    public void onShareClick(MenuItem view) {
        if(!TextUtils.isEmpty(etNewTask.getText().toString())) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "";
            shareBody += etNewTask.getText().toString();
            if(!TextUtils.isEmpty(timeTextView.getText().toString())) {
                shareBody += "\n" + timeTextView.getText().toString();
                if(!TextUtils.isEmpty(dateTextView.getText().toString())) {
                    shareBody += "\n" + dateTextView.getText().toString();
                }
            }
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ToDo task");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
    }

}
