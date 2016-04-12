package com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;


import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs.ColorDialogFragment;
import com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs.SizeDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class WhiteboardFragment extends Fragment {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private WhiteboardView whiteboard;
    private Button btnColor;
    private Button btnSize;
    private Button btnEraserToggle;
    private Button btnDownload;

    private WhiteboardFragment thisFragment;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.26.52.185:8000");
        } catch (URISyntaxException e) {}
    }


    private Emitter.Listener onNewBitmap = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String encodedBitmap;
                    try {
                        encodedBitmap = data.getString("image");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    updateBitmap(encodedBitmap);
                }
            });
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_whiteboard, container, false);

        try {
            whiteboard = (WhiteboardView) rootView.findViewById(R.id.whiteboard);
            btnColor = (Button) rootView.findViewById(R.id.btn_color);
            btnSize = (Button) rootView.findViewById(R.id.btn_size);
            btnEraserToggle = (Button) rootView.findViewById(R.id.btn_eraser_toggle);
            btnDownload = (Button) rootView.findViewById(R.id.btn_download);
            thisFragment = this;
            initializeColorButton();
            initializeSizeButton();
            initializeEraserToggleButton();
            initializeDownloadButton();
        } catch (NullPointerException e) {
            // rip in pieces
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        mSocket.on("new bitmap", onNewBitmap);
        mSocket.connect();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                attemptSend();
            }
        },0,3000);//Update text every 3 seconds
    }

    private void attemptSend() {
        try {
            Bitmap image = whiteboard.getCanvasBitMap();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.URL_SAFE);

            JSONObject obj = new JSONObject("{image:" + "\"" + encodedImage + "\"" + "}");
            mSocket.emit("new bitmap", obj);

        } catch (NullPointerException e) {
            Log.d("Null Pointer Exception", "Got a null pointer within attemptSend");
        } catch (JSONException e) {
            Log.d("JSONException", "Got a JSON Exception within attemptSend");
            e.printStackTrace();
        }
    }

    private void updateBitmap(String encodedBitmap) {
        byte[] decodedString = Base64.decode(encodedBitmap, Base64.URL_SAFE);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        whiteboard.setCanvasBitMap(decodedByte.copy(Bitmap.Config.ARGB_8888, true));
    }

    private void initializeColorButton() {
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDialogFragment cdf = new ColorDialogFragment();
                cdf.show(getFragmentManager(), "cdf");
                cdf.setParentWhiteboardFragment(thisFragment);
            }
        });
    }

    private void initializeSizeButton() {
        btnSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SizeDialogFragment sdf = new SizeDialogFragment();
                sdf.show(getFragmentManager(), "sdf");
                sdf.setParentWhiteboardFragment(thisFragment);
            }
        });
    }

    private void initializeEraserToggleButton() {
        btnEraserToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteboard.toggleEraser();
                if (whiteboard.isEraser()) {
                    btnEraserToggle.setText("ERASER");
                    Toast.makeText(getContext(), "eraser enabled", Toast.LENGTH_SHORT).show();
                } else {
                    btnEraserToggle.setText("PEN");
                    //setColor(latestColor);
                    Toast.makeText(getContext(), "pen enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeDownloadButton() {
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToExternalStorage();
            }
        });
    }

    public void setColor(String color) {
        whiteboard.setPaintColor(color);
        Toast.makeText(getContext(), "color set to " + color, Toast.LENGTH_SHORT).show();
    }

    public void setSize(int size) {
        whiteboard.setStrokeWidth((float) size);
        Toast.makeText(getContext(), "size set to " + size, Toast.LENGTH_SHORT).show();
    }

    private void saveToExternalStorage() {

        verifyStoragePermissions(getActivity());

        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/StudyBuddy");
        myDir.mkdirs();

        String fname = Long.toString(System.currentTimeMillis()) + ".png";

        File file = new File (myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            whiteboard.getCanvasBitMap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(getView(), "Error: FileNotFoundException.", Snackbar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(getView(), "Error: IOException.", Snackbar.LENGTH_SHORT).show();
        }

        // Notify the user
        Snackbar.make(getView(), fname + " saved to " + myDir, Snackbar.LENGTH_SHORT).show();
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
