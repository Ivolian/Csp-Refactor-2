package com.unicorn.csp.model;

import java.io.Serializable;
import java.util.Date;


public class Answer implements Serializable {

    private String content;

    private String displayName;

    private Date eventTime;

     //

    public Answer( String content, String displayName, Date eventTime) {
        this.content = content;
        this.displayName = displayName;
        this.eventTime = eventTime;
    }

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

}
