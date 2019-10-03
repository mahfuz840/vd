package com.the_spartan.virtualdiary.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.NoteContract.NoteEntry;
import com.the_spartan.virtualdiary.data.NoteDbHelper;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.objects_and_others.Note;
import com.the_spartan.virtualdiary.objects_and_others.Utils;

import java.util.Calendar;

public class CreateNoteActivity extends AppCompatActivity {

    EditText EtTitle;
    EditText EtContent;
    ImageView TvDateIndicator;
    ImageView TvTitleIndicator;
    TextView dateView;

    private String content;
    private String title;

    Note mLoadedNote;
    int mDay, mMonth, mYear;
    int id;
    private Calendar mCalendar;

    private boolean isExiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        MobileAds.initialize(this, "ca-app-pub-6572007445610561~2773371688");
        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        isExiting = false;
        id = -1;
        title = null;
        content = null;

        mCalendar = Calendar.getInstance();
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mMonth = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);

        EtTitle = findViewById(R.id.title_edit_text);
        EtContent = findViewById(R.id.content_edit_text);
        TvDateIndicator = findViewById(R.id.date_indicator);
        TvTitleIndicator = findViewById(R.id.title_indicator);
        dateView = findViewById(R.id.date);


        Typeface myFont = Utils.initializeFonts(CreateNoteActivity.this);

        if (myFont != null) {
            EtContent.setTypeface(myFont);
            EtTitle.setTypeface(myFont);

//            TvDateIndicator.setTypeface(myFont);
//            TvTitleIndicator.setTypeface(myFont);
            dateView.setTypeface(myFont);

        }

        int color = Utils.initializeColor(CreateNoteActivity.this);

        if (color != 0) {
            EtContent.setTextColor(color);
            EtTitle.setTextColor(color);
        }

        String fontSize = Utils.initializeFontSize(CreateNoteActivity.this);

        if (fontSize != null) {
            EtContent.setTextSize(Float.parseFloat(fontSize));
            EtTitle.setTextSize(Float.parseFloat(fontSize));

//            TvDateIndicator.setTextSize(Float.parseFloat(fontSize));
//            TvTitleIndicator.setTextSize(Float.parseFloat(fontSize));
        }

        String monthString;
        switch (mMonth + 1) {
            case 1:
                monthString = "Jan";
                break;
            case 2:
                monthString = "Feb";
                break;
            case 3:
                monthString = "Mar";
                break;
            case 4:
                monthString = "Apr";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "Jun";
                break;
            case 7:
                monthString = "Jul";
                break;
            case 8:
                monthString = "Aug";
                break;
            case 9:
                monthString = "Sep";
                break;
            case 10:
                monthString = "Oct";
                break;
            case 11:
                monthString = "Nov";
                break;
            case 12:
                monthString = "Dec";
                break;
            default:
                monthString = "Unknown";
        }
        dateView.setText(mDay + " " + monthString + " " + mYear);

        if (getIntent().getExtras() != null) {
            id = getIntent().getIntExtra(NoteEntry.COLUMN_ID, 10);
            Log.d("ID", " " + id);
            String date = getIntent().getStringExtra("formatted_time");
            String[] dates = date.split("/");
            switch (Integer.parseInt(dates[1])) {
                case 1:
                    monthString = "Jan";
                    break;
                case 2:
                    monthString = "Feb";
                    break;
                case 3:
                    monthString = "Mar";
                    break;
                case 4:
                    monthString = "Apr";
                    break;
                case 5:
                    monthString = "May";
                    break;
                case 6:
                    monthString = "Jun";
                    break;
                case 7:
                    monthString = "Jul";
                    break;
                case 8:
                    monthString = "Aug";
                    break;
                case 9:
                    monthString = "Sep";
                    break;
                case 10:
                    monthString = "Oct";
                    break;
                case 11:
                    monthString = "Nov";
                    break;
                case 12:
                    monthString = "Dec";
                    break;
                default:
                    monthString = "Unknown";
            }
            dateView.setText(dates[0] + " " + monthString + " " + dates[2]);
            title = getIntent().getStringExtra(NoteEntry.COLUMN_TITLE);
            content = getIntent().getStringExtra(NoteEntry.COLUMN_DESCRIPTION);

                EtTitle.setText(title);
                EtContent.setText(content);


//            EtTitle.setFocusable(false);
//            EtContent.setFocusable(false);

        }


        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(CreateNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        month += 1;
                        String monthString;
                        switch (month + 1) {
                            case 1:
                                monthString = "Jan";
                                break;
                            case 2:
                                monthString = "Feb";
                                break;
                            case 3:
                                monthString = "Mar";
                                break;
                            case 4:
                                monthString = "Apr";
                                break;
                            case 5:
                                monthString = "May";
                                break;
                            case 6:
                                monthString = "Jun";
                                break;
                            case 7:
                                monthString = "Jul";
                                break;
                            case 8:
                                monthString = "Aug";
                                break;
                            case 9:
                                monthString = "Sep";
                                break;
                            case 10:
                                monthString = "Oct";
                                break;
                            case 11:
                                monthString = "Nov";
                                break;
                            case 12:
                                monthString = "Dec";
                                break;
                            default:
                                monthString = "Unknown";
                        }
                        dateView.setText(dayOfMonth + " " + monthString + " " + year);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.YEAR, year);
                    }
                }, mYear, mMonth, mDay);
                dialog.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_note_activity_menu, menu);
        return true;
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
                String titleForIntent = EtTitle.getText().toString();
                String descriptionForIntent = EtContent.getText().toString();
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

        title = EtTitle.getText().toString();
        content = EtContent.getText().toString();

        NoteDbHelper helper = new NoteDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NoteEntry.COLUMN_DATE, date);
        values.put(NoteEntry.COLUMN_TITLE, title);
        values.put(NoteEntry.COLUMN_DESCRIPTION, content);
        values.put(NoteEntry.COLUMN_MONTH, month);
        values.put(NoteEntry.COLUMN_YEAR, year);

        Uri uri = getContentResolver().insert(NoteProvider.CONTENT_URI, values);
        id = (int) ContentUris.parseId(uri);
    }

    private void updateNote() {
        Long date = mCalendar.getTimeInMillis();
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int year = mCalendar.get(Calendar.YEAR);

        String title = EtTitle.getText().toString();
        String content = EtContent.getText().toString();

        ContentValues values = new ContentValues();

        values.put(NoteEntry.COLUMN_DATE, getIntent().getLongExtra(NoteEntry.COLUMN_DATE, 0));
        values.put(NoteEntry.COLUMN_TITLE, title);
        values.put(NoteEntry.COLUMN_DESCRIPTION, content);
        values.put(NoteEntry.COLUMN_MONTH, month);
        values.put(NoteEntry.COLUMN_YEAR, year);

//        int IntegerID = Integer.parseInt(id);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title);

        builder.setPositiveButton(R.string.delete_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNote();
                finish();
            }
        });

        builder.setNegativeButton(R.string.delete_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        if (isExiting) {
            super.onBackPressed();
        } else if (!EtTitle.getText().toString().equals(title) || !EtContent.getText().toString().equals(content)) {
            showSavePopup();
        } else {
            super.onBackPressed();
        }
    }

    private void showSavePopup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateNoteActivity.this);
        dialog.setTitle("Do you want to save the changes?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (id != -1) {
                            updateNote();
                        } else {
                            saveNote();
                        }
                        isExiting = true;
                        onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isExiting = true;
                        onBackPressed();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.show();
    }
}


