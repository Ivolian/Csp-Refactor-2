package com.unicorn.csp.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unicorn.csp.model.Notice;

import java.util.List;


public class GsonUtils {

    public static List<Notice> parseNoticeList(String str) {
        Gson gson = new Gson();
        List<Notice> workOrderProcessInfoList = gson.
                fromJson(str, new TypeToken<List<Notice>>() {
                }.getType());
        return workOrderProcessInfoList;
    }

}