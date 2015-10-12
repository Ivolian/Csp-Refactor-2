package com.unicorn.csp.utils;

import android.os.Environment;

import com.unicorn.csp.MyApplication;
import com.unicorn.csp.other.TinyDB;

import java.io.File;


public class ConfigUtils {

    public static String getBaseUrl() {

//        return "http://115.28.239.33:8080/withub";
//        return "http://192.168.7.90:3000/withub";
//        return "http://192.168.7.59:3000/withub";
        return "http://58.16.65.7:8090/withub";
    }


    final static String SF_USER_ID = "userId";

    public static void saveUserId(String userId) {

        new TinyDB(MyApplication.getInstance().getApplicationContext()).putString(SF_USER_ID, userId);
    }

    public static String getUserId() {

        return new TinyDB(MyApplication.getInstance().getApplicationContext()).getString(SF_USER_ID);
    }


    public static String getDownloadDirPath() {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "csp");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath();
    }

}
