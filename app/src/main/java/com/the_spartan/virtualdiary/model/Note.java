package com.the_spartan.virtualdiary.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by the_spartan on 3/7/18.
 */

public class Note implements Serializable {

    @Exclude
    private static final long serialVersionUID = 1L;

    @Exclude
    public static final String NOTE = "Note";

    private String key;

    private String title;

    private String description;

    private long timestamp;

    public Note() {

    }

    public Note(NoteBuilder noteBuilder) {
        this.title = noteBuilder.title;
        this.description = noteBuilder.description;
        this.timestamp = noteBuilder.timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNew() {
        return getKey() == null;
    }

    public static class NoteBuilder {

        private String title;
        private String description;
        private long timestamp;

        public NoteBuilder setTitle(String title) {
            this.title = title;

            return this;
        }

        public NoteBuilder setDescription(String description) {
            this.description = description;

            return this;
        }

        public NoteBuilder setTimeStamp(long timestamp) {
            this.timestamp = timestamp;

            return this;
        }

        public Note build() {
            Note note = new Note(this);

            return note;
        }
    }
}
