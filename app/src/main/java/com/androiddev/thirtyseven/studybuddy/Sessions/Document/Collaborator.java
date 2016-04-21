package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

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
    public Collaborator(EditText textField, String myID, Emitter.Listener onNewMutation) {
        this.textField = textField;
        this.myID = myID;
        this.onNewMutation = onNewMutation;
        mutations = new LinkedList<>();
        disregard = 0;
        mutation = null;
        textField.addTextChangedListener(this);
        mSocket.on("new mutation", onNewMutation);
        mSocket.connect();
    }

    /**
     * Processes the given mutation, transforming it as necessary, then
     * applying it to text
     * @param mutation - the Mutation to process
     */
    public void process(Mutation mutation){
        if (mutation.senderID.equals(myID)) {
            mutations.poll();
            // @TODO Send string to server
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
                    mutation = new Delete(start, before, myID);
                    if (count > 0) {
                        // Text was deleted, then inserted
                    }
                }
            }
            lastEdit = System.currentTimeMillis();
            try {
                Thread.sleep(3000);
                if (lastEdit - System.currentTimeMillis() >= 3000) {
                    mutations.add(mutation.copy());
                    attemptSend(mutation.getJSON());
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

    private void attemptSend(JSONObject toSend) {
        mSocket.emit("new mutation", toSend);
    }

    public void onDestroy() {
        mSocket.disconnect();
        mSocket.off("new mutation", onNewMutation);
    }
}
