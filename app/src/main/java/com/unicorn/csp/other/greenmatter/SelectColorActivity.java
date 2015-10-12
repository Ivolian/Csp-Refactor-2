package com.unicorn.csp.other.greenmatter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.other.TinyDB;

import butterknife.Bind;


public class SelectColorActivity extends ToolbarActivity {

    public static final String SF_ENABLE = "enable";

    public static final String SF_ACCENT_HUE = "accent_hue";

    public static final int SELECT_COLOR_SUCCESS = 2333;

    @Bind(R.id.fab)
    com.melnykov.fab.FloatingActionButton floatingActionButton;

    @Bind(R.id.overrideSwitch)
    SwitchCompat mOverrideSwitch;

//    @Bind(R.id.primary_seekbar)
//    SeekBar mPrimarySeekbar;

    @Bind(R.id.accent_seekbar)
    SeekBar mAccentSeekbar;

//    @Bind(R.id.primary_preview)
//    View mPrimaryPreview;

    @Bind(R.id.accent_preview)
    View mAccentPreview;

//    @Bind(R.id.lightToggle)
//    ToggleButton mLightToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_color);
        initToolbar("主题色彩", true);
        enableSlidr();

        // 成功设置主题
        final ColorOverrider overrider = ColorOverrider.getInstance(this);
        floatingActionButton.setImageDrawable(getBrushDrawable());
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overrider.setEnabled(mOverrideSwitch.isChecked());
                overrider.setAccentHue(mAccentSeekbar.getProgress());
                overrider.setPrimaryHue(mAccentSeekbar.getProgress());

                TinyDB tinyDB = new TinyDB(SelectColorActivity.this);
                tinyDB.putBoolean(SF_ENABLE,mOverrideSwitch.isChecked());
                tinyDB.putFloat(SF_ACCENT_HUE,mAccentSeekbar.getProgress());

                setResult(SELECT_COLOR_SUCCESS);
                finish();
            }
        });

        // 自定义主题
        mOverrideSwitch.setChecked(overrider.isEnabled());
        mOverrideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setComponentsEnable(isChecked, overrider);
            }
        });

//        // 选择颜色
//        mPrimarySeekbar.setProgress((int) overrider.getPrimaryHue());
//        mPrimarySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                int color = ColorUtils.replaceHue(overrider.getColorPrimary(), progress);
//                mPrimaryPreview.setBackgroundColor(color);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
        mAccentSeekbar.setProgress((int) overrider.getAccentHue());
        mAccentSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int color = ColorUtils.replaceHue(overrider.getColorAccent(), progress);
                mAccentPreview.setBackgroundColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        mLightToggle.setChecked(overrider.isLightTheme());
//        mLightToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                overrider.setLightTheme(isChecked);
//                setComponentsEnable(mOverrideSwitch.isEnabled(), overrider);
//            }
//        });
        setComponentsEnable(overrider.isEnabled(), overrider);
    }

    private void setComponentsEnable(boolean enabled, ColorOverrider overrider) {
//        mPrimarySeekbar.setEnabled(enabled);
        mAccentSeekbar.setEnabled(enabled);
//        mLightToggle.setEnabled(enabled);

//        int primaryColor = ColorUtils.replaceHue(overrider.getColorPrimary(), mPrimarySeekbar.getProgress());
//        mPrimaryPreview.setBackgroundColor(enabled ? primaryColor : Color.DKGRAY);
        int accentColor = ColorUtils.replaceHue(overrider.getColorAccent(), mAccentSeekbar.getProgress());
        mAccentPreview.setBackgroundColor(enabled ? accentColor : Color.GRAY);
    }

    private Drawable getBrushDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_brush)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }

}
