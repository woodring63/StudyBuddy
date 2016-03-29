package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

/**
 * Created by Joseph Elliott on 3/26/2016.
 */
public class Task implements Comparable<Task> {
    private String task;
    private boolean done;

    public Task(String task, boolean done) {
        this.task = task;
        this.done = done;
    }

    public String getTask() {
        return task;
    }

    public boolean getDone() {
        return done;
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
            return task.compareTo(t.getTask());
        }
    }
}
