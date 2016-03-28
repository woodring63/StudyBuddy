package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class DocumentFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    EditText text;
    GoogleApiClient mGoogleApiClient;
    DriveId driveId;

    private final Activity activity = getActivity(); // For use in nested objects
    static final String TAG = "DocumentFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_document, container, false);
        text = (EditText) view.findViewById(R.id.drive_file_text_display);
        Button loadButton = (Button) view.findViewById(R.id.load_button);
        Button clearButton = (Button) view.findViewById(R.id.clear_button);
        Button saveButton = (Button) view.findViewById(R.id.save_button);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .setAccountName("nlg@iastate.edu")
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Sets the text on the screen to "" after verifying that is is what the user wants to do
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        // Saves the current state of text to the drive as a new file
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Make new contents to save to the drive
                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        // If unsuccessful
                        if (!driveContentsResult.getStatus().isSuccess()) {
                            Toast.makeText(activity,
                                    "Could not create new drive contents. Please try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                        // Otherwise continue
                        else {
                            // Set contents
                            try {
                            FileWriter writer = new FileWriter(driveContentsResult
                                    .getDriveContents()
                                    .getParcelFileDescriptor()
                                    .getFileDescriptor());
                                writer.write(text.getText().toString());
                            } catch (IOException e) {
                                // rip in pieces
                            }

                            // Create the MetadataChangeSet for file info
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("Session Document.txt")
                                    .setMimeType("text/plain")
                                    .build();

                            driveId = Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient,
                                            changeSet,
                                            driveContentsResult.getDriveContents())
                                    .await().getDriveFile().getDriveId();
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(activity, "Connection successful!", Toast.LENGTH_LONG).show();
    }

    // Handles when the connection goes down
    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case CAUSE_NETWORK_LOST:
                Toast.makeText(activity, "Network lost. Please try again later.", Toast.LENGTH_LONG).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Toast.makeText(activity, "Service disconnected. Please try again later.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    // Handles a failed connection
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(activity, "Connection failed. Please try again later.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .setAccountName("nlg@iastate.edu")
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .setAccountName("nlg@iastate.edu")
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onResume();
    }
}
