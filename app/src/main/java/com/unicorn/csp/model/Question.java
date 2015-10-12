package com.unicorn.csp.model;

import java.io.Serializable;
import java.util.Date;


public class Question implements Serializable {

    private String id;

    private String content;

    private String displayName;

    private Date eventTime;


    public Question() {
    }

    public Question(String id, String content, String displayName, Date eventTime) {
        this.id = id;
        this.content = content;
        this.displayName = displayName;
        this.eventTime = eventTime;
    }


    //

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
