package com.unicorn.csp.activity.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.utils.AppUtils;

import butterknife.Bind;


public class AboutActivity extends ToolbarActivity {

    @Bind(R.id.tv_version_name)
    TextView tvVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initToolbar("关于", true);
        initViews();
        enableSlidr();
    }

    private void initViews() {
        tvVersionName.setText(AppUtils.getVersionName());
    }

}

