package com.the_spartan.virtualdiary.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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
import com.the_spartan.virtualdiary.activity.GoogleSigninActivity;
import com.the_spartan.virtualdiary.activity.MainActivity;
import com.the_spartan.virtualdiary.connection.InternetConnection;
import com.the_spartan.virtualdiary.util.GoogleSignin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;


public class BackupRestoreFragment extends Fragment {

    public static final String TAG = "BackupRestoreFragment";
    public static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    TextView emailTextView;
    ImageView DPImageView;
    Button changeAccountBtn;
    private Button backupButton;
    private Drive mDriveServiceHelper;
    private Button restoreButton;
    private Toolbar toolbar;
    MainActivity mainActivity;

    ProgressDialog dialog;
    com.google.api.services.drive.Drive googleDriveService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup_restore, container, false);

        emailTextView = view.findViewById(R.id.email_textview);
        changeAccountBtn = view.findViewById(R.id.change_account_btn);
        backupButton = view.findViewById(R.id.backup_btn);
        restoreButton = view.findViewById(R.id.restore_btn);

        toolbar = view.findViewById(R.id.my_toolbar);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity = (MainActivity)getActivity();
        mainActivity.setToolbar(toolbar);
        mainActivity.setTitle("");

        backupButton.setOnClickListener(new View.OnClickListener() {
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

        restoreButton.setOnClickListener(new View.OnClickListener() {
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

        dialog = new ProgressDialog(getContext());



        account = GoogleSignIn.getLastSignedInAccount(getContext());
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        getContext(), Collections.singleton(DriveScopes.DRIVE_APPDATA));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean checkConnection() {
        if (InternetConnection.checkConnection(getContext())) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account == null) {
            Toast.makeText(getContext(), "Please Sign in first", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void signOut() {
        GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

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
                Toast.makeText(getContext(), "successful", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(getContext(), GoogleSigninActivity.class));
            }

        } catch (ApiException e) {
            Log.d("Signin", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
            emailTextView.setText("Not signed in");
            changeAccountBtn.setText("Sign in");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home)
//            onBackPressed();

        return true;
    }

    class BackupAsyncTask extends AsyncTask<Drive, Void, File> {

        @Override
        protected File doInBackground(Drive... drives) {

            //deleting any previous backups
            SharedPreferences preferences = getContext().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
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
            java.io.File filePath = getContext().getDatabasePath("note.db");
            Log.d("Path", getContext().getDatabasePath("note.db").toString());

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
                SharedPreferences preferences = getContext().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
                preferences.edit().putString("fileID", fileID).apply();
                Log.d("id", fileID);
                Toast.makeText(getContext(), "Backup successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Backup failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class RestoreAsyncTask extends AsyncTask<Drive, Void, Integer> {

        @Override
        protected Integer doInBackground(Drive... services) {
            FileList files;
            File returnFile = null;
            SharedPreferences preferences = getContext().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
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
                        String directory = getContext().getDatabasePath("note.db").getParent();
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
//                finish();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (dialog.isShowing())
                dialog.cancel();

            if (integer == 1)
                Toast.makeText(getContext(), "Restored", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
        }
    }



}
