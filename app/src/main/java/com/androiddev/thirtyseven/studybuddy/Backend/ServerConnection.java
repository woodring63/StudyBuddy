package com.androiddev.thirtyseven.studybuddy.Backend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Comments coming soon
 * Created by enclark on 2/27/2016.
 */
public class ServerConnection {

    /**
     * Created by enclark on 2/7/2016.
     */

    private static final String URL = "http://10.65.207.45:3000";
    private static String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
    private boolean get;
    private boolean post;
    private boolean put;
    private JSONObject body;
    private String params;



    public ServerConnection(String params)//For the GET method
    {
        this.params = params;
        get = true;
    }

    public ServerConnection(User user, String params)//For the POST method
    {
        post = true;
        this.body = user.getJSON();
        this.params = params;

    }

    public ServerConnection(SessionSchema session, String params)//For the POST method
    {
        post = true;
        this.body = session.getJSON();
        this.params = params;

    }

    public ServerConnection(JSONObject json,String method, String params)//For the PUT/POST method, for sessions or users
    {
        if(method.equals("PUT"))
        {
            put = true;
        }else if(method.equals("POST"))
        {
            post = true;
        }
        this.body = json;
        this.params = params;

    }



    public JSONObject run(){
        try {
            //Send a get request or a post request and put the returned value somewhere

            if(get)
            {
                return get(params);
            }
            else if(post)
            {

                return post(body, params);
            }
            else if(put)
            {
                //Body can be null
                return put(body, params);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject get(String params)throws IOException, JSONException
    {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(URL + params).openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Host", URL);
        urlConnection.setUseCaches(false);

        int responseStatus = urlConnection.getResponseCode();
        if(responseStatus != 200)
        {
            System.out.println(responseStatus);
            System.out.println(urlConnection.getResponseMessage());
        }

        //Will return a JSON object containing an array of JSON objects
        JSONObject json = new JSONObject(readResponse(urlConnection));
        if(json == null) {
            return null;
        }
        get = false;
        return json;

    }

    public JSONObject post(JSONObject json, String params) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(URL + params).openConnection();
            urlConnection.setDoOutput(true);//Sets POST
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Host", URL);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            writeRequest(urlConnection,json);

            urlConnection.connect();
            int responseStatus = urlConnection.getResponseCode(); // 200 is the only acceptable response
            if(responseStatus != 200)
            {
                System.out.println(responseStatus);
                System.out.println(urlConnection.getResponseMessage());
            }

            JSONObject jsonObj = new JSONObject(readResponse(urlConnection));
            post = false;
            return  jsonObj;


        } catch (Exception e) {
            post = false;
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject put(JSONObject json, String params) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(URL + params).openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Host", URL);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            if(json != null) // If it is null, then this request does not require a body
            {
                writeRequest(urlConnection,json);
            }

            urlConnection.connect();
            int responseStatus = urlConnection.getResponseCode(); // 200 is the only acceptable response
            if(responseStatus != 200)
            {
                System.out.println(responseStatus);
                System.out.println(urlConnection.getResponseMessage());
            }

            JSONObject jsonObj = new JSONObject(readResponse(urlConnection));
            put = false;
            return  jsonObj;

        } catch (Exception e) {
            put = false;
            e.printStackTrace();
            return null;
        }
    }

    public String readResponse(HttpURLConnection urlConnection)
    {
        //This will read the response from the server and the string will later be converted into a JSON object
        StringBuilder sBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(), charset))) {
            for (String line; (line = reader.readLine()) != null; ) {
                sBuilder.append(line);
                sBuilder.append('\n');
            }
            return sBuilder.toString();
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

    }

    public void writeRequest(HttpURLConnection urlConnection, JSONObject json)
    {
        //Allows for writing to the body of the request
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            out.write(json.toString());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
