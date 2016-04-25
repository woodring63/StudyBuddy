package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.parser.Packet;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Nathan on 4/19/2016.
 */
public class Collaborator implements TextWatcher {


    /**
     * The sending takes place in attemptSendMutation. This is called from charChange, which used to be
     * onTextChanged, but then I realized that onTextChanged doesn't work lik I thought it did. The process
     * method is what actually changes the display. It calls transform on some mutations to fix "merge conflicts".
     * Transform is explained in Insert
     *
     * attamptSendText is called whenever the user receives one of their own mutations, seving as a confirmation that
     * the server has received it. According to Erica, this should store the string in the server/database. Then
     * pollForText runs the DocumentAsync, which should retrieve this text. I call pollForText in the Collaborator
     * constructor and in onResume in DocumentFragment.
     */
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(ServerConnection.IP + ":8000");
        } catch (URISyntaxException e) {}
    }


    private EditText textField;
    private String myID;
    private String sessionID;
    private Emitter.Listener onNewMutation;
    private ArrayList<Mutation> mutations;
    private int disregard;
    private int cursorPosition;
    private Mutation mutation;
    private TimerThread timerThread;
    protected volatile boolean sent;
    private String strBefore;
    private String strAfter;
    private String totalBefore;
    private HashMap<String, Integer> version;


    /**
     * An object used to manage mutations
     * @param textField - the EditText displaying the session Document
     * @param myID - the User's ID
     */
    public Collaborator(EditText textField, String myID, String sessionID, Emitter.Listener onNewMutation) {
        this.textField = textField;
        this.myID = myID;
        this.onNewMutation = onNewMutation;
        this.sessionID = sessionID;
        sent = false;
        mutations = new ArrayList<>();
        disregard = 1;
        mutation = null;
        textField.addTextChangedListener(this);
        mSocket.on("new mutation", onNewMutation);
        mSocket.connect();
        pollForText();
        version = new HashMap<>();
        version.put(myID, 0);
    }

    /**
     * Processes the given mutation, transforming it as necessary, then
     * applying it to text
     * @param mutation - the Mutation to process
     */
    public void process(Mutation mutation){
        Log.e("Mut","process is run");
        if (mutation.senderID.equals(myID)) {
            Log.e("Mut","this is run");
            attemptSendText(textField.getText().toString());
        }
        else {
            disregard++;
            // If either version misses the other user's ID, add that
            if (!mutation.version.containsKey(myID)) {
                mutation.version.put(myID, 0);
            }
            if (!version.containsKey(mutation.senderID)) {
                version.put(mutation.senderID, 0);
            }
            // Add the sender to version
            version.put(mutation.senderID, version.get(mutation.senderID) + 1);
            String text = textField.getText().toString();
            for (int i = mutation.version.get(myID); i < mutations.size(); ++i) {
                mutation.transform(mutations.get(i), myID);
            }
            switch (mutation.type) {
                case Mutation.MUTATION_INSERT:
                    text = text.substring(0, mutation.index) +
                            ((Insert) mutation).toInsert + text.substring(mutation.index);
                    break;
                case Mutation.MUTATION_DELETE:
                    if (((Delete) mutation).subDelete != null) {
                        version.put(mutation.senderID, ((Delete) mutation).subDelete.version.get(mutation.senderID) - 1);
                        process(((Delete) mutation).subDelete);
                        text = textField.getText().toString();
                    }
                    if (((Delete) mutation).numChars == text.length()) {
                        text = "";
                    }
                    else {
                        text = text.substring(0, mutation.index) + text.substring(mutation.end());
                    }
                    break;
                default:
                    break;
            }
            textField.setText(text);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Store the current word after change
        strBefore = s.toString().substring(start, start + count);
        totalBefore = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (totalBefore.equals(s.toString())) {
            return;
        }
        // Store the current word after change
        strAfter = s.toString().substring(start, start + count);

        // Check if either is 0
        if (totalBefore.length() == 0) {
            charChange(s.toString(), start, before, count);
            return;
        }
        if (s.toString().length() == 0) {
            charChange(s.toString(), 0, totalBefore.length(), 0);
            return;
        }

        // Compare strBefore and strAfter, calling charChange as necessary
        if (!strBefore.equals(strAfter)) {
            int startVal;

            // Set startVal
            if (strBefore.length() < strAfter.length()) {
                for (startVal = 0; startVal < strBefore.length(); ++startVal) {
                    if (strBefore.charAt(startVal) != strAfter.charAt(startVal)) {
                        break;
                    }
                }
            }
            else {
                for (startVal = 0; startVal < strAfter.length(); ++startVal) {
                    if (strAfter.charAt(startVal) != strBefore.charAt(startVal)) {
                        break;
                    }
                }
            }

            startVal += start;

            // Trim the front of the strings
            try {
                while (strBefore.charAt(0) == strAfter.charAt(0)) {
                    strBefore = strBefore.substring(1);
                    strAfter = strAfter.substring(1);

                    // Check if either is 0
                    if (strBefore.length() == 0) {
                        charChange(s.toString(), startVal, 0, strAfter.length());
                        return;
                    }
                    if (strAfter.length() == 0) {
                        charChange(s.toString(), startVal, strBefore.length(), 0);
                        return;
                    }
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                // Check if either is 0
                if (strBefore.length() == 0) {
                    charChange(s.toString(), startVal, 0, strAfter.length());
                    return;
                }
                if (strAfter.length() == 0) {
                    charChange(s.toString(), startVal, strBefore.length(), 0);
                    return;
                }
            }

            // Trim the end of the strings
            try {
                while (strBefore.charAt(strBefore.length() - 1) == strAfter.charAt(strAfter.length() - 1)) {
                    strBefore = strBefore.substring(0, strBefore.length() - 1);
                    strAfter = strAfter.substring(0, strAfter.length() - 1);


                    // Check if either is 0
                    if (strBefore.length() == 0) {
                        charChange(s.toString(), startVal, 0, strAfter.length());
                        return;
                    }
                    if (strAfter.length() == 0) {
                        charChange(s.toString(), startVal, strBefore.length(), 0);
                        return;
                    }
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                // Check if either is 0
                if (strBefore.length() == 0) {
                    charChange(s.toString(), startVal, 0, strAfter.length());
                    return;
                }
                if (strAfter.length() == 0) {
                    charChange(s.toString(), startVal, strBefore.length(), 0);
                    return;
                }
            }

            charChange(s.toString(), startVal, strBefore.length(), strAfter.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Nothing to do here
    }

    public void charChange(String s, int start, int before, int count) {
        Log.d(Mutation.TAG, s + ", " + start + ", " + before + ", " + count);
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.cancel();
        }
        if (sent) {
            mutation = null;
            sent = false;
        }
        if (disregard != 0) {
            // Change is due to applying mutation
            disregard--;
        }
        else {
            // Change is due to the user typing
            // Make / update mutation
            if (mutation == null) {
                // Create new mutation
                if (before > 0) {
                    // Stuff was deleted
                    mutation = new Delete(start, before, version, myID, sessionID);
                    cursorPosition = start;
                    if (count > 0) {
                        // Text was deleted, then inserted
                        mutations.add(mutation.copy());
                        version.put(myID, version.get(myID) + 1);
                        attemptSendMutation(mutation.getJSON());
                        mutation = new Insert(
                                start,
                                s.substring(start, start + count),
                                version,
                                myID,
                                sessionID
                        );
                        cursorPosition += count;
                    }
                    // Otherwise nothing was added
                }
                else {
                    // before = 0, so something was inserted
                    mutation = new Insert(
                            start,
                            s.substring(start, start + count),
                            version,
                            myID,
                            sessionID
                    );
                    cursorPosition = start + count;
                }
            }
            else {
                // Mutation already exists. Either updated it, or send it and create a new one
                switch (mutation.type) {
                    case Mutation.MUTATION_INSERT:
                        if (before > 0) {
                            // Delete. Send mutation and make a new Delete
                            mutations.add(mutation.copy());
                            version.put(myID, version.get(myID) + 1);
                            attemptSendMutation(mutation.getJSON());

                            mutation = new Delete(start, before, version, myID, sessionID);
                            cursorPosition = start;
                            if (count > 0) {
                                // Text was deleted, then inserted
                                mutations.add(mutation.copy());
                                version.put(myID, version.get(myID) + 1);
                                attemptSendMutation(mutation.getJSON());
                                mutation = new Insert(
                                        start,
                                        s.substring(start, start + count),
                                        version,
                                        myID,
                                        sessionID
                                );
                                cursorPosition += count;
                            }
                            // Otherwise nothing was added
                        }
                        else {
                            // Insert
                            if (start == cursorPosition) {
                                // Continuing current insert
                                ((Insert) mutation).toInsert += s.substring(start, start + count);
                                cursorPosition += count;
                            }
                            else {
                                // New insert. Send mutation and make a new Insert
                                mutations.add(mutation.copy());
                                version.put(myID, version.get(myID) + 1);
                                attemptSendMutation(mutation.getJSON());

                                mutation = new Insert(
                                        start,
                                        s.substring(start, start + count),
                                        version,
                                        myID,
                                        sessionID);
                                cursorPosition = start + count;
                            }
                        }
                        break;
                    case Mutation.MUTATION_DELETE:
                        if (before > 0) {
                            // Delete. Either update this one or create a new one
                            if (start == cursorPosition - before) {
                                // Continuing current delete
                                ((Delete) mutation).numChars += before;
                                cursorPosition = start;

                                if (count > 0) {
                                    // Text was deleted, then inserted
                                    mutations.add(mutation.copy());
                                    version.put(myID, version.get(myID) + 1);
                                    attemptSendMutation(mutation.getJSON());
                                    mutation = new Insert(
                                            start,
                                            s.substring(start, start + count),
                                            version,
                                            myID,
                                            sessionID);
                                    cursorPosition += count;
                                }
                                // Otherwise nothing was added
                            }
                            else {
                                // New Delete. Send the current one and make a new one
                                mutations.add(mutation.copy());
                                version.put(myID, version.get(myID) + 1);
                                attemptSendMutation(mutation.getJSON());

                                mutation = new Delete(start, before, version, myID, sessionID);
                                cursorPosition = start;
                                if (count > 0) {
                                    // Text was deleted, then inserted
                                    mutations.add(mutation.copy());
                                    version.put(myID, version.get(myID) + 1);
                                    attemptSendMutation(mutation.getJSON());
                                    mutation = new Insert(
                                            start,
                                            s.substring(start, start + count),
                                            version,
                                            myID,
                                            sessionID
                                    );
                                    cursorPosition += count;
                                }
                                // Otherwise nothing was added
                            }
                        }
                        else {
                            // Insert. Send mutation then create new Insert
                            mutations.add(mutation.copy());
                            version.put(myID, version.get(myID) + 1);
                            attemptSendMutation(mutation.getJSON());

                            mutation = new Insert(start, s.substring(start, start + count), version, myID, sessionID);
                            cursorPosition = start + count;
                        }
                        break;
                    default:
                        break;
                }
            }

            // Create and run a timer thread
            timerThread = new TimerThread(mutation.copy());
            timerThread.start();
        }
    }

    public void pollForText() {
        disregard++;
        DocumentAsync async = new DocumentAsync("/sessions/document/" + sessionID);
        async.execute();
    }

    private void attemptSendMutation(JSONObject toSend) {
        mSocket.emit("new mutation", toSend);
        sent = true;
    }

    private void attemptSendText(String toSend) {
        JSONObject json = new JSONObject();
        try {
            json.put("text", toSend);
            json.put("sessionID", sessionID);
            mSocket.emit("new text", json);//was toSend
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        mSocket.disconnect();
        mSocket.off("new mutation", onNewMutation);
    }

    class DocumentAsync extends AsyncTask<String, Void, String> {

        ServerConnection server;

        public DocumentAsync(String params) {
            server = new ServerConnection(params);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject json = server.run();
                Log.e("Jsom",json.toString());
                return json.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textField.setText(s);
        }
    }

    public class TimerThread extends Thread {
        private long startTime;
        private Mutation mutation;

        public TimerThread(Mutation mutation) {
            startTime = System.currentTimeMillis();
            this.mutation = mutation;
        }

        public void run() {
            // If no change is made within 3 seconds, send the mutation
            while (System.currentTimeMillis() - startTime < 3000) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            if (!Thread.currentThread().isInterrupted()) {
                SenderThread senderThread = new SenderThread(mutation);
                senderThread.start();
            }
        }

        public void cancel() {
            interrupt();
        }
    }

    public class SenderThread extends Thread {
        private Mutation mutation;

        public SenderThread(Mutation mutation) {
            this.mutation = mutation;
        }

        public void run() {
            attemptSendMutation(mutation.getJSON());
        }
    }
}