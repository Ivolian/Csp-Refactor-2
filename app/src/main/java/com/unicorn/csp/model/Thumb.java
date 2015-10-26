package com.unicorn.csp.model;

import java.util.Date;


public class Thumb {

    private String displayName;

    private Date eventTime;

    public Thumb(String displayName, Date eventTime) {
        this.displayName =displayName;
        this.eventTime = eventTime;
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
