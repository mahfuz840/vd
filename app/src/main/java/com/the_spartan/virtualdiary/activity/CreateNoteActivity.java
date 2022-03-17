package com.the_spartan.virtualdiary.activity;

import static com.the_spartan.virtualdiary.model.Note.NOTE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.model.Note;
import com.the_spartan.virtualdiary.service.NoteService;
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

    private Note note;

    private AdView adView;
    private EditText etTitle;
    private EditText etDescription;
    private TextView tvDate;
    private TextView tvTime;
    private ImageButton ivBackArrow;
    private ImageButton ivDone;
    private ImageButton ivShare;
    private ImageButton ivDelete;

    private String description;
    private String title;
    private Calendar mCalendar;
    private boolean existing;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private NoteService noteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        loadAd();
        initVariables();
        initViews();
        initStyles();
        initNote();
        setOnClickListeners();
    }

    private void loadAd() {
        adView = new AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }

    private void initVariables() {
        mCalendar = Calendar.getInstance();
        existing = false;
        noteService = new NoteService(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void initViews() {
        etTitle = findViewById(R.id.ed_note_title);
        etDescription = findViewById(R.id.content_edit_text);
        tvDate = findViewById(R.id.date);
        tvTime = findViewById(R.id.time);
        ivBackArrow = findViewById(R.id.iv_create_note_back);
        ivDone = findViewById(R.id.iv_create_note_done);
        ivShare = findViewById(R.id.iv_create_note_share);
        ivDelete = findViewById(R.id.iv_create_note_delete);

        populateDateTimeViews();
    }

    private void populateDateTimeViews() {
        tvDate.setText(DateUtil.getFormattedDateStrFromMillis(mCalendar.getTimeInMillis()));
        tvTime.setText(DateUtil.getFormattedDateStrFromMillis(mCalendar.getTimeInMillis()));
    }

    private void initStyles() {
        Typeface myFont = FontUtil.initializeFonts(CreateNoteActivity.this);
        if (myFont != null) {
            etDescription.setTypeface(myFont);
            etTitle.setTypeface(myFont);
            tvDate.setTypeface(myFont);
        }

        int color = FontUtil.initializeColor(CreateNoteActivity.this);
        if (color != 0) {
            etDescription.setTextColor(color);
            etTitle.setTextColor(color);
        }

        String fontSize = FontUtil.initializeFontSize(CreateNoteActivity.this);
        if (fontSize != null) {
            etDescription.setTextSize(Float.parseFloat(fontSize));
            etTitle.setTextSize(Float.parseFloat(fontSize));
        }
    }

    private void initNote() {
        note = (Note) getIntent().getSerializableExtra(NOTE);
        if (note == null) {
            return;
        }

        tvDate.setText(DateUtil.getFormattedDateStrFromMillis(note.getTimestamp()));
        tvTime.setText(DateUtil.getFormattedTimeStrFromMillis(note.getTimestamp()));

        etTitle.setText(note.getTitle());
        etDescription.setText(note.getDescription());

        existing = true;
    }

    private void save() throws ParseException {
        getInputs();

        Note note = new Note.NoteBuilder()
                .setTitle(title)
                .setDescription(description)
                .setTimeStamp(mCalendar.getTimeInMillis())
                .build();

        checkForReview();

        noteService.saveOrUpdate(note);
    }

    private void update() {
        getInputs();

        note.setTitle(title);
        note.setDescription(description);
        note.setTimestamp(mCalendar.getTimeInMillis());

        noteService.saveOrUpdate(note);
    }

    private void share() {
        String dateForIntent = tvDate.getText().toString();
        String titleForIntent = etTitle.getText().toString();
        String descriptionForIntent = etDescription.getText().toString();
        String message = dateForIntent + "\n" + titleForIntent + "\n" + descriptionForIntent;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share your note"));
    }

    private void getInputs() {
        title = etTitle.getText().toString();
        description = etDescription.getText().toString();
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

        customDialog.posBtn.setOnClickListener(view -> {
            noteService.delete(note);
            customDialog.dismiss();

            Toast.makeText(this,
                    getString(R.string.toast_note_deleted_successfully),
                    Toast.LENGTH_SHORT)
                    .show();

            finish();
        });

        customDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (existing) {
            super.onBackPressed();
        } else if (!etTitle.getText().toString().equals(title) || !etDescription.getText().toString().equals(description)) {
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
            if (existing) {
                update();
            } else {
                try {
                    save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            onBackPressed();
        });

        customDialog.negBtn.setOnClickListener(v -> {
            customDialog.dismiss();
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

    private void setOnClickListeners() {
        ivBackArrow.setOnClickListener(view -> this.finish());
        ivDone.setOnClickListener(view ->
        {
            if (note == null) {
                try {
                    save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                update();
            }

            this.finish();
        });

        ivShare.setOnClickListener(view -> share());
        ivDelete.setOnClickListener(view -> showDeleteDialog());

        tvDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(CreateNoteActivity.this, (view, year, month, dayOfMonth) -> {
                String monthString1 = StringUtil.getMonthNameFromInt(month + 1);
                tvDate.setText(dayOfMonth + " " + monthString1 + ", " + year);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.YEAR, year);
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }
}
