package com.unicorn.csp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unicorn.csp.R;
import com.unicorn.csp.activity.bookCity.WebViewActivity;
import com.unicorn.csp.fragment.base.ButterKnifeFragment;

import java.util.Arrays;
import java.util.List;


public class BookCityFragment extends ButterKnifeFragment {

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_bookcity;
    }

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = super.onCreateView(inflater, container, savedInstanceState);
        initViews();
        return rootView;
    }

    // todo 等知网那边做出回应
    private String[] urls = {
            "http://i.cnki.net/Mobile/Home/Index/",
            "http://www.dz.cnki.net/kns55/index.html",
            "http://kepu.cnki.net/KNetWeb/Nonacad/nonacad/OutHref/cjft?type=in",
            "http://wenhua.cnki.net/KNetWeb/Nonacad/nonacad/OutHref/CJFU?type=in",
            "http://wenyi.cnki.net//KNetWeb/Nonacad/nonacad/OutHref/cjfv?type=in"
    };

    /*
         "http://www.dz.cnki.net/kns55/index.html?username=sygzgf&password=sygzgf123",
            "http://kepu.cnki.net/KNetWeb/Nonacad/nonacad/OutHref/cjft?type=in&username=sygzgf&password=sygzgf123",
            "http://wenhua.cnki.net/KNetWeb/Nonacad/nonacad/OutHref/CJFU?type=in&username=sygzgf&password=sygzgf123",
            "http://wenyi.cnki.net//KNetWeb/Nonacad/nonacad/OutHref/cjfv?type=in&username=sygzgf&password=sygzgf123"
     */

    private List<Integer> zhiWangIds = Arrays.asList(R.id.tv_zhiwang1, R.id.tv_zhiwang2, R.id.tv_zhiwang3, R.id.tv_zhiwang4, R.id.tv_zhiwang5);

    private void initViews() {

        for (final int id : zhiWangIds) {
            rootView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("url", urls[zhiWangIds.indexOf(id)]);
                    getActivity().startActivity(intent);
                }
            });
        }
    }

}
