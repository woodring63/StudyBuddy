package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Nathan on 4/19/2016.
 */
public class Collaborator implements TextWatcher {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(ServerConnection.IP + ":8300");
        } catch (URISyntaxException e) {}
    }


    private EditText textField;
    private String myID;
    private String sessionID;
    private Emitter.Listener onNewMutation;
    private LinkedList<Mutation> mutations;
    private int disregard;
    private int cursorPosition;
    private Mutation mutation;
    private long lastEdit;


    /**
     * An object used to manage mutations
     * @param textField - the EditText displaying the session Document
     * @param myID - the User's ID
     */
    public Collaborator(EditText textField, String myID, String sessionID, Emitter.Listener onNewMutation) {
        this.textField = textField;
        this.myID = myID;
        this.onNewMutation = onNewMutation;
        mutations = new LinkedList<>();
        disregard = 0;
        mutation = null;
        textField.addTextChangedListener(this);
        mSocket.on("new mutation", onNewMutation);
        mSocket.connect();
        DocumentAsync async = new DocumentAsync("/sessions/document/" + sessionID);
        async.execute();
    }

    /**
     * Processes the given mutation, transforming it as necessary, then
     * applying it to text
     * @param mutation - the Mutation to process
     */
    public void process(Mutation mutation){
        if (mutation.senderID.equals(myID)) {
            mutations.poll();
            attemptSendText(textField.getText().toString());
        }
        else {
            disregard++;
            String text = textField.getText().toString();
            ListIterator<Mutation> iter = mutations.listIterator();
            while(iter.hasNext()) {
                mutation.transform(iter.next());
            }
            switch (mutation.type) {
                case Mutation.MUTATION_INSERT:
                    text = text.substring(0, mutation.index) +
                            ((Insert) mutation).toInsert + text.substring(mutation.index);
                    break;
                case Mutation.MUTATION_DELETE:
                    if (((Delete) mutation).subDelete != null) {
                        process(((Delete) mutation).subDelete);
                    }
                    text = text.substring(0, mutation.index) + text.substring(mutation.end());
                    break;
                default:
                    break;
            }
            textField.setText(text);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Nothing to do here
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    mutation = new Delete(start, before, myID, sessionID);
                    cursorPosition = start;
                    if (count > 0) {
                        // Text was deleted, then inserted
                        mutations.add(mutation.copy());
                        attemptSendMutation(mutation.getJSON());
                        mutation = new Insert(
                                start,
                                s.toString().substring(start, start + count),
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
                            s.toString().substring(start, start + count),
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
                            attemptSendMutation(mutation.getJSON());

                            mutation = new Delete(start, before, myID, sessionID);
                            cursorPosition = start;
                            if (count > 0) {
                                // Text was deleted, then inserted
                                mutations.add(mutation.copy());
                                attemptSendMutation(mutation.getJSON());
                                mutation = new Insert(
                                        start,
                                        s.toString().substring(start, start + count),
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
                                ((Insert) mutation).toInsert += s.toString().substring(start, start + count);
                                cursorPosition += count;
                            }
                            else {
                                // New insert. Send mutation and make a new Insert
                                mutations.add(mutation.copy());
                                attemptSendMutation(mutation.getJSON());

                                mutation = new Insert(
                                        start,
                                        s.toString().substring(start, start + count),
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
                                    attemptSendMutation(mutation.getJSON());
                                    mutation = new Insert(
                                            start,
                                            s.toString().substring(start, start + count),
                                            myID,
                                            sessionID);
                                    cursorPosition += count;
                                }
                                // Otherwise nothing was added
                            }
                            else {
                                // New Delete. Send the current one and make a new one
                                mutations.add(mutation.copy());
                                attemptSendMutation(mutation.getJSON());

                                mutation = new Delete(start, before, myID, sessionID);
                                cursorPosition = start;
                                if (count > 0) {
                                    // Text was deleted, then inserted
                                    mutations.add(mutation.copy());
                                    attemptSendMutation(mutation.getJSON());
                                    mutation = new Insert(
                                            start,
                                            s.toString().substring(start, start + count),
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
                            attemptSendMutation(mutation.getJSON());

                            mutation = new Insert(start, s.toString().substring(start, start + count), myID, sessionID);
                            cursorPosition = start + count;
                        }
                        break;
                    default:
                        break;
                }
            }

            // If nothing else happens for 3 seconds, send the mutation
            lastEdit = System.currentTimeMillis();
            try {
                Thread.sleep(3000);
                if (lastEdit - System.currentTimeMillis() >= 3000) {
                    mutations.add(mutation.copy());
                    attemptSendMutation(mutation.getJSON());
                    mutation = null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Nothing to do here
    }

    private void attemptSendMutation(JSONObject toSend) {
        mSocket.emit("new mutation", toSend);
    }

    private void attemptSendText(String toSend) {
        JSONObject json = new JSONObject();
        try {
            json.put("text", toSend);
            json.put("sessionID", sessionID);
            mSocket.emit("new text", toSend);
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
                return server.run().getString("text");
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
}