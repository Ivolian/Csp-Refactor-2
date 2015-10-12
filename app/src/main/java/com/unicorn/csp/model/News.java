package com.unicorn.csp.model;

import java.io.Serializable;
import java.util.Date;


public class News implements Serializable {

    private String id;

    private String title;

    private Date time;

    private int commentCount;

    private int thumbCount;

    private String picture;

    public News(String id, String title, Date time, int commentCount, int thumbCount, String picture) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.commentCount = commentCount;
        this.thumbCount = thumbCount;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getThumbCount() {
        return thumbCount;
    }

    public void setThumbCount(int thumbCount) {
        this.thumbCount = thumbCount;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
