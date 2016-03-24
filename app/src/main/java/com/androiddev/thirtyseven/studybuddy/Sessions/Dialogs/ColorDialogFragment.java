package com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.WhiteboardFragment;

import java.util.ArrayList;

/**
 * Created by Joseph Elliott on 3/22/2016.
 */
public class ColorDialogFragment extends DialogFragment {

    private static final int NUMBER_OF_COLORS = 5;
    private String currentColor;

    private ArrayList<String> colors = new ArrayList<>();

    private WhiteboardFragment parentWhiteboardFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize the colors
        initializeColors();

        final String[] colorsToString = new String[colors.size()];
        for (int i = 0; i < colors.size(); i++) {
            colorsToString[i] = colors.get(i);
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a color!")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setItems(colorsToString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentColor = colors.get(which);
                        parentWhiteboardFragment.setColor(currentColor);
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

    public void setParentWhiteboardFragment(WhiteboardFragment parentWhiteboardFragment) {
        this.parentWhiteboardFragment = parentWhiteboardFragment;
    }

}
