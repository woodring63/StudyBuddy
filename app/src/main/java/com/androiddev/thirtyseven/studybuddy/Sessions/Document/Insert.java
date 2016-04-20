package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nathan on 4/18/2016.
 */
public class Insert extends Mutation {

    protected String toInsert; // text to insert

    /**
     * Constructs a new Insert
     * @param index - the index that the mutation begins at
     * @param toInsert - the text to insert
     * @param senderID - The ID of the user who created this Mutation
     */
    public Insert(int index, String toInsert, String senderID) {
        super(MUTATION_INSERT, index, senderID);
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
    public void transform(Mutation mutation) {
        switch (mutation.type) {
            case MUTATION_INSERT:
                if (mutation.index < index) { // this needs to be modified
                    index += mutation.length();
                }
                break;
            case MUTATION_DELETE:
                Delete del = (Delete) mutation;
                if (del.subDelete != null) {
                    transform(del.subDelete);
                }

                if (mutation.index < index) { // this needs to me modified
                    if (index - mutation.index < del.length()) { // mutation is split
                        index -= mutation.index - index;
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
            json.put("senderIndex", senderID);
            json.put("toInsert", toInsert);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
