package com.the_spartan.virtualdiary.model;

import android.content.Context;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by the_spartan on 3/7/18.
 */

public class Note implements Serializable {

    private int ID;

    private long dateTime;

    private String title;

    private String description;

    @Exclude
    private Calendar calendar;

    private long timestamp;

    private int day, month, year;

    public Note() {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public Note(long dateTime, String title, String description, Calendar calendar) {
        this.dateTime = dateTime;
        this.title = title;
        this.description = description;
        this.calendar = calendar;
        this.timestamp = calendar.getTimeInMillis();

        month = this.calendar.get(Calendar.MONTH) + 1;
        year = this.calendar.get(Calendar.YEAR);
    }

    public Note(int ID, long dateTime, String title, String description) {
        this.ID = ID;
        this.dateTime = dateTime;
        this.title = title;
        this.description = description;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);

        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mtitle) {
        this.title = mtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDateTimeFormatted(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mma"
                , context.getResources().getConfiguration().locale);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
    }

    @Override
    public String toString() {
        return "Note{" +
                "dateTime=" + dateTime +
                ", title='" + title + '\'' +
                ", content='" + description + '\'' +
                ", calendar=" + calendar +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                ", ID=" + ID +
                '}';
    }
}
