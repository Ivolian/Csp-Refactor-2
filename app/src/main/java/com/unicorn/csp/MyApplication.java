package com.unicorn.csp;

import android.database.sqlite.SQLiteDatabase;

import com.unicorn.csp.greendao.DaoMaster;
import com.unicorn.csp.greendao.DaoSession;
import com.unicorn.csp.greendao.MenuDao;
import com.unicorn.csp.greendao.SearchHistoryDao;
import com.unicorn.csp.volley.MyVolley;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cn.jpush.android.api.JPushInterface;


public class MyApplication extends ZLAndroidApplication {

    private static MyApplication instance;

    public static MyApplication getInstance() {

        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        CustomActivityOnCrash.install(this);
        MyVolley.init(this);
        initGreenDao();
        initJPush();
        instance = this;
    }

    private static DaoSession daoSession;

    public static MenuDao getMenuDao(){

        return  daoSession.getMenuDao();
    }

    public static SearchHistoryDao getSearchHistoryDao(){

        return daoSession.getSearchHistoryDao();
    }

    private void initGreenDao(){

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"csp-db",null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private void initJPush(){

        JPushInterface.init(this);
    }

}
