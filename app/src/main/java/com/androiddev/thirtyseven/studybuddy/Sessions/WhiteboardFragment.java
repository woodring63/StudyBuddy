package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.androiddev.thirtyseven.studybuddy.R;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class WhiteboardFragment extends Fragment {

    private static final int SEEKBAR_MAX = 10;
    private static final int SEEKBAR_START_PEN = (int) Math.ceil((double) SEEKBAR_MAX / 2.0);

    private WhiteboardView whiteboard;
    private SeekBar seekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_whiteboard, container, false);

        try {
            whiteboard = (WhiteboardView) rootView.findViewById(R.id.whiteboard);
            seekBar = (SeekBar) rootView.findViewById(R.id.seekbar);
            initializeSeekBar();
        } catch (NullPointerException e) {
            // rip in pieces
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
    }


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

}
