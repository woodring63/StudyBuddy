package com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.androiddev.thirtyseven.studybuddy.Sessions.WhiteboardFragment;

import java.util.ArrayList;

/**
 * Created by Joseph Elliott on 3/22/2016.
 */
public class SizeDialogFragment extends DialogFragment {

    private static final int NUMBER_OF_SIZES = 10;
    private int currentSize;

    private ArrayList<Integer> sizes = new ArrayList<>();

    private WhiteboardFragment parentWhiteboardFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initializeSizes();

        final String[] sizesToString = new String[sizes.size()];
        for (int i = 0; i < sizes.size(); i++) {
            sizesToString[i] = "" + sizes.get(i);
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a size!")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setItems(sizesToString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentSize = sizes.get(which);
                        parentWhiteboardFragment.setSize(currentSize);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void initializeSizes() {
        for (int i = 1; i <= NUMBER_OF_SIZES; i++) {
            sizes.add(i);
        }
    }

    public void setParentWhiteboardFragment(WhiteboardFragment parentWhiteboardFragment) {
        this.parentWhiteboardFragment = parentWhiteboardFragment;
    }
}
