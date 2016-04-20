package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Nathan on 4/19/2016.
 */
public class Collaborator implements TextWatcher {

    private EditText textField;
    private String myID;
    private LinkedList<Mutation> mutations;
    private int disregard = 0;

    /**
     * An object used to manage mutations
     * @param textField - the EditText displaying the session Document
     * @param myID - the User's ID
     */
    public Collaborator(EditText textField, String myID) {
        this.textField = textField;
        this.myID = myID;
        mutations = new LinkedList<>();
        textField.addTextChangedListener(this);
    }

    /**
     * Processes the given mutation, transforming it as necessary, then
     * applying it to text
     * @param mutation - the Mutation to process
     */
    public void process(Mutation mutation){
        if (mutation.senderID.equals(myID)) {
            mutations.poll();
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

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
