package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;
import com.androiddev.thirtyseven.studybuddy.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;



public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    private Boolean exists;
    private JSONObject j;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mStatusTextView = (TextView) findViewById(R.id.status);

        //Gets all 3 buttons
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.returnButton).setOnClickListener(this);

        //Build a sign in options, requesting email
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();


        //Client so we can use Google's Sign in API
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Setup Sign In Button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
    }

    //Logs failed connections
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //On page start
    @Override
    public void onStart() {
        super.onStart();


        //Check if they were previously signed in, handle this sign in
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        //Otherwise, load and prepare for a new sign in
        else {

            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    //Define what happens when log in attempted
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //On success
        if (requestCode == RC_SIGN_IN) {
            //Handle the Sign in
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            //TO DO: Finish Async Task


            exists = false;
            final String my_params = "/users/username/woodring@iastate.edu";
            AsyncTask a = new AsyncTask<Object, Void, Boolean>() {


                @Override
                protected Boolean doInBackground(Object... params) {

                    ServerConnection s = new ServerConnection(my_params);
                    j = s.run();
                    return true;

                }
                @Override
                protected void onPostExecute(Boolean result) {

                    String str = "";
                    try {
                        str = j.getString("name");
                    }
                    catch(Exception e){
                    }
                    if(result){
                         Toast.makeText(getApplicationContext(), str  ,Toast.LENGTH_LONG).show();

                    }

                }


            };
            a.execute();
            //Move to Hub
            Intent i;
            if(exists == false) {
                i = new Intent(getApplicationContext(), NoValidAccount.class);
            }
            else{
                i = new Intent(getApplicationContext(), HubActivity.class);
            }
            startActivity(i);
        }
    }

    //What happens on sign in
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        //On success, update the UI to "true"
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);

        }
        //On fail, update the UI to "false"

        else {
            updateUI(false);
        }
    }

    //When signing in, make an intent to sign in and ask for a result (used to check if successful)
    private void signIn() {


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    //On sign out, update UI to false
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                updateUI(false);
            }
        });

    }

    //returns to hub
    private void returnToApp() {

        Intent i = new Intent(getApplicationContext(), HubActivity.class);
        startActivity(i);
    }


    //Shows progress dialog when needed. Makes a new one and displays it
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    //hides progress dialog
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    private void updateUI(boolean signedIn) {
        //On signed in, remove sign in button, show sign out and return

        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_return).setVisibility(View.VISIBLE);

        }
        //On signed out, show sign in button, remove sign out and return

        else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_return).setVisibility(View.GONE);
        }
    }

    //Sets different relevant OnClickListeners
    @Override
    public void onClick(View v) {
        if (isNetworkAvailable()) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    signIn();
                    break;
                case R.id.sign_out_button:
                    signOut();
                    break;
                case R.id.returnButton:
                    returnToApp();
                    break;
            }
        } else {
            switch(v.getId()){
                case R.id.sign_out_button:
                    signOut();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

            }

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

class NewUserAsync extends AsyncTask<Void, Void, Boolean> {

    ServerConnection server;
    Boolean exists;

    public NewUserAsync(String method, String params)
    {
        server = new ServerConnection(params);
        exists = false;

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        JSONObject json = server.run();
        if(1 == 1){
            exists = true;
        }
        return exists;
    }
    public void onPostExecute(Boolean result)
    {

    }



}

