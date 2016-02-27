package com.androiddev.thirtyseven.studybuddy.Backend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by enclark on 2/27/2016.
 */
public class ServerConnection implements Runnable {

    /**
     * Created by enclark on 2/7/2016.
     */

    private static final String URL = "https://10.26.228.50:3000/users/";
    private static String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
    private boolean get;
    private boolean post;
    private JSONObject user;
    private String params;



    // ...
    public ServerConnection(String params)//For the GET method
    {
        get = true;
        this.params = params;

    }

    public ServerConnection(User user, String params)//For the POST method
    {
        post = true;
        this.user = user.getJSON();
        this.params = params;

    }


    public void run(){
        try {
            //Send a get request or a post request and put the returned value somewhere

            if(get)
            {
                get(params);
            }
            else if(post)
            {
                post(user, params);
                //send data somewhere
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        //Dies
    }


    public JSONArray get(String params)throws IOException, JSONException
    {
        HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(URL + params).openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Host", URL);
        urlConnection.setUseCaches(false);

        int responseStatus = urlConnection.getResponseCode();
        if(responseStatus != 200)
        {
            System.out.println(responseStatus);
            System.out.println(urlConnection.getResponseMessage());
        }

        //Will return a JSON object containing an array of JSON objects
        JSONArray json = new JSONArray(readResponse(urlConnection));
        if(json == null) {
            return null;
        }

        return json;

    }

    public JSONObject post(JSONObject json, String params) {
        try {
            HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(URL + params).openConnection();
            urlConnection.setDoOutput(true);//Sets POST
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Host", URL);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            //Allows for writing to the body of the request
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            out.write(json.toString());
            out.flush();
            out.close();
            urlConnection.connect();
            int responseStatus = urlConnection.getResponseCode(); // 200 is the only acceptable response
            if(responseStatus != 200)
            {
                System.out.println(responseStatus);
                System.out.println(urlConnection.getResponseMessage());
            }

            JSONObject jsonObj = new JSONObject(readResponse(urlConnection));

            return  jsonObj;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String readResponse(HttpsURLConnection urlConnection)
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






}
