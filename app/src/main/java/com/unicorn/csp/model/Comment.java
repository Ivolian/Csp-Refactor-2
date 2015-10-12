package com.unicorn.csp.model;

import java.util.Date;


public class Comment {

    private String displayName;

    private Date eventTime;

    private String content;

    public Comment(String displayName,Date eventTime, String content) {
        this.displayName =displayName;
        this.eventTime = eventTime;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
