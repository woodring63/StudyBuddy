package com.androiddev.thirtyseven.studybuddy.Backend;

/**
 * Created by enclark on 2/27/2016.
 */
import org.json.JSONObject;

/**
 * Created by enclark on 2/20/2016.
 */
public class User {

    private JSONObject json;

    public User()
    {
        json = new JSONObject();
        setName("undefined");
        setUsername("undefined");
        setCourses(null);
        setSessions(null);
        setCreatedSessions(null);

    }

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

    public boolean setCourses(String[] courses)
    {
        try {
            if(courses != null)
            {
                json.put("courses", courses);
            }
            else
            {
                json.put("courses", new String[0]);
            }
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean setSessions(String[] courses)
    {
        try {
            if(courses != null)
            {
                json.put("sessions", courses);
            }
            else
            {
                json.put("sessions", new String[0]);
            }
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean setCreatedSessions(String[] courses)
    {
        try {
            if(courses != null)
            {
                json.put("createdSessions", courses);
            }
            else
            {
                json.put("createdSessions", new String[0]);
            }
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    public JSONObject getJSON()
    {
        return json;
    }

}
