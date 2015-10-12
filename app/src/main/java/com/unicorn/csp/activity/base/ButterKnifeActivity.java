package com.unicorn.csp.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.f2prateek.dart.Dart;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;

import butterknife.ButterKnife;


public abstract class ButterKnifeActivity extends ColorActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResId) {

        super.setContentView(layoutResId);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Dart.inject(this);
    }


    // ========================== 滑动移除 ==========================

    public void enableSlidr() {

        SlidrConfig config = new SlidrConfig.Builder().edge(true).build();
        Slidr.attach(this, config);
    }


    // ========================== 其他方法 ==========================

    public void startActivity(Class activityClass) {

        startActivity(new Intent(this, activityClass));
    }

    public void startActivityAndFinish(Class activityClass) {

        startActivity(activityClass);
        finish();
    }

}
