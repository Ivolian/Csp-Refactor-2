package com.unicorn.csp.model;

import java.io.Serializable;
import java.util.Date;


public class Book implements Serializable {

    private Integer orderNo;

    private String name;

    private String picture;

    private String ebook;

    private String ebookFilename;

    private String summary;

    private String id;

    private Date eventTime;

    private Integer numerator;

    private Integer denominator;

    private Integer commentCount;

    private Integer thumbCount;

    public Book() {
    }

    public Book(Integer orderNo, String name, String picture, String ebook, String ebookFilename, String summary, String id,Date eventTime) {
        this.orderNo = orderNo;
        this.name = name;
        this.picture = picture;
        this.ebook = ebook;
        this.ebookFilename = ebookFilename;
        this.summary = summary;
        this.id = id;
        this.eventTime = eventTime;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEbook() {
        return ebook;
    }

    public void setEbook(String ebook) {
        this.ebook = ebook;
    }

    public String getEbookFilename() {
        return ebookFilename;
    }

    public void setEbookFilename(String ebookFilename) {
        this.ebookFilename = ebookFilename;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNumerator() {
        return numerator;
    }

    public void setNumerator(Integer numerator) {
        this.numerator = numerator;
    }

    public Integer getDenominator() {
        return denominator;
    }

    public void setDenominator(Integer denominator) {
        this.denominator = denominator;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }


    public Integer getThumbCount() {
        return thumbCount;
    }

    public void setThumbCount(Integer thumbCount) {
        this.thumbCount = thumbCount;
    }
}

