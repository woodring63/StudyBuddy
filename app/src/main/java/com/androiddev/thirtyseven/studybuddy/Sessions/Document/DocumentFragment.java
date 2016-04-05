package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.CreateFileActivityBuilder;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class DocumentFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private EditText text = null;
    private GoogleApiClient mGoogleApiClient = null;
    private Activity activity = null; // Used to pass a context
    private Metadata openFile = null;
    private SharedPreferences prefs;

    static final String TAG = "DocumentFragment";
    private static final int REQUEST_CODE_LOAD = 1;
    private static final int REQUEST_CODE_SAVE = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_document, container, false);
        activity = getActivity();
        text = (EditText) view.findViewById(R.id.drive_file_text_display);
        Button loadButton = (Button) view.findViewById(R.id.load_button);
        Button clearButton = (Button) view.findViewById(R.id.clear_button);
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .setAccountName(prefs.getString("email", "None"))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnecting()) {
                    Toast.makeText(activity,
                            "Study Buddy is still connecting to Google Drive. Please wait.",
                            Toast.LENGTH_LONG).show();
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(activity,
                            "Study Buddy is not connected to Google Drive. Please try again later.",
                            Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                    // Set the title
                    alertDialogBuilder.setTitle("WARNING");

                    // Set the message and buttons
                    alertDialogBuilder
                            .setMessage("All unsaved changes will be discarded!")
                            .setCancelable(false)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    save();
                                    load();
                                }
                            })
                            .setNegativeButton("Discard Changes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    load();
                                }
                            });

                    // Create the actual AlertDialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // And show it
                    alertDialog.show();
                }
            }
        });

        // Sets the text on the screen to "" after verifying that is is what the user wants to do
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        // Saves the current state of text to the drive as a new file
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnecting()) {
                    Toast.makeText(activity,
                            "Study Buddy is still connecting to Google Drive. Please wait.",
                            Toast.LENGTH_LONG).show();
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(activity,
                            "Study Buddy is not connected to Google Drive. Please try again later.",
                            Toast.LENGTH_LONG).show();
                } else {
                    save();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult Fragment");
        switch (requestCode) {
            case REQUEST_CODE_LOAD:
                if (resultCode == Activity.RESULT_OK) {
                    // Load file
                    Log.d(TAG, "File successfully opened");
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    driveId.asDriveResource().getMetadata(mGoogleApiClient)
                            .setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                @Override
                                public void onResult(DriveResource.MetadataResult metadataResult) {
                                    openFile = metadataResult.getMetadata();openFile.getDriveId().asDriveFile().open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                                            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                                @Override
                                                public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                                                    if (driveContentsResult == null) {
                                                        Log.d(TAG, "Could not retrieve file contents");
                                                        Toast.makeText(activity, "Could not retrieve file contents. Please try again.",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                    else {
                                                        Scanner in = new Scanner(driveContentsResult.getDriveContents().getInputStream());
                                                        String fileText = "";
                                                        while (in.hasNextLine()) {
                                                            fileText += in.nextLine() + "\n";
                                                        }
                                                        fileText = fileText.substring(0, fileText.length() - 1);
                                                        text.setText(fileText);
                                                        in.close();
                                                    }
                                                }
                                            });
                                }
                            });
                }
                else {
                    Log.d(TAG, "Failed to load file");
                    Toast.makeText(activity, "Load failed. Please try again.", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_SAVE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "File save successful");
                    Toast.makeText(activity, "File saved!", Toast.LENGTH_LONG).show();
                    DriveId driveId = data.getParcelableExtra(CreateFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    driveId.asDriveResource().getMetadata(mGoogleApiClient)
                            .setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                @Override
                                public void onResult(DriveResource.MetadataResult metadataResult) {
                                    openFile = metadataResult.getMetadata();
                                }
                    });
                }
                else {
                    Log.d(TAG, "File save unsuccessful. Result Code: " + resultCode);
                    Toast.makeText(activity, "Save failed. Please try again.", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_RESOLUTION:
                // Sign-in failed
                if (!(resultCode == Activity.RESULT_OK)) {
                    Toast.makeText(activity, "Sing-in failed. Please try again later.", Toast.LENGTH_LONG).show();
                }
                // Sign-in successful, retry connection
                else {
                    mGoogleApiClient.connect();
                }
                break;
            default:
                Log.d(TAG, "Request Code not found");
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google Drive connection successful");
    }

    // Handles when the connection goes down
    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case CAUSE_NETWORK_LOST:
                Log.d(TAG, "Connection Suspended: Network Lost");
                Toast.makeText(activity, "Network lost. Please try again later.", Toast.LENGTH_LONG).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Log.d(TAG, "Connection Suspended: Service Disconnected");
                Toast.makeText(activity, "Service disconnected. Please try again later.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    // Handles a failed connection
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // If there is a resolution, try to resolve it
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
            }
            catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.toString());
            }
        }
        else {
            Log.d(TAG, "Connection failed");
            Toast.makeText(activity, "Google Drive connection failed. Please try again later.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .setAccountName(prefs.getString("username", "None"))
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    private void load() {
        Log.d(TAG, "load");
        String[] mimeType = {"text/plain"};
        IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder()
                .setActivityStartFolder(Drive.DriveApi.getRootFolder(mGoogleApiClient).getDriveId())
                .setMimeType(mimeType)
                .build(mGoogleApiClient);
        try {
            activity.startIntentSenderForResult(
                    intentSender, REQUEST_CODE_LOAD, null, 0, 0, 0);
        }
        catch (IntentSender.SendIntentException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void clear() {
        Log.d(TAG, "clear");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        // Set the title
        alertDialogBuilder.setTitle("Clear text?");

        // Set the message and buttons
        alertDialogBuilder
                .setMessage("Are you sure you want to erase everything?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        text.setText("");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Create the actual AlertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // And show it
        alertDialog.show();
    }

    private void save() {
        Log.d(TAG, "save");
        if (openFile != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

            // Set the title
            alertDialogBuilder.setTitle("Save");

            // Set the message and buttons
            alertDialogBuilder
                    .setMessage("Saving will overwrite " + openFile.getTitle() + ". Do you wish to continue?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Delete the open file and create a new version of it with the same name
                            // New contents
                            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                        @Override
                                        public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                                            // If unsuccessful
                                            if (!driveContentsResult.getStatus().isSuccess()) {
                                                Log.d(TAG, "Could not create new drive contents");
                                            }
                                            // Otherwise continue
                                            try {
                                                FileOutputStream fileOutputStream = new FileOutputStream(driveContentsResult
                                                        .getDriveContents()
                                                        .getParcelFileDescriptor()
                                                        .getFileDescriptor());
                                                Writer writer = new OutputStreamWriter(fileOutputStream);
                                                writer.write(text.getText().toString());
                                                writer.close();
                                                fileOutputStream.close();
                                            } catch (IOException e) {
                                                Log.e(TAG, e.toString());
                                            }

                                            // Create the MetadataChangeSet for file info
                                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                    .setTitle(openFile.getTitle())
                                                    .setMimeType("text/plain")
                                                    .build();

                                            // Create the file
                                            Drive.DriveApi.getRootFolder(mGoogleApiClient).createFile(
                                                    mGoogleApiClient, changeSet, driveContentsResult.getDriveContents())
                                                    .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                        @Override
                                                        public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                                            if (driveFileResult == null) {
                                                                Log.d(TAG, "Could not overwrite file " + openFile.getTitle());
                                                                Toast.makeText(activity, "Save failed. Please try again.", Toast.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                            else {
                                                                // Delete current openFile
                                                                Log.d(TAG, "File " + openFile.getTitle() + " successfully overwritten");
                                                                Toast.makeText(activity, "File saved!" , Toast.LENGTH_LONG).show();
                                                                // CANNOT BE UNDONE!!
                                                                openFile.getDriveId().asDriveResource().delete(mGoogleApiClient);
                                                                driveFileResult.getDriveFile().getMetadata(mGoogleApiClient)
                                                                        .setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                                                            @Override
                                                                            public void onResult(DriveResource.MetadataResult metadataResult) {
                                                                                openFile = metadataResult.getMetadata();
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Create the actual AlertDialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // And show it
            alertDialog.show();
        }
        // Otherwise make a new file
        else {
            // Make new contents to save to the drive
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(DriveApi.DriveContentsResult driveContentsResult) {// If unsuccessful
                            if (!driveContentsResult.getStatus().isSuccess()) {
                                Log.d(TAG, "Could not create new drive contents");
                            }
                            // Otherwise continue
                            else {
                                // Set contents
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(driveContentsResult
                                            .getDriveContents()
                                            .getParcelFileDescriptor()
                                            .getFileDescriptor());
                                    Writer writer = new OutputStreamWriter(fileOutputStream);
                                    writer.write(text.getText().toString());
                                    writer.close();
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    Log.e(TAG, e.toString());
                                }

                                // Create the MetadataChangeSet for file info
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle("Session Document.txt")
                                        .setMimeType("text/plain")
                                        .build();

                                // Start the create file activity
                                IntentSender intentSender = Drive.DriveApi
                                        .newCreateFileActivityBuilder()
                                        .setInitialMetadata(changeSet)
                                        .setInitialDriveContents(driveContentsResult.getDriveContents())
                                        .build(mGoogleApiClient);
                                try {
                                    activity.startIntentSenderForResult(
                                            intentSender, REQUEST_CODE_SAVE, null, 0, 0, 0);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }
                    });
        }
    }
}
