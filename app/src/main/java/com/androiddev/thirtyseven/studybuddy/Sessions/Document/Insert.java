package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nathan on 4/18/2016.
 */
public class Insert extends Mutation {

    /**
     * The transform method changes the index where a string is inserted or deleted and
     * in the case of Delete, it can change how many characters to delete
     */
    protected String toInsert; // text to insert

    /**
     * Constructs a new Insert
     * @param index - the index that the mutation begins at
     * @param toInsert - the text to insert
     * @param version - the version when this Mutation was created
     * @param senderID - the ID of the user who created this Mutation
     * @param sessionID - the ID of the session that this Mutation is from
     */
    public Insert(int index, String toInsert, HashMap<String, Integer> version, String senderID, String sessionID) {
        super(MUTATION_INSERT, index, version, senderID, sessionID);
        this.toInsert = toInsert;
    }

    /**
     * Parses the given JSONObject to construct the Insert
     * @param json - the JSONObject representing the Insert
     */
    public Insert(JSONObject json) {
        super(json);
        try {
            toInsert = json.getString("toInsert");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int length() {
        return toInsert.length();
    }

    @Override
    public int end() {
        return index + toInsert.length();
    }

    @Override
    public void transform(Mutation mutation, String receiverID) {
        switch (mutation.type) {
            case MUTATION_INSERT:
                if (mutation.index < index) { // this needs to be modified
                    index += mutation.length();
                }
                break;
            case MUTATION_DELETE:
                Delete del = (Delete) mutation;
                if (del.subDelete != null) {
                    transform(del.subDelete, receiverID);
                }

                if (del.index < index) { // this needs to me modified
                    if (index - del.index < del.length()) { // mutation is split
                        index = del.index;
                    }
                    else {
                        index -= del.length();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("index", index);
            json.put("senderID", senderID);
            json.put("sessionID", sessionID);
            json.put("toInsert", toInsert);
            json.put("version", new JSONObject(version));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
