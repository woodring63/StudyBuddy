package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Nathan on 2/28/2016.
 */
public class Buddy implements Parcelable {

    private String id;
    private String name;
    private String username;
    private String major;
    private String[] courses;
    private String[] sessions;
    private String[] buddies;

    public Buddy() {
        id = null;
        name = null;
        username = null;
        major = null;
        courses = null;
        sessions = null;
        buddies = null;
    }

    public String[] getBuddies() {
        return buddies;
    }

    public void setBuddies(String[] buddies) {
        this.buddies = buddies;
    }

    public String[] getCourses() {
        return courses;
    }

    public void setCourses(String[] courses) {
        this.courses = courses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSessions() {
        return sessions;
    }

    public void setSessions(String[] sessions) {
        this.sessions = sessions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(major);
        dest.writeStringArray(courses);
        dest.writeStringArray(sessions);
        dest.writeStringArray(buddies);
    }

    public static final Parcelable.Creator<Buddy> CREATOR = new Parcelable.Creator<Buddy>() {

        @Override
        public Buddy createFromParcel(Parcel source) {
            return new Buddy(source);
        }

        @Override
        public Buddy[] newArray(int size) {
            return new Buddy[size];
        }
    };

    private Buddy(Parcel source) {
        this.setId(source.readString());
        this.setName(source.readString());
        this.setUsername(source.readString());
        this.setMajor(source.readString());
        this.setCourses(source.createStringArray());
        this.setSessions(source.createStringArray());
        this.setBuddies(source.createStringArray());
    }
}

