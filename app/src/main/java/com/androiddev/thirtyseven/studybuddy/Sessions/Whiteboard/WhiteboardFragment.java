package com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs.ColorDialogFragment;
import com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs.SizeDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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
        // TODO create an async task for saving to the server
        // TODO call saveToServer
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

    private void saveToServer() {
        Bitmap bitmap = whiteboard.getCanvasBitMap();
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        byte[] array = buffer.array();
        // TODO do a PostRequest
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
