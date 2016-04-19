package com.androiddev.thirtyseven.studybuddy.Sessions.Chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.SessionActivity;
import com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard.WhiteboardView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class ChatFragment extends Fragment {

    private TextView mView;
    private String name;
    private EditText mInput;
    private Socket mSocket;
    private String sessionId;
    {
        try {
            //Connects to a socket on the server
            mSocket = IO.socket(ServerConnection.IP + ":8000");
        } catch (URISyntaxException e) {}
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        //For when an the android recieves a message from
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        String username;
                        String message;
                        Log.e("data", data.toString());
                        try {
                            if (!data.getString("session").equals(sessionId)) {
                                return;
                            }
                            username = data.getString("name");
                            message = data.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        // add the message to view
                        Log.e("Message", message);
                        addMessage(username, message);
                    }
                });
            }catch(NullPointerException e)
            {
                Log.d("onNewBitmap", "Null pointer exception (Chat) getActivity.runOnUiThread.");
            }
        }
    };




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        mInput = (EditText) rootView.findViewById(R.id.chat_edit_text);
        mView = (TextView) rootView.findViewById(R.id.chat_text_view);
        mView.setMovementMethod(new ScrollingMovementMethod());
        Button sendBtn = (Button) rootView.findViewById(R.id.chat_send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        name = pref.getString("name", "None");
        sessionId = ((SessionActivity) getActivity()).getSessionId();
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        mSocket.on("message", onNewMessage);
        mSocket.connect();

    }

    private void attemptSend() {
        String message = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mInput.setText("");

        //mView.setText(mView.getText().toString() + '\n' + message);
        try {
            JSONObject json = new JSONObject("{msg:\" " + message +"\",name:\""+name+"\",session:\""+sessionId+"\"}");
            Log.e("senddata",json.toString());
            mSocket.emit("new message", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(String username, String message) {
        Toast.makeText(getContext(), "Test", Toast.LENGTH_LONG);
        mView.setText(mView.getText().toString() + '\n' + username + ":" + message);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
}
