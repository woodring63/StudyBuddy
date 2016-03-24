package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class DocumentFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "DocumentFragment";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        return inflater.inflate(R.layout.fragment_document, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this.getActivity(), REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(this.getActivity(), "Unable to resolve connection", Toast.LENGTH_SHORT);
            }
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this.getActivity(), 0).show();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:
                break;
            case REQUEST_CODE_CREATOR:
                break;
            case REQUEST_CODE_RESOLUTION:
                //if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                //}
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
