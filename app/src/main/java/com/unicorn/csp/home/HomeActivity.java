package com.unicorn.csp.home;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.volley.MyVolley;
import com.wenchao.cardstack.CardStack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
        initToolbar("首页", true);
        enableSlidr();

        GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
        gradientDrawable.setColor(ColorOverrider.getInstance(this).getColorPrimary());


        GradientDrawable gradientDrawable1 = (GradientDrawable) view1.getBackground();
        gradientDrawable1.setColor(ColorOverrider.getInstance(this).getColorPrimary());

fetchTopNews();
//        simpleImageBanner.setOnItemClickL(new SimpleImageBanner.OnItemClickL() {
//            @Override
//            public void onItemClick(int position) {
//               simpleImageBanner
//            }
//        });

        mCardStack.setContentResource(R.layout.card_content);

        final CardsDataAdapter mCardAdapter = new CardsDataAdapter(getApplicationContext(), 0);
        mCardAdapter.add("test1");
        mCardAdapter.add("test2");
        mCardAdapter.add("test3");
        mCardAdapter.add("test4");
        mCardAdapter.add("test5");
        mCardStack.setAdapter(mCardAdapter);

        mCardStack.setListener(new CardStack.CardEventListener() {
            @Override
            public boolean swipeEnd(int i, float v) {
                if (i == 0 || i == 1)
                    return false;
                return true;
            }

            @Override
            public boolean swipeStart(int i, float v) {

                if (i == 0 || i == 1)
                    return false;

//                ToastUtils.show(""+i);

                return true;
            }

            @Override
            public boolean swipeContinue(int i, float v, float v1) {

                if (i == 0 || i == 1)
                    return false;

                return true;
            }

            @Override
            public void discarded(int i, int i1) {
//                ToastUtils.show(i+"");
                if (i == 5) {
                    mCardStack.reset(true);
                }
            }

            @Override
            public void topCardTapped() {
            }
        });


    }


    private void fetchTopNews() {
        String url = ConfigUtils.getBaseUrl() + "/api/v1/news/topList";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
            copeTopNewsResponse(response);
                    }
                },
                MyVolley.getDefaultErrorListener()
        );
        MyVolley.addRequest(jsonArrayRequest);
    }

    private void copeTopNewsResponse(JSONArray response){


        ArrayList<BannerItem> list = new ArrayList<>();
        for (int i = 0; i != response.length(); i++) {
          JSONObject jsonObject = JSONUtils.getJSONObject(response,i);
          String picture = JSONUtils.getString(jsonObject,"picture","");
          String title = JSONUtils.getString(jsonObject,"title","");
            BannerItem item = new BannerItem();
            item.imgUrl = ConfigUtils.getBaseUrl() + picture;
            item.title = title;
            list.add(item);
            simpleImageBanner
                    .setSelectAnimClass(ZoomInEnter.class)
                    .setSource(list)
                    .startScroll();

        }
    }


}
