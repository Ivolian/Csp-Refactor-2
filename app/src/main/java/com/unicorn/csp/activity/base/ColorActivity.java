package com.unicorn.csp.activity.base;

import com.negusoft.greenmatter.MatPalette;
import com.negusoft.greenmatter.activity.MatActivity;
import com.unicorn.csp.other.greenmatter.ColorOverrider;


// clear
public abstract class ColorActivity extends MatActivity {

    @Override
    public MatPalette overridePalette(MatPalette palette) {

        return ColorOverrider.getInstance(this).applyOverride(palette);
    }

}
