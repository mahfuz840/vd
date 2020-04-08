package com.the_spartan.virtualdiary.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.connection.InternetConnection;
import com.the_spartan.virtualdiary.objects_and_others.GoogleSignin;

public class GoogleSigninActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    public static final String TAG = "GoogleSignInActivity";
    GoogleSignInClient mGoogleSignInClient;
    TextView appNameTextview;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signin);
//        getSupportActionBar().setTitle("Welcome");
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSkipped = preferences.getBoolean("skip", false);
        if (isSkipped) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        appNameTextview = findViewById(R.id.app_name_textview);
//        Typeface typeface = ResourcesCompat.getFont(this, R.fonts.myfont);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        findViewById(R.id.signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection())
                    signIn();
            }
        });

//        findViewById(R.id.skip_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(GoogleSigninActivity.this);
//                builder.setMessage("If you skip google sign in you won't be able to backup and restore your notes. You can sign in later from Settings. Do you still want to skip sign in?")
//                        .setPositiveButton("SKIP", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GoogleSigninActivity.this);
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.putBoolean("skip", true);
//                                editor.apply();
//                                startActivity(new Intent(GoogleSigninActivity.this, MainActivity.class));
//                                finish();
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//
//                builder.show();
//            }
//        });
        findViewById(R.id.skip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(GoogleSigninActivity.this, "If you skip google sign in you won't be able to backup and restore your notes. You can sign in later from Settings. Do you still want to skip sign in?", "SKIP", "CANCEL");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // do something with the account
        if (account == null)
            Log.d(TAG, "not signed in");
        else {
            Log.d(TAG, "already signed in");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean checkConnection() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        if (InternetConnection.checkConnection(this)) {
            dialog.cancel();
            return true;
        } else {
            dialog.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please check your network connection")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.cancel();

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignin.getInstance().setAccount(account);

            Log.d(TAG, "login successful");
            startActivity(new Intent(GoogleSigninActivity.this, MainActivity.class));
            finish();
        } catch (ApiException e) {
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Error while signing in", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDialog(Activity activity, String msg, String posText, String negText) {

//        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.dialog);
//
//        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
//        text.setText(msg);
//
//        TextView posBtn = dialog.findViewById(R.id.pos_btn);
//        posBtn.setText(posText);
//        posBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GoogleSigninActivity.this);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean("skip", true);
//                editor.apply();
//                startActivity(new Intent(GoogleSigninActivity.this, MainActivity.class));
//                finish();
//            }
//        });
//
//        TextView negBtn = dialog.findViewById(R.id.neg_btn);
//        negBtn.setText(negText);
//        negBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//        dialog.show();

        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, viewGroup, false);
        dialogView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_in));


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView msgView = dialogView.findViewById(R.id.text_dialog);
        msgView.setText(msg);

        TextView posBtn = dialogView.findViewById(R.id.pos_btn);
        posBtn.setText(posText);

        TextView negBtn = dialogView.findViewById(R.id.neg_btn);
        negBtn.setText(negText);

        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GoogleSigninActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("skip", true);
                editor.apply();
                startActivity(new Intent(GoogleSigninActivity.this, MainActivity.class));
                finish();
            }
        });
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
}