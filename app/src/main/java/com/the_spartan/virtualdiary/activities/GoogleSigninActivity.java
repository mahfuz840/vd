package com.the_spartan.virtualdiary.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        if (isSkipped){
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
                if(checkConnection())
                    signIn();
            }
        });

        findViewById(R.id.skip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(GoogleSigninActivity.this);
                builder.setMessage("If you skip google sign in you won't be able to backup and restore your notes. You can sign in later from Settings. Do you still want to skip sign in?")
                        .setPositiveButton("SKIP", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GoogleSigninActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("skip", true);
                                editor.apply();
                                startActivity(new Intent(GoogleSigninActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();
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

    private boolean checkConnection(){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        if(InternetConnection.checkConnection(this)){
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
}