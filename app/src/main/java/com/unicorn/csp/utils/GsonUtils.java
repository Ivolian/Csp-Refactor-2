package com.unicorn.csp.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unicorn.csp.model.Comment;
import com.unicorn.csp.model.News;
import com.unicorn.csp.model.Notice;
import com.unicorn.csp.model.Thumb;

import java.util.List;


public class GsonUtils {

    public static List<Notice> parseNoticeList(String str) {
        Gson gson = new Gson();
        List<Notice> workOrderProcessInfoList = gson.
                fromJson(str, new TypeToken<List<Notice>>() {
                }.getType());
        return workOrderProcessInfoList;
    }

    public static List<News> parseNewsList(String str) {
        Gson gson = new Gson();
        List<News> newsList = gson.
                fromJson(str, new TypeToken<List<News>>() {
                }.getType());
        return newsList;
    }

    public static List<Comment> parseCommentList(String str) {
        Gson gson = new Gson();
        List<Comment> commentList = gson.
                fromJson(str, new TypeToken<List<Comment>>() {
                }.getType());
        return commentList;
    }

    public static List<Thumb> parseThumbList(String str) {
        Gson gson = new Gson();
        List<Thumb> thumbList = gson.
                fromJson(str, new TypeToken<List<Thumb>>() {
                }.getType());
        return thumbList;
    }
}