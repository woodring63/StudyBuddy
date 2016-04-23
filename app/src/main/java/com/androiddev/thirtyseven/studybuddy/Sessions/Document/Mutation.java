package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Nathan on 4/18/2016.
 */
public abstract class Mutation {

    public static final String TAG = "Mutation";
    public static final int MUTATION_INSERT = 1;
    public static final int MUTATION_DELETE = 2;

    protected int type; // MUTATION_INSERT or MUTATION_DELETE
    protected int index; // the index that the mutation begins at
    protected String senderID; // The ID of the user who created this Mutation
    protected String sessionID; // The ID of the session that this Mutation is from


    /**
     * Constructs a new Mutation
     * @param type - MUTATION_INSERT or MUTATION_DELETE
     * @param index - the index that the mutation begins at
     * @param senderID - The ID of the user who created this Mutation
     * @param sessionID - The ID of the session that this Mutation is from
     */
    public Mutation(int type, int index, String senderID, String sessionID) {
        this.type = type;
        this.index = index;
        this.senderID = senderID;
        this.sessionID = sessionID;
    }

    /**
     * Parses the given JSONObject to construct the Mutation
     * @param json - the JSONObject representing the Mutation
     */
    public Mutation(JSONObject json) {
        JSONObject jMap;
        Iterator<?> ids;
        try {
            type = json.getInt("type");
            index = json.getInt("index");
            senderID = json.getString("senderID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a shallow copy of this Mutation
     * @return - a shallow copy of this Mutation
     */
    public Mutation copy() {
        switch(type) {
            case MUTATION_INSERT:
                //change from null
                return new Insert(index, ((Insert) this).toInsert, null,senderID);
            case MUTATION_DELETE:
                //change fromn ull
                return new Delete(index, ((Delete) this).numChars, null, senderID);
            default:
                return null;
        }
    }

    /**
     * Changes this Mutation as necessary to account for the
     * given Mutation
     * @param mutation
     */
    public abstract void transform(Mutation mutation);

    /**
     * Returns the number of characters inserted / deleted by this mutation
     * @return - the number of characters inserted / deleted by this mutation
     */
    public abstract int length();

    /**
     * Returns the first index not affected by this mutation
     * @return - the first index not affected by this mutation
     */
    public abstract int end();

    /**
     * Writes the mutation as a JSON object.
     * @return - the JSON object representing this Mutation
     */
    public abstract JSONObject getJSON();
}
