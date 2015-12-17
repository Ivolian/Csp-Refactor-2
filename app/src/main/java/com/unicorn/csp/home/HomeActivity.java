package com.unicorn.csp.home;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.flyco.banner.anim.select.ZoomInEnter;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.adapter.recyclerView.BookAdapter;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.RecycleViewUtils;

import butterknife.Bind;


public class HomeActivity extends ToolbarActivity {

    @Bind(R.id.shape)
    View view;

    @Bind(R.id.shape1)
    View view1;

    @Bind(R.id.sib_anim)
    SimpleImageBanner simpleImageBanner;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initToolbar("首页", false);

        GradientDrawable gradientDrawable =(GradientDrawable)view.getBackground();
        gradientDrawable.setColor(ColorOverrider.getInstance(this).getColorPrimary());


        GradientDrawable gradientDrawable1 =(GradientDrawable)view1.getBackground();
        gradientDrawable1.setColor(ColorOverrider.getInstance(this).getColorPrimary());

        simpleImageBanner
                .setSelectAnimClass(ZoomInEnter.class)
                .setSource(DataProvider.getList())
                .startScroll();

//        sib.setOnItemClickL(new SimpleImageBanner.OnItemClickL() {
//            @Override
//            public void onItemClick(int position) {
//                T.showShort(context, "position--->" + position);
//            }
//        });
        initRecycleView();
    }

    private void initRecycleView(){
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = RecycleViewUtils.getLinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter( new BookAdapter(this));
    }




}
