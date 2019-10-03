package com.the_spartan.virtualdiary.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.connection.InternetConnection;
import com.the_spartan.virtualdiary.objects_and_others.GoogleSignin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;


public class BackupRestoreActivity extends AppCompatActivity {

    public static final String TAG = "BackupRestoreActivity";
    public static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    TextView emailTextView;
    ImageView DPImageView;
    Button changeAccountBtn;
    private Button backupButton;
    private Drive mDriveServiceHelper;

    ProgressDialog dialog;
    com.google.api.services.drive.Drive googleDriveService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dialog = new ProgressDialog(this);

        emailTextView = findViewById(R.id.email_textview);
        changeAccountBtn = findViewById(R.id.change_account_btn);

        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        this, Collections.singleton(DriveScopes.DRIVE_APPDATA));
        if (account != null) {
//            Toast.makeText(this, account.getEmail(), Toast.LENGTH_SHORT).show();
            emailTextView.setText(account.getEmail());
//            DPImageView.setImageDrawable(account.getPhotoUrl());
            credential.setSelectedAccount(account.getAccount());
        } else {
            emailTextView.setText("Not signed in");
//            startActivity(new Intent(this, GoogleSigninActivity.class));
            changeAccountBtn.setText("Sign in");
        }

        googleDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("Virtual Diary")
                .build();

        findViewById(R.id.backup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing()) {
                    dialog.setMessage("Backing up your data...");
                    dialog.show();
                }
                if (checkConnection()) {
                    boolean isLoggedIn = checkLogin();
                    if (isLoggedIn)
                        new BackupAsyncTask().execute(googleDriveService);
//                        new ClearAsyncTask().execute(googleDriveService);
                } else if (dialog.isShowing())
                    dialog.cancel();

            }
        });

        findViewById(R.id.restore_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dialog.isShowing()) {
                    dialog.setMessage("Restoring your data...");
                    dialog.show();
                }
                if (checkConnection()) {
                    boolean isLoggedIn = checkLogin();
                    if (isLoggedIn)
                        new RestoreAsyncTask().execute(googleDriveService);
                }
            }
        });

        changeAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("Please wait...");
                dialog.show();
                if (checkConnection()) {
                    signOut();
                }
            }
        });

    }

    private boolean checkConnection() {
        if (InternetConnection.checkConnection(this)) {
            return true;
        } else {
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

    private boolean checkLogin() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Toast.makeText(this, "Please Sign in first", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void signOut() {
        GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();

        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                signIn();
            }
        });
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        return mGoogleSignInClient;
    }

    private void signIn() {
        GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog.isShowing())
            dialog.cancel();

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignin.getInstance().setAccount(account);

            if (account != null) {
                emailTextView.setText(account.getEmail());
                changeAccountBtn.setText("Change Account");
                Toast.makeText(this, "successful", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, GoogleSigninActivity.class));
            }

        } catch (ApiException e) {
            Log.d("Signin", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            emailTextView.setText("Not signed in");
            changeAccountBtn.setText("Sign in");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    class BackupAsyncTask extends AsyncTask<Drive, Void, File> {

        @Override
        protected File doInBackground(Drive... drives) {

            //deleting any previous backups
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            String fileID = preferences.getString("fileID", null);
            if (fileID != null) {
                FileList files = null;
                try {
                    files = drives[0].files().list()
                            .setSpaces("appDataFolder")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageSize(10)
                            .execute();

                    for (File file : files.getFiles()) {
                        if (file.getId().equals(fileID) || file.getName().equals("note.db")) {
                            try {
                                drives[0].files().delete(file.getId()).execute();
                            } catch (IOException e) {
                                Log.d(TAG, "An error occurred while deleting file: " + e);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            File fileMetadata = new File();
            fileMetadata.setName("note.db");
            fileMetadata.setMimeType("application/x-sqlite3");
            fileMetadata.setParents(Collections.singletonList("appDataFolder"));
            java.io.File filePath = getApplicationContext().getDatabasePath("note.db");
            Log.d("Path", getApplicationContext().getDatabasePath("note.db").toString());

            FileContent mediaContent = new FileContent("application/x-sqlite3", filePath);
            File file = null;
            try {
                file = drives[0].files()
                        .create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                Log.d("upload", file.getId());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (dialog.isShowing())
                dialog.cancel();

            if (file != null) {
                String fileID = file.getId();
                SharedPreferences preferences = getApplicationContext().getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                preferences.edit().putString("fileID", fileID).apply();
                Log.d("id", fileID);
                Toast.makeText(BackupRestoreActivity.this, "Backup successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BackupRestoreActivity.this, "Backup failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class RestoreAsyncTask extends AsyncTask<Drive, Void, Integer> {

        @Override
        protected Integer doInBackground(Drive... services) {
            FileList files;
            File returnFile = null;
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            String fileID = preferences.getString("fileID", "");
            if (fileID != null)
                Log.d("id from preference", fileID);
            try {
                files = services[0].files().list()
                        .setSpaces("appDataFolder")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageSize(10)
                        .execute();

                for (File file : files.getFiles()) {
                    System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
                    if (fileID.equals(file.getId()) || file.getName().equals("note.db")) {
                        String directory = getApplicationContext().getDatabasePath("note.db").getParent();
                        Log.d("dir", directory);
                        String fileName = "note.db";
                        java.io.File path = new java.io.File(directory + "/" + fileName);
                        OutputStream outputStream = new FileOutputStream(path);
                        if(fileID.equals(""))
                            fileID = file.getId();
                        services[0].files().get(fileID)
                                .executeMediaAndDownloadTo(outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Log.d("directory", path.getAbsolutePath());
                        Log.d("writeToDB", "success");
                    }
                }

//                String fileId = returnFile.getId();

            } catch (Exception e) {
                Log.e("exception", e + " ");
                return 0;
            } finally {
                finish();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (dialog.isShowing())
                dialog.cancel();

            if (integer == 1)
                Toast.makeText(BackupRestoreActivity.this, "Restored", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(BackupRestoreActivity.this, "failed", Toast.LENGTH_SHORT).show();
        }
    }



}
