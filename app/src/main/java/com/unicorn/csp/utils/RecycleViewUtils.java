package com.unicorn.csp.utils;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;


public class RecycleViewUtils {

    public static LinearLayoutManager getLinearLayoutManager(Activity activity) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return linearLayoutManager;
    }

    public static GridLayoutManager getGridLayoutManager(Activity activity){

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,2);
        return gridLayoutManager;
    }
}
