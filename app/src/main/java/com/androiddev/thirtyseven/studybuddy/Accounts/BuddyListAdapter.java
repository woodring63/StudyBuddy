package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.R;

/**
 * Created by Nathan on 2/8/2016.
 */
class BuddyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] resultNames;
    private final int[] resultImgIds;

    BuddyListAdapter(Activity context, String[] resultNames, int[] resultImgIds){
        super(context, R.layout.buddy_list_layout, resultNames);

        this.context = context;
        this.resultNames = resultNames;
        this.resultImgIds = resultImgIds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View searchResult = inflater.inflate(R.layout.buddy_list_layout, null, true);

        TextView resultName = (TextView) searchResult.findViewById(R.id.resultName);
        ImageView resultImg = (ImageView) searchResult.findViewById(R.id.resultImg);

        resultName.setText(resultNames[position]);
        resultImg.setImageResource(resultImgIds[position]);
        return searchResult;
    }
}
