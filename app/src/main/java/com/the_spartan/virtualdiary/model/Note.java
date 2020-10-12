package com.the_spartan.virtualdiary.model;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by the_spartan on 3/7/18.
 */

public class Note implements Serializable {

    private long mDateTime;
    private String mTitle;
    private String mContent;
    private Calendar mCalendar;
    private int mDay, mMonth, mYear;
    private int ID;

    public Note(long dateTime, String title, String content, Calendar calendar){
        mDateTime = dateTime;
        mTitle = title;
        mContent = content;
        mCalendar = calendar;

        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mYear = mCalendar.get(Calendar.YEAR);
    }

    public Note(int ID,long dateTime, String title, String content){
        this.ID = ID;
        mDateTime = dateTime;
        mTitle = title;
        mContent = content;
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(dateTime);

        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mYear = mCalendar.get(Calendar.YEAR);
    }

    public long getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(long mDateTime) {
        this.mDateTime = mDateTime;
    }

    public String getMtitle() {
        return mTitle;
    }

    public void setMtitle(String mtitle) {
        this.mTitle = mtitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public Calendar getmCalendar() {
        return mCalendar;
    }

    public void setmCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDateTimeFormatted(Context context){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mma"
                , context.getResources().getConfiguration().locale);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return  simpleDateFormat.format(new Date(mCalendar.getTimeInMillis()));
    }
}
