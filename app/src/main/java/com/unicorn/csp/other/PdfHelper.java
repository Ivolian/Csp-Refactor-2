package com.unicorn.csp.other;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.unicorn.csp.model.Book;


public class PdfHelper {

    public static int getPDFPageCount(Book book,Activity activity){

        String pdfId = book.getId();
        String key = pdfId + "_" + "pageCount";

        String sfName = "com.artifex.mupdfdemo.MuPDFActivity";
        SharedPreferences sharedPreferences = activity.getSharedPreferences(sfName, Context.MODE_WORLD_READABLE);
        int pageCount = sharedPreferences.getInt(key, 0);

        return pageCount;
    }

    // MuPDF 存的是 0-255 ，实际是指 1-256
    public static int getPDFPage(Book book,Activity activity){

        String pdfId = book.getId();
        String key = pdfId + "_" + "page";

        String sfName = "com.artifex.mupdfdemo.MuPDFActivity";
        SharedPreferences sharedPreferences = activity.getSharedPreferences(sfName, Context.MODE_WORLD_READABLE);
        int page = sharedPreferences.getInt(key, 0);

        return page;
    }

}
