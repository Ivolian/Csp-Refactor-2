package com.unicorn.csp.activity.setting;

import android.os.Bundle;

import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;

import butterknife.OnClick;


public class SettingActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolbar("更多设置", true);
        enableSlidr();
    }

    @OnClick(R.id.tv_change_password)
    public void startChangePasswordActivity(){

        startActivity(ChangePasswordActivity.class);
    }

    @OnClick(R.id.tv_about)
    public void startAboutActivity(){

        startActivity(AboutActivity.class);
    }

}
