package com.unicorn.csp.other;

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.activity.main.LoginActivity;
import com.unicorn.csp.utils.AppUtils;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.volley.MyVolley;

import org.json.JSONObject;

import java.util.Date;


public class LoginHelper {

    public static void checkLoginTime() {

        TinyDB tinyDB = new TinyDB(MyApplication.getInstance());
        long lastLoginTime = tinyDB.getLong(LoginActivity.SF_LAST_LOGIN_TIME, 0);
        // 相差毫秒数
        long distance = new Date().getTime() - lastLoginTime;
        if (distance > 86400000) {
            login();
        }
    }

    private static void login() {

        MyVolley.addRequest(new JsonObjectRequest(getLoginUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TinyDB tinyDB = new TinyDB(MyApplication.getInstance());
                        tinyDB.putLong(LoginActivity.SF_LAST_LOGIN_TIME, new Date().getTime());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }));
    }

    private static String getLoginUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/user/login?").buildUpon();
        TinyDB tinyDB = new TinyDB(MyApplication.getInstance());
        String username = tinyDB.getString(LoginActivity.SF_USERNAME);
        builder.appendQueryParameter("username", username);
        String password = tinyDB.getString(LoginActivity.SF_PASSWORD);
        builder.appendQueryParameter("password", password);
        builder.appendQueryParameter("currentVersionName", AppUtils.getVersionName());
        return builder.toString();
    }

}
