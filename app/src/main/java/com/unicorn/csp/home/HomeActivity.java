package com.unicorn.csp.home;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import com.flyco.banner.anim.select.ZoomInEnter;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ToastUtils;
import com.wenchao.cardstack.CardStack;

import butterknife.Bind;


public class HomeActivity extends ToolbarActivity {

    @Bind(R.id.shape)
    View view;

    @Bind(R.id.shape1)
    View view1;

    @Bind(R.id.sib_anim)
    SimpleImageBanner simpleImageBanner;

@Bind(R.id.cardStack)
 CardStack mCardStack;



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

        mCardStack.setContentResource(R.layout.card_content);
        mCardStack.setStackMargin(20);
        final CardsDataAdapter mCardAdapter = new CardsDataAdapter(getApplicationContext(),0);
        mCardAdapter.add("test1");
        mCardAdapter.add("test2");
        mCardAdapter.add("test3");
        mCardAdapter.add("test4");
        mCardAdapter.add("test5");
        mCardStack.setAdapter(mCardAdapter);

        mCardStack.setListener(new CardStack.CardEventListener() {
            @Override
            public boolean swipeEnd(int i, float v) {

                return true;
            }

            @Override
            public boolean swipeStart(int i, float v) {
                return true;
            }

            @Override
            public boolean swipeContinue(int i, float v, float v1) {
                return true;
            }

            @Override
            public void discarded(int i, int i1) {
                ToastUtils.show(i+"");
                if (i==5){
                    mCardStack.reset(true);
                }
            }

            @Override
            public void topCardTapped() {
            }
        });


    }









}
