package com.unicorn.csp.home;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.activity.news.NewsDetailActivity;
import com.unicorn.csp.model.News;
import com.unicorn.csp.model.Notice;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.GsonUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.volley.MyVolley;
import com.wenchao.cardstack.CardStack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;


public class HomeActivity extends ToolbarActivity {


    // ========================== onCreate ==========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initToolbar("首页", true);
        initViews();
        enableSlidr();
    }

    private void initViews() {
        initShape();
        initBanner();
        initCardStack();
    }


    // ========================== shape ==========================

    @Bind(R.id.shape)
    View view;

    @Bind(R.id.shape1)
    View view1;

    private void initShape() {
        GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
        int colorPrimary = ColorOverrider.getInstance(this).getColorPrimary();
        gradientDrawable.setColor(colorPrimary);
        gradientDrawable = (GradientDrawable) view1.getBackground();
        gradientDrawable.setColor(colorPrimary);
    }


    // ========================== banner ==========================

    @Bind(R.id.banner)
    SimpleImageBanner banner;

    List<News> topNewsList;

    private void initBanner() {
        fetchTopNews();
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

    private void copeTopNewsResponse(JSONArray response) {
        topNewsList = parseTopNewsList(response);
        banner.setSelectAnimClass(ZoomInEnter.class)
                .setSource(topNewsList)
                .startScroll();
        banner.setOnItemClickL(new SimpleImageBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                startNewsDetailActivity(topNewsList.get(position));
            }
        });
    }

    private void startNewsDetailActivity(News news) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("news", news);
        startActivity(intent);
    }

    private List<News> parseTopNewsList(JSONArray response) {
        List<News> topNewsList = new ArrayList<>();
        for (int i = 0; i != response.length(); i++) {
            JSONObject newsJSONObject = JSONUtils.getJSONObject(response, i);
            String id = JSONUtils.getString(newsJSONObject, "id", "");
            String picture = JSONUtils.getString(newsJSONObject, "picture", "");
            String title = JSONUtils.getString(newsJSONObject, "title", "");
            Date postTime = new Date(JSONUtils.getLong(newsJSONObject, "postTime", 0));
            int commentCount = JSONUtils.getInt(newsJSONObject, "commentCount", 0);
            int thumbCount = JSONUtils.getInt(newsJSONObject, "thumbCount", 0);
            int hasVideo = JSONUtils.getInt(newsJSONObject, "hasVideo", 0);
            int videoType = JSONUtils.getInt(newsJSONObject, "videoType", 0);
            String videoUrl = JSONUtils.getString(newsJSONObject, "videoUrl", "");
            News news = new News();
            news.setId(id);
            news.setTitle(title);
            news.setPicture(picture);
            news.setTime(postTime);
            news.setCommentCount(commentCount);
            news.setThumbCount(thumbCount);
            news.setHasVideo(hasVideo);
            news.setVideoType(videoType);
            news.setVideoUrl(videoUrl);
            topNewsList.add(news);
        }
        return topNewsList;
    }


    // ========================== card stack ==========================

    @Bind(R.id.cardStack)
    CardStack cardStack;

    int topNoticesSize = -1;

    private void initCardStack() {
        fetchTopNotices();
        cardStack.setContentResource(R.layout.item_card_board);
        cardStack.setListener(new CardStack.CardEventListener() {
            @Override
            public boolean swipeEnd(int i, float v) {
                return i == 2 || i == 3;
            }

            @Override
            public boolean swipeStart(int i, float v) {
                return i == 2 || i == 3;
            }

            @Override
            public boolean swipeContinue(int i, float v, float v1) {
                return i == 2 || i == 3;
            }

            @Override
            public void discarded(int i, int i1) {
                if (i == topNoticesSize) {
                    cardStack.reset(true);
                }
            }

            @Override
            public void topCardTapped() {
            }
        });
    }

    private void fetchTopNotices() {
        String url = ConfigUtils.getBaseUrl() + "/api/v1/notice/topList";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        copeTopNoticesResponse(response);
                    }
                },
                MyVolley.getDefaultErrorListener()
        );
        MyVolley.addRequest(jsonArrayRequest);
    }

    private void copeTopNoticesResponse(JSONArray response) {
        final CardsDataAdapter mCardAdapter = new CardsDataAdapter(getApplicationContext(), 0);
        List<Notice> topNoticeList = GsonUtils.parseNoticeList(response.toString());
        topNoticesSize = topNoticeList.size();
        mCardAdapter.addAll(topNoticeList);
        cardStack.setAdapter(mCardAdapter);
    }

}
