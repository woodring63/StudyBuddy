package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class DocumentFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private EditText text = null;
    private GoogleApiClient mGoogleApiClient = null;
    private Activity activity = null; // Used to pass a context
    private ArrayList<Metadata> driveFiles = null;
    private Metadata openFile = null;
    //private SharedPreferences prefs;

    static final String TAG = "DocumentFragment";
    private static final int REQUEST_CODE_LOAD = 1;
    private static final int REQUEST_CODE_SAVE = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_document, container, false);
        text = (EditText) view.findViewById(R.id.drive_file_text_display);
        Button loadButton = (Button) view.findViewById(R.id.load_button);
        Button clearButton = (Button) view.findViewById(R.id.clear_button);
        Button saveButton = (Button) view.findViewById(R.id.save_button);

        //prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        activity = getActivity();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                //.setAccountName(prefs.getString("email", "None"))
                .setAccountName("nlg@iastate.edu")
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
                    // Make a query
                    Query query = new Query.Builder()
                            .addFilter(Filters.or(Filters.ownedByMe(), Filters.sharedWithMe()))
                            .build();

                    // Execute the query
                    Drive.DriveApi.query(mGoogleApiClient, query)
                            .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                                @Override
                                public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                                    // Iterate through the Buffer and add the items to the ArrayList
                                    driveFiles = new ArrayList<Metadata>();
                                    ArrayList<String> fileNames = new ArrayList<String>();
                                    Iterator<Metadata> iterator = metadataBufferResult
                                            .getMetadataBuffer().iterator();
                                    while (iterator.hasNext()) {
                                        Metadata metadata = iterator.next();
                                        driveFiles.add(metadata);
                                        fileNames.add(metadata.getTitle());
                                    }

                                    // Open a new FileList Activity
                                    Intent intent = new Intent(activity, FileList.class);
                                    intent.putStringArrayListExtra("names", fileNames);
                                    startActivityForResult(intent, REQUEST_CODE_LOAD);
                                }
                            });
                }
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
                if (mGoogleApiClient.isConnecting()) {
                    Toast.makeText(activity,
                            "Study Buddy is still connecting to Google Drive. Please wait.",
                            Toast.LENGTH_LONG).show();
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(activity,
                            "Study Buddy is not connected to Google Drive. Please try again later.",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (openFile != null) {
                        // Deal with file already open
                    }
                    // Otherwise make a new file
                    else {
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
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOAD:
                if (resultCode == Activity.RESULT_OK) {
                    // Load file
                    final Metadata metadata = driveFiles.get(data.getIntExtra("position", 0));
                    DriveFile file = metadata.getDriveId().asDriveFile();

                    if (metadata.getFileExtension() != "txt" && metadata.getFileExtension() != ".txt") {
                        Log.d(TAG, "Tried to load non-txt file.");
                        Toast.makeText(activity, "You can only load files with the \".txt\" extension.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    file.open(mGoogleApiClient, DriveFile.MODE_READ_WRITE, null)
                            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                            if (!(driveContentsResult.getStatus().isSuccess())) {
                                Log.d(TAG, "Could not open file " + metadata.getTitle());
                            }
                        }
                    });
                }
                break;
            case REQUEST_CODE_SAVE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "File save successful");
                    Toast.makeText(activity, "File saved!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(activity, "Google Drive connection failed. Please try again later.", Toast.LENGTH_LONG).show();
        }
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
    public void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onResume();
    }

    public class FileList extends AppCompatActivity {

        Activity thisActivity;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_file_list);

            thisActivity = this;

            ArrayList<String> names = getIntent().getStringArrayListExtra("names");
            FileListAdapter adapter = new FileListAdapter(this, names);
            ListView fileList = (ListView) findViewById(R.id.file_list);
            fileList.setAdapter(adapter);

            fileList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent data = new Intent();
                            data.putExtra("position", position);
                            thisActivity.setResult(Activity.RESULT_OK, data);
                            thisActivity.finish();
                        }
                    }
            );


        }

        public class FileListAdapter extends ArrayAdapter<String> {

            private final Activity context;
            private final ArrayList<String> names;

            FileListAdapter(Activity context, ArrayList<String> names) {
                super(context, R.layout.file_list_layout, names);

                this.context = context;
                this.names = names;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = context.getLayoutInflater();
                View fileListView = inflater.inflate(R.layout.file_list_layout, null, true);

                ImageView fileIcon = (ImageView) findViewById(R.id.file_icon);
                TextView fileName = (TextView) findViewById(R.id.file_name);

                fileName.setText(names.get(position));
                fileIcon.setImageResource(R.drawable.defult_text_file_icon);

                return fileListView;
            }
        }

    }
}
