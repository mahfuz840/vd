package com.the_spartan.virtualdiary.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class ToDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Exclude
    public static final String TODO = "todo";

    private String key;
    private Priority priority;
    private boolean done;

    private String subject;
    private String dueDate;
    private String time;

    public ToDo() {
        this.priority = Priority.LOW;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isNew() {
        return getKey() == null;
    }
}
