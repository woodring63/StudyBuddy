package com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.androiddev.thirtyseven.studybuddy.R;

import java.util.ArrayList;

/**
 * Created by Joseph Elliott on 3/22/2016.
 */
public class ColorDialogFragment extends DialogFragment {

    private static final int NUMBER_OF_COLORS = 5;

    private ArrayList<String> colors = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize the colors
        initializeColors();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a color!")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // send the color to the whiteboard activity
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setItems((String[]) colors.toArray(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                // red
                                break;
                            case 1:
                                // orange
                                break;
                            case 2:
                                // yellow
                                break;
                            case 3:
                                // green
                                break;
                            case 4:
                                // blue
                                break;
                            case 5:
                                // indigo
                                break;
                            case 6:
                                // violet
                                break;
                            case 7:
                                // black
                                break;
                            case 8:
                                // white
                                break;
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void initializeColors() {
        colors.add("red");
        colors.add("orange");
        colors.add("yellow");
        colors.add("green");
        colors.add("blue");
        colors.add("indigo");
        colors.add("violet");
        colors.add("black");
        colors.add("white");
    }
}
