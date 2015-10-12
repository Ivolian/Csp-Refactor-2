package com.unicorn.csp.utils;

import android.content.Context;

import com.unicorn.csp.MyApplication;


public class AppUtils {

    public static String getVersionName() {

        Context context = MyApplication.getInstance();
        String packageName = context.getPackageName();
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            //
        }
        return versionName;
    }

}
