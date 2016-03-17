package com.androiddev.thirtyseven.studybuddy.Backend;

import android.content.pm.PackageInstaller;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This object will behave like User and will send format a json object for a new session
 * to be sent to the database.
 * Created by enclark on 3/12/2016.
 */
public class SessionSchema {

    /**
     * The object which stores the session information and will be sent to the database.
     * Should have the format of
     * {
         title: String,
         startTime: Number,
         endTime: Number,
         course: String,
         bio: String,
         attendees: Array,
         messages: Array,
         leader: String
      }
     */

    private JSONObject json;

    /**
     * Constructs a new session object and initially defines all fields to be undefined
     *  except the leader which is the current user. Some of the info should not be set and
     *  here will be initialized as empty arrays.
     * @param userId of the current user (will by default be the leader)
     */

    public SessionSchema(String userId)
    {
        json = new JSONObject();
        setTitle("undefined");
        setTimes(0, 0);
        setCourse("undefined");
        setBio("undefined");
        try{
            json.put("attendees",new JSONArray());
            json.put("messages",new JSONArray());
            json.put("leader",userId);
        }catch(org.json.JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the title of the session object
     * @param title title of the study session
     * @return boolean indicating success/failure
     */

    public boolean setTitle(String title)
    {
        try {
            json.put("title", title);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Sets the title of the starting and ending times of the session.  Will
     * be used to find sessions within the next 24 hours and for filtering purposes.
     * If needed, this can be altered to take Date objects
     * @param startTime time that the study session plans to start in milliseconds Jan 1, 1970
     * @param endTime time that the study session plans to end in milliseconds Jan 1, 1970
     * @return boolean indicating success/failure
     */

    public boolean setTimes(int startTime, int endTime)
    {
        try {
            json.put("startTime", startTime);
            json.put("endTime", endTime);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Sets the course that the study session plans to study for.
     * @param course course
     * @return boolean indicating success/failure
     */

    public boolean setCourse(String course)
    {
        try {
            json.put("course", course);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Sets the description/bio/purpose of the session.
     * @param bio a small statement describing the purpose of the sudy session
     * @return boolean indicating success/failure
     */

    public boolean setBio(String bio)
    {
        try {
            json.put("bio", bio);
        }catch(Exception e)
        {
            return false;
        }
        return true;

    }

    /**
     * This method should be used for testing purposes and by the ServerConnection class.
     * @return the json object to represent the session in the database
     */

    public JSONObject getJSON()
    {
        return json;
    }
}
