package com.unicorn.csp.model;

import java.io.Serializable;


public class UserInfo implements Serializable {

    private int thumbCount;

    private int favoriteNewsCount;

    private int commentCount;

    private int readTimes;

    private int loginTimes;

    //

    public int getThumbCount() {
        return thumbCount;
    }

    public void setThumbCount(int thumbCount) {
        this.thumbCount = thumbCount;
    }

    public int getFavoriteNewsCount() {
        return favoriteNewsCount;
    }

    public void setFavoriteNewsCount(int favoriteNewsCount) {
        this.favoriteNewsCount = favoriteNewsCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(int readTimes) {
        this.readTimes = readTimes;
    }

    public int getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(int loginTimes) {
        this.loginTimes = loginTimes;
    }

}
