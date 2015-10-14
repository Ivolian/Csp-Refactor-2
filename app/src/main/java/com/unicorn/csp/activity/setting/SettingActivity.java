package com.unicorn.csp.activity.setting;

import android.os.Bundle;
import android.provider.Settings;

import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.OnClick;


public class SettingActivity extends ToolbarActivity {


    // ================ views =================

    @Bind(R.id.dsb_brightness)
    DiscreteSeekBar dsbBrightness;


    // ================ onCreate =================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolbar("更多设置", true);
        enableSlidr();
        initSbBrightness();
    }

    private void initSbBrightness() {

        // 亮度手动调节模式
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        int currentBrightness = 0;
        try {
            currentBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            //
        }
        dsbBrightness.setProgress(currentBrightness);
        dsbBrightness.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {

                android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, progress);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }


    // ================ OnClick =================

    @OnClick(R.id.tv_change_password)
    public void startChangePasswordActivity() {

        startActivity(ChangePasswordActivity.class);
    }

    @OnClick(R.id.tv_about)
    public void startAboutActivity() {

        startActivity(AboutActivity.class);
    }

}
