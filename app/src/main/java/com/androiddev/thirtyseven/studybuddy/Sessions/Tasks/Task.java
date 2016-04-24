package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

import android.test.PerformanceTestCase;

import java.util.Date;

/**
 * Created by Joseph Elliott on 3/26/2016.
 */
public class Task implements Comparable<Task> {

    private String task;
    private boolean done;
    private Date createdDate;
    private Date terminationDate;
    private Date checkedDate;
    private String id;

    public Task(String task, boolean done, Date createdDate, String id) {
        this.task = task;
        this.id = id;
        this.done = done;
        this.createdDate = new Date(createdDate.getTime());
        this.terminationDate = null;
        this.checkedDate = null;
    }

    public String getId(){return id;}

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public String getTask() {
        return task;
    }

    public boolean getDone() {
        return done;
    }

    public void setCreatedDate(Date date) {
        this.createdDate = new Date(date.getTime());
    }

    public void setTerminationDate(Date date) {
        try {
            this.terminationDate = new Date(date.getTime());
        } catch (NullPointerException e) {
            this.terminationDate = null;
        }
    }

    public void setCheckedDate(Date date) {
        try {
            this.checkedDate = new Date(date.getTime());
        } catch (NullPointerException e) {
            this.checkedDate = null;
        }
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void toggleDone() {
        done = !done;
    }

    @Override
    public int compareTo(Task t) {
        if (done && !t.getDone()) {
            return 1;
        } else if (!done && t.getDone()) {
            return -1;
        } else {
            return Long.compare(createdDate.getTime(), t.getCreatedDate().getTime());
        }
    }
}
