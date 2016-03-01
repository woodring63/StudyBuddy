package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.R;

import java.util.ArrayList;

/**
 * Created by Nathan on 2/8/2016.
 */
class BuddyListAdapter extends ArrayAdapter<Buddy> {

    private final Activity context;
    private final ArrayList<Buddy> buddies;

    BuddyListAdapter(Activity context, ArrayList<Buddy> buddies){
        super(context, R.layout.buddy_list_layout, buddies);

        this.context = context;
        this.buddies = buddies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View searchResult = inflater.inflate(R.layout.buddy_list_layout, null, true);

        TextView resultName = (TextView) searchResult.findViewById(R.id.resultName);
        ImageView resultImg = (ImageView) searchResult.findViewById(R.id.resultImg);

        resultName.setText(buddies.get(position).getName());
        return searchResult;
    }
}
