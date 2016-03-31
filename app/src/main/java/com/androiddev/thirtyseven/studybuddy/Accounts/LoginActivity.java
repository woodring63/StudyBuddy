package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private String email;
    private SharedPreferences.Editor editor;



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


            GoogleSignInAccount acct = result.getSignInAccount();
            email = acct.getEmail();
            editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("email", email);
            editor.commit();

            exists = false;
            final String my_params = "/users/username/"+ email;
            AsyncTask a = new AsyncTask<Object, Void, Boolean>() {


                @Override
                protected Boolean doInBackground(Object... params) {


                    ServerConnection s = new ServerConnection(my_params);
                    j = s.run();

                    try {
                        Log.v("Login", j.getJSONObject("user").getString("name"));
                        exists = true;

                    } catch (Exception e) {
                        Log.v("Login", "NOPE");
                        exists = false;
                    }
                    try {
                        editor.putString("id", j.getJSONObject("user").getString("_id"));
                        editor.putString("name", j.getJSONObject("user").getString("name"));
                        editor.putString("bio", j.getJSONObject("user").getString("bio"));
                        editor.putString("major", j.getJSONObject("user").getString("major"));
                        editor.commit();

                    }
                    catch(Exception e){

                    }

                    return exists;

                }

            };
            try {
                exists = (Boolean) a.execute().get();
            }
            catch(Exception e){

            }
            Log.v("EXESTENCE", exists.toString());
            //Move to Hub
            Intent i;
            if (!exists) {
                i = new Intent(getApplicationContext(), NoValidAccount.class);
            } else {
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
            switch (v.getId()) {
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
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
}

