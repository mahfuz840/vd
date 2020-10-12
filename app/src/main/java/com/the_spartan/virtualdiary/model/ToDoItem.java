package com.the_spartan.virtualdiary.model;

import java.io.Serializable;

public class ToDoItem implements Serializable {

    private String subject;
    private String dueDate;
    private String time;
    private int priority;
    private int isDone;
    private int ID;

    public ToDoItem(int ID, String subject, String dueDate, String time, int priority, int isDone){
        this.ID = ID;
        this.subject = subject;
        this.dueDate = dueDate;
        this.time = time;
        this.priority = priority;
        this.isDone = isDone;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
