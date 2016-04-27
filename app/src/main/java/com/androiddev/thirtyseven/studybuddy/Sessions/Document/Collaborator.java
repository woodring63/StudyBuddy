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
import java.util.Iterator;
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
    private int cursorPosition;
    private Mutation mutation;
    private int disregard;
    private TimerThread timerThread;
    protected volatile boolean sent;
    private String strBefore;
    private String strAfter;
    private HashMap<String, Integer> version;
    private diff_match_patch dmp;



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
        mutation = null;
        disregard = 0;
        textField.addTextChangedListener(this);
        mSocket.on("new mutation", onNewMutation);
        mSocket.connect();
        pollForText();
        version = new HashMap<>();
        version.put(myID, 0);
        dmp = new diff_match_patch();
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
            if (this.mutation != null && !sent) {
                sendMut();
            }
            try {
                // If either version misses the other user's ID, add that
                if (!mutation.version.containsKey(myID)) {
                    mutation.version.put(myID, 0);
                }
                if (!version.containsKey(mutation.senderID)) {
                    version.put(mutation.senderID, 0);
                }
                // Add the sender to version
                version.put(mutation.senderID, version.get(mutation.senderID) + 1);
                String text;
                for (int i = mutation.version.get(myID); i < mutations.size(); ++i) {
                    mutation.transform(mutations.get(i), myID);
                }
                StringBuilder sBuilder = new StringBuilder(textField.getText().toString());
                switch (mutation.type) {
                    case Mutation.MUTATION_INSERT:
                        Log.d(Mutation.TAG, "Inserting \"" + ((Insert) mutation).toInsert + "\" at index " + mutation.index);
                        sBuilder.insert(mutation.index, ((Insert) mutation).toInsert).toString();
                        text = sBuilder.toString();
                        break;
                    case Mutation.MUTATION_DELETE:
                        Log.d(Mutation.TAG, "Deleting " + ((Delete) mutation).numChars + " characters at index " + mutation.index);
                        sBuilder.delete(mutation.index, mutation.index + ((Delete) mutation).numChars).toString();
                        text = sBuilder.toString();
                        break;
                    default:
                        text = "";
                        break;
                }
                disregard++;
                textField.setText(text);
            }
            catch (StringIndexOutOfBoundsException e) {
                refresh();
                e.printStackTrace();
            }
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Store the string before the change
        strBefore = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (disregard > 0) {
            // Disregard change
            Log.d(Mutation.TAG, "Disregarding change to \"" + s.toString() + "\"");
            disregard--;
            return;
        }
        // Store the string after the change
        strAfter = s.toString();
        if (strBefore == null || strAfter == null || strBefore.equals(strAfter)) {
            return;
        }

        if (strBefore.length() == 0) {
            charChange(s.toString(), 0, 0, strAfter.length());
            return;
        }
        else if (strAfter.length() == 0) {
            charChange(s.toString(), 0, strBefore.length(), 0);
            return;
        }

        // Use Google's diff-match-patch to analyze the strBefore and strAfter
        LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(strBefore, strAfter);
        Iterator<diff_match_patch.Diff> iter = diffs.listIterator();
        diff_match_patch.Diff diff;
        int startVal = 0;
        while(iter.hasNext()) {
            diff = iter.next();
            switch (diff.operation) {
                case DELETE:
                    charChange(s.toString(), startVal, diff.text.length(), 0);
                    break;
                case INSERT:
                    charChange(s.toString(), startVal, 0, diff.text.length());
                case EQUAL:
                    startVal += diff.text.length();
                    break;
                default:
                    break;
            }
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

        // Change is due to the user typing
        // Make / update mutation
        if (mutation == null || sent) {
            // Create new mutation
            if (before > 0) {
                // Stuff was deleted
                mutation = new Delete(start, before, version, myID, sessionID);
                cursorPosition = start;
                if (count > 0) {
                    // Text was deleted, then inserted
                    sendMut();
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
                        sendMut();

                        mutation = new Delete(start, before, version, myID, sessionID);
                        cursorPosition = start;
                        if (count > 0) {
                            // Text was deleted, then inserted
                            sendMut();
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
                            sendMut();

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
                            mutation.index = start;
                            cursorPosition = start;

                            if (count > 0) {
                                // Text was deleted, then inserted
                                sendMut();
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
                            sendMut();

                            mutation = new Delete(start, before, version, myID, sessionID);
                            cursorPosition = start;
                            if (count > 0) {
                                // Text was deleted, then inserted
                                sendMut();
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
                        sendMut();

                        mutation = new Insert(start, s.substring(start, start + count), version, myID, sessionID);
                        cursorPosition = start + count;
                    }
                    break;
                default:
                    break;
            }

            sent = false;
            // Create and run a timer thread
            timerThread = new TimerThread(mutation.copy());
            timerThread.start();
        }
    }

    public void pollForText() {
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

    public void onPause() {
        if (mutation != null && !sent) {
            sendMut();
        }
    }

    public void onResume() {
        pollForText();
    }

    public void refresh() {
        if (mutation != null && !sent) {
            sendMut();
        }
        pollForText();
    }

    public void load(String newText) {
        if (mutation != null && !sent) {
            sendMut();
        }
    }

    public void onDestroy() {
        mSocket.disconnect();
        mSocket.off("new mutation", onNewMutation);
    }

    private void sendMut() {
        mutations.add(mutation.copy());
        version.put(myID, version.get(myID) + 1);
        attemptSendMutation(mutation.getJSON());
        mutation = null;
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
            disregard++;
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
            while (System.currentTimeMillis() - startTime < 1500) {
                if (Thread.currentThread().isInterrupted() || sent) {
                    break;
                }
            }
            if (!Thread.currentThread().isInterrupted() && !sent) {
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
            Log.d(Mutation.TAG, "sending");
        }
    }
}