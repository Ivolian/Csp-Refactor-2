package com.unicorn.csp.fragment.viewpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.unicorn.csp.R;
import com.unicorn.csp.adapter.viewpager.ViewPagerAdapter;
import com.unicorn.csp.fragment.base.ButterKnifeFragment;
import com.unicorn.csp.other.greenmatter.ColorOverrider;

import butterknife.Bind;


public class ViewPagerFragmentL1 extends ButterKnifeFragment {

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_viewpager_l1;
    }

    @Bind(R.id.tabs)
    PagerSlidingTabStrip pagerSlidingTabStrip;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        initViews();
        return rootView;
    }

    private void initViews() {

        com.unicorn.csp.greendao.Menu menu = (com.unicorn.csp.greendao.Menu) getArguments().getSerializable("menu");
        viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), menu));
        if (menu != null) {
            viewPager.setOffscreenPageLimit(menu.getChildren().size());
        }
        pagerSlidingTabStrip.setViewPager(viewPager);
        pagerSlidingTabStrip.setIndicatorColor(ColorOverrider.getInstance(getActivity()).getColorAccent());
    }

}
