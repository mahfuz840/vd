package com.the_spartan.virtualdiary.activity;

import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.FirebaseHelper;
import com.the_spartan.virtualdiary.data.NoteContract.NoteEntry;
import com.the_spartan.virtualdiary.data.NoteDbHelper;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.model.Note;
import com.the_spartan.virtualdiary.util.FontUtil;
import com.the_spartan.virtualdiary.util.StringUtil;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String NO_OF_TIMES_USED = "noOfTimesSaved";
    private static final int FIRST_MILESTONE = 15;
    private static final int SECOND_MILESTONE = 40;
    private static final int THIRD_MILESTONE = 65;
    private static final int FOURTH_MILESTONE = 90;

    int mDay, mMonth, mYear;
    int id;
    private EditText etTitle;
    private EditText etContent;
    private TextView dateView;
    private TextView timeView;
    private AdView adView;
    private String content;
    private String title;
    private Calendar mCalendar;
    private boolean isExiting;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loadAd();

        isExiting = false;
        id = -1;
        title = "";
        content = "";

        mCalendar = Calendar.getInstance();
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mMonth = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);

        etTitle = findViewById(R.id.title_edit_text);
        etContent = findViewById(R.id.content_edit_text);
        dateView = findViewById(R.id.date);
        timeView = findViewById(R.id.time);


        Typeface myFont = FontUtil.initializeFonts(CreateNoteActivity.this);

        if (myFont != null) {
            etContent.setTypeface(myFont);
            etTitle.setTypeface(myFont);
            dateView.setTypeface(myFont);

        }

        int color = FontUtil.initializeColor(CreateNoteActivity.this);

        if (color != 0) {
            etContent.setTextColor(color);
            etTitle.setTextColor(color);
        }

        String fontSize = FontUtil.initializeFontSize(CreateNoteActivity.this);

        if (fontSize != null) {
            etContent.setTextSize(Float.parseFloat(fontSize));
            etTitle.setTextSize(Float.parseFloat(fontSize));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mma", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        String monthString = StringUtil.getMonthNameFromInt(mMonth + 1);

        dateView.setText(mDay + " " + monthString + ", " + mYear);
        timeView.setText(currentDateandTime);

        if (getIntent().getExtras() != null) {
            id = getIntent().getIntExtra(NoteEntry.COLUMN_ID, 10);
            Log.d("ID", " " + id);
            String date = getIntent().getStringExtra("formatted_time");
            String[] dates = date.split("/");
            monthString = StringUtil.getMonthNameFromInt(Integer.parseInt(dates[1]));

            String[] timeString = dates[2].split(" ");
            timeView.setText(timeString[1]);
            dates[2] = timeString[0];
            dateView.setText(dates[0] + " " + monthString + ", " + dates[2]);

            title = getIntent().getStringExtra(NoteEntry.COLUMN_TITLE);
            content = getIntent().getStringExtra(NoteEntry.COLUMN_DESCRIPTION);

            etTitle.setText(title);
            etContent.setText(content);

        }


        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(CreateNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String monthString = StringUtil.getMonthNameFromInt(month + 1);
                        dateView.setText(dayOfMonth + " " + monthString + ", " + year);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.YEAR, year);
                    }
                }, mYear, mMonth, mDay);
                dialog.show();
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_note_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getIntent().getExtras() == null) {
            menu.findItem(R.id.note_delete).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.create_note_activity_action_save:
                if (getIntent().getExtras() == null) {
                    saveNote();
                } else {
                    updateNote();
                }
                finish();
                break;

            case R.id.note_delete:
                if (getIntent().getExtras() != null) {
                    showDeleteDialog();
                }
                break;

            case R.id.create_note_activity_action_share:

                String dateForIntent = dateView.getText().toString();
                String titleForIntent = etTitle.getText().toString();
                String descriptionForIntent = etContent.getText().toString();
                String message = dateForIntent + "\n" + titleForIntent + "\n" + descriptionForIntent;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share your note"));
                break;
        }

        return true;
    }

    private void saveNote() {
        Long date = mCalendar.getTimeInMillis();
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int year = mCalendar.get(Calendar.YEAR);

        title = etTitle.getText().toString();
        content = etContent.getText().toString();

        NoteDbHelper helper = new NoteDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NoteEntry.COLUMN_DATE, date);
        values.put(NoteEntry.COLUMN_TITLE, title);
        values.put(NoteEntry.COLUMN_DESCRIPTION, content);
        values.put(NoteEntry.COLUMN_MONTH, month);
        values.put(NoteEntry.COLUMN_YEAR, year);

        Note note = new Note(date, title, content, mCalendar);

        Uri uri = getContentResolver().insert(NoteProvider.CONTENT_URI, values);
        id = (int) ContentUris.parseId(uri);

        checkForReview();

        FirebaseHelper.getReference().child("Note").push().setValue(note);
    }

    private void updateNote() {
        Long date = mCalendar.getTimeInMillis();
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int year = mCalendar.get(Calendar.YEAR);

        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        ContentValues values = new ContentValues();

        values.put(NoteEntry.COLUMN_DATE, getIntent().getLongExtra(NoteEntry.COLUMN_DATE, 0));
        values.put(NoteEntry.COLUMN_TITLE, title);
        values.put(NoteEntry.COLUMN_DESCRIPTION, content);
        values.put(NoteEntry.COLUMN_MONTH, month);
        values.put(NoteEntry.COLUMN_YEAR, year);

        getContentResolver().update(Uri.withAppendedPath(NoteProvider.CONTENT_URI, String.valueOf(id)),
                values,
                null,
                null);
    }

    private void deleteNote() {
        getContentResolver().delete(Uri.withAppendedPath(NoteProvider.CONTENT_URI, String.valueOf(id)),
                null,
                null);
    }

    private void showDeleteDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        CustomDialog customDialog = new CustomDialog(this,
                viewGroup,
                R.layout.dialog,
                R.string.confirm_delete,
                R.string.dialog_msg_note_single_delete,
                R.string.dialog_btn_yes,
                R.string.dialog_btn_cancel);

        customDialog.posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote();
                finish();
            }
        });

        customDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isExiting) {
            super.onBackPressed();
        } else if (!etTitle.getText().toString().equals(title) || !etContent.getText().toString().equals(content)) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showSaveDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final CustomDialog customDialog = new CustomDialog(this,
                viewGroup,
                R.layout.dialog,
                0,
                R.string.dialog_title_save_changes,
                R.string.dialog_btn_save,
                R.string.dialog_btn_cancel);

        customDialog.posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id != -1) {
                    updateNote();
                } else {
                    saveNote();
                }
                isExiting = true;
                onBackPressed();
            }
        });

        customDialog.negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                isExiting = true;
                onBackPressed();
            }
        });

        customDialog.show();
    }

    private void checkForReview() {
        int noOfTimesUsed = preferences.getInt(NO_OF_TIMES_USED, 0);
        if (noOfTimesUsed < FIRST_MILESTONE
                || (noOfTimesUsed > FIRST_MILESTONE && noOfTimesUsed < SECOND_MILESTONE)
                || (noOfTimesUsed > SECOND_MILESTONE && noOfTimesUsed < THIRD_MILESTONE)
                || (noOfTimesUsed > THIRD_MILESTONE && noOfTimesUsed < FOURTH_MILESTONE)) {
            editor = preferences.edit();
            editor.putInt(NO_OF_TIMES_USED, ++noOfTimesUsed);
            editor.apply();

            return;
        }

        showReview();
    }

    private void showReview() {
        final ReviewManager reviewManager = ReviewManagerFactory.create(this);

        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    Task<Void> flow = reviewManager.launchReviewFlow(CreateNoteActivity.this, task.getResult());
                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {

                        }
                    });
                } else {
                    Toast.makeText(CreateNoteActivity.this, "Error in review", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadAd() {
        adView = new AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }
}


