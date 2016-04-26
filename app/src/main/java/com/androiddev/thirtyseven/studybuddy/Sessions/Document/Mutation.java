package com.androiddev.thirtyseven.studybuddy.Sessions.Document;

import android.util.Log;

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

    /**
     * A note: version represent how many changes each user has made to the text
     * For example:
     *      Alice:   1 change
     *      Bob:     2 changes
     *      Charlie: 0 changes
     * This is used to determine which mutations this one needs to be transformed against
     */
    public static final String TAG = "Mutation";
    public static final int MUTATION_INSERT = 1;
    public static final int MUTATION_DELETE = 2;

    protected int type; // MUTATION_INSERT or MUTATION_DELETE
    protected int index; // the index that the mutation begins at
    protected HashMap<String, Integer> version; // the version when this Mutation was created
    protected String senderID; // the ID of the user who created this Mutation
    protected String sessionID; // the ID of the session that this Mutation is from

    /**
     * Constructs a new Mutation
     * @param type - MUTATION_INSERT or MUTATION_DELETE
     * @param index - the index that the mutation begins at
     * @param version - the version when this Mutation was created
     * @param senderID - the ID of the user who created this Mutation
     * @param sessionID - the ID of the session that this Mutation is from
     */
    public Mutation(int type, int index, HashMap<String, Integer> version, String senderID, String sessionID) {
        this.type = type;
        this.index = index;
        this.version = (HashMap<String, Integer>) version.clone();
        this.senderID = senderID;
        this.sessionID = sessionID;
    }

    /**
     * Parses the given JSONObject to construct the Mutation
     * @param json - the JSONObject representing the Mutation
     */
    public Mutation(JSONObject json) {
        version = new HashMap<>();
        try {
            type = json.getInt("type");
            index = json.getInt("index");
            senderID = json.getString("senderID");
            JSONObject map = json.getJSONObject("version");
            Iterator<String> keys = map.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                int value = map.getInt(key);
                version.put(key, value);
            }
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
                return new Insert(index, ((Insert) this).toInsert, version, senderID, sessionID);
            case MUTATION_DELETE:
                return new Delete(index, ((Delete) this).numChars, version, senderID, sessionID);
            default:
                return null;
        }
    }

    /**
     * Changes this Mutation as necessary to account for the
     * given Mutation
     * @param mutation - the Mutation to account for
     * @param receiverID - the ID of this User
     */
    public abstract void transform(Mutation mutation, String receiverID);

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
