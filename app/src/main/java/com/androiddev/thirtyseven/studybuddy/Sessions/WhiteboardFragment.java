package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs.ColorDialogFragment;
import com.androiddev.thirtyseven.studybuddy.Sessions.Dialogs.SizeDialogFragment;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class WhiteboardFragment extends Fragment {

    //private static final int SEEKBAR_MAX = 10;
    //private static final int SEEKBAR_START_PEN = (int) Math.ceil((double) SEEKBAR_MAX / 2.0);

    private WhiteboardView whiteboard;
    private Button btnColor;
    private Button btnSize;
    //private SeekBar seekBar;

    private WhiteboardFragment thisFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_whiteboard, container, false);

        try {
            whiteboard = (WhiteboardView) rootView.findViewById(R.id.whiteboard);
            btnColor = (Button) rootView.findViewById(R.id.btn_color);
            btnSize = (Button) rootView.findViewById(R.id.btn_size);
            thisFragment = this;
            initializeColorButton();
            initializeSizeButton();
        } catch (NullPointerException e) {
            // rip in pieces
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
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

    public void setColor(String color) {
        // TODO FIX COLORS
        switch(color) {
            case "red":
                whiteboard.setPenColor(Color.RED);
                break;
            case "orange":
                whiteboard.setPenColor(Color.RED);
                break;
            case "yellow":
                whiteboard.setPenColor(Color.YELLOW);
                break;
            case "green":
                whiteboard.setPenColor(Color.GREEN);
                break;
            case "blue":
                whiteboard.setPenColor(Color.BLUE);
                break;
            case "indigo":
                whiteboard.setPenColor(Color.BLUE);
                break;
            case "violet":
                whiteboard.setPenColor(Color.BLUE);
                break;
            case "black":
                whiteboard.setPenColor(Color.BLACK);
                break;
            case "white":
                whiteboard.setPenColor(Color.WHITE);
                break;
            default:
                whiteboard.setPenColor(Color.BLACK);
        }
    }

    public void setSize(int size) {
        whiteboard.setStrokeWidth((float) size);
    }

    /*
    private void initializeSeekBar() {
        seekBar.setMax(SEEKBAR_MAX);
        seekBar.setProgress(SEEKBAR_START_PEN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= SEEKBAR_START_PEN) {
                    // We are using the pen, anything greater will increase pen size.
                    whiteboard.setEraser(false);
                    whiteboard.setStrokeWidth(((float) progress) * 5f);
                } else {
                    whiteboard.setEraser(true);
                    whiteboard.setStrokeWidth(((float) SEEKBAR_MAX - (float) progress) * 5f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    */
}
