package com.androiddev.thirtyseven.studybuddy.Backend;

import android.os.AsyncTask;

/**
 * Created by enclark on 2/28/2016.
 */
public class ServerAsync extends AsyncTask {

    ServerConnection server;

    public ServerAsync(String method, String params)
    {
        server = new ServerConnection(params);
    }

    @Override
    protected Object doInBackground(Object[] params) {

        return server.run();
    }
}
