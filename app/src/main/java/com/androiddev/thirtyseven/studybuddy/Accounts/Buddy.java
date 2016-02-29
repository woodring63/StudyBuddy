package com.androiddev.thirtyseven.studybuddy.Accounts;

import java.util.ArrayList;

/**
 * Created by Nathan on 2/28/2016.
 */
public class Buddy {

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
}

