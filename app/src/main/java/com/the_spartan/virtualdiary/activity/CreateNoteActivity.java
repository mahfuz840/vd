package com.the_spartan.virtualdiary.activity;

import static android.provider.ContactsContract.CommonDataKinds.Note.NOTE;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.model.Note;
import com.the_spartan.virtualdiary.util.DateUtil;
import com.the_spartan.virtualdiary.util.FontUtil;
import com.the_spartan.virtualdiary.util.StringUtil;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.text.ParseException;
import java.util.Calendar;

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
    private TextView tvDate;
    private TextView tvTime;
    private AdView adView;
    private String description;
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

        mCalendar = Calendar.getInstance();
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mMonth = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);

        etTitle = findViewById(R.id.title_edit_text);
        etContent = findViewById(R.id.content_edit_text);
        tvDate = findViewById(R.id.date);
        tvTime = findViewById(R.id.time);

        Typeface myFont = FontUtil.initializeFonts(CreateNoteActivity.this);

        if (myFont != null) {
            etContent.setTypeface(myFont);
            etTitle.setTypeface(myFont);
            tvDate.setTypeface(myFont);
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

        tvDate.setText(DateUtil.getFormattedDateStrFromMillis(mCalendar.getTimeInMillis()));
        tvTime.setText(DateUtil.getFormattedDateStrFromMillis(mCalendar.getTimeInMillis()));

        Note note = (Note) getIntent().getSerializableExtra(NOTE);
        if (note != null) {
            id = note.getID();

            tvDate.setText(DateUtil.getFormattedDateStrFromMillis(note.getTimestamp()));
            tvTime.setText(DateUtil.getFormattedTimeStrFromMillis(note.getTimestamp()));

            etTitle.setText(note.getTitle());
            etContent.setText(note.getDescription());
        }

        tvDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(CreateNoteActivity.this, (view, year, month, dayOfMonth) -> {
                String monthString1 = StringUtil.getMonthNameFromInt(month + 1);
                tvDate.setText(dayOfMonth + " " + monthString1 + ", " + year);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.YEAR, year);
            }, mYear, mMonth, mDay);
            dialog.show();
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
            menu.findItem(R.id.action_delete).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                if (getIntent().getExtras() == null) {
                    try {
                        saveNote();
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(),
                                "Something wrong happened!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    updateNote();
                }
                finish();
                break;

            case R.id.action_delete:
                if (getIntent().getExtras() != null) {
                    showDeleteDialog();
                }
                break;

            case R.id.action_share:
                String dateForIntent = tvDate.getText().toString();
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

    private void saveNote() throws ParseException {
        title = etTitle.getText().toString();
        description = etContent.getText().toString();

        Note note = new Note.NoteBuilder()
                .setTitle(title)
                .setDescription(description)
                .setTimeStamp(mCalendar.getTimeInMillis())
                .build();

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
        } else if (!etTitle.getText().toString().equals(title) || !etContent.getText().toString().equals(description)) {
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

        customDialog.posBtn.setOnClickListener(v -> {
            if (id != -1) {
                updateNote();
            } else {
                try {
                    saveNote();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            isExiting = true;
            onBackPressed();
        });

        customDialog.negBtn.setOnClickListener(v -> {
            customDialog.dismiss();
            isExiting = true;
            onBackPressed();
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


