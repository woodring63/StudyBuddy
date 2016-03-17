package com.androiddev.thirtyseven.studybuddy.Backend;

/**
 * Created by enclark on 2/27/2016.
 */
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *  An up-to-date user object to format new users to send as a json object to the database.
 *  Not all the information needs to be set, in fact most of it does not need to be set,
 *  in that case, undefined or an empty array will be sent in it's place.
 */

public class User {

    /**
     * The object which stores the user information and will be sent to the database.
     */

    private JSONObject json;

    /**
     * Constructor that will initially set all the items to an empty array
     * that should not be set initially.
     */

    public User()
    {
        json = new JSONObject();
        setName("undefined");
        setUsername("undefined");
        setCourses(null);
        setMajor(null);
        setBio(null);
        try{
            json.put("buddies", new JSONArray());
            json.put("sessions",new JSONArray());
            json.put("createdSessions",new JSONArray());
        }catch(org.json.JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the name of the user in the db
     * @param name The name of the user that will be going into the db.
     * @return boolean indicating success/failure
     */

    public boolean setName(String name)
    {
        try {
            json.put("name", name);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Sets the username (the email) of the new user
     * @param username
     * @return boolean indicating success/failure
     */

    public boolean setUsername(String username)
    {
        try {
            json.put("username", username);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Sets the array of courses as the users current courses.  These will determine
     * which sessions show up on their main page.
     * @param courses array of current courses of the user
     * @return boolean indicating success
     */

    public boolean setCourses(String[] courses)
    {
        try {
            if(courses != null)
            {
                json.put("courses", new JSONArray(courses));
            }
            else
            {
                json.put("courses", new JSONArray());
            }
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Sets the major of the user to show buddies in order to indicate which courses they
     * might take in the future or what they might know.
     * @param major The user's college major
     * @return boolean indicating success/failure
     */

    public boolean setMajor(String major) {
        try {
            json.put("major", major);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     *
     */

    public boolean setBio(String bio){
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
     * @return the json object to represent the user in the database
     */

    public JSONObject getJSON()
    {
        return json;
    }


//************************The following methods are probably unnecessary
// ***********************and will be deleted when it is fully determined

//
//    public boolean setSessions(String[] sessions)
//    {
//        try {
//            if(sessions != null)
//            {
//                json.put("sessions", sessions);
//            }
//            else
//            {
//                json.put("sessions", new String[0]);
//            }
//        }catch(Exception e)
//        {
//            return false;
//        }
//        return true;
//    }
//
//    public boolean setCreatedSessions(String[] sessions)
//    {
//        try {
//            if(sessions != null)
//            {
//                json.put("createdSessions", sessions);
//            }
//            else
//            {
//                json.put("createdSessions", new String[0]);
//            }
//        }catch(Exception e)
//        {
//            return false;
//        }
//        return true;
//    }



}
