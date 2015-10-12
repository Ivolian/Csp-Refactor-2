package com.unicorn.csp.other.mycode;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class MyViewPager extends ViewPager
{
    public MyViewPager(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MyViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y)
    {
        if (v != this && v instanceof SwipeableRecycleView)
        {// 解决冲突
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

}