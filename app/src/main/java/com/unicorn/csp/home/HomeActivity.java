package com.unicorn.csp.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
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
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


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
        if (response.toString().equals("")) {
            return;
        }
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

    List<Notice> topNoticeList;

    private void initCardStack() {
        cardStack.setContentResource(R.layout.item_card_board);
        addCardStackListener();
        fetchTopNotices();
    }

    private void addCardStackListener() {
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
                if (i == topNoticeList.size()) {
                    cardStack.reset(true);
                }
            }

            @Override
            public void topCardTapped() {
                Notice notice = topNoticeList.get(cardStack.getCurrIndex());
                showNoticeDialog(notice);
            }
        });
    }

    private void showNoticeDialog(Notice notice) {
        new MaterialDialog.Builder(HomeActivity.this)
                .title(notice.getTitle())
                .content(notice.getContent())
                .show();
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
        topNoticeList = GsonUtils.parseNoticeList(response.toString());
        CardsDataAdapter cardsDataAdapter = new CardsDataAdapter(getApplicationContext(), 0);
        if (topNoticeList.size() == 0) {
            topNoticeList.add(getEmptyNotice());
        }
        cardsDataAdapter.addAll(topNoticeList);
        cardStack.setAdapter(cardsDataAdapter);
    }

    private Notice getEmptyNotice() {
        Notice notice = new Notice();
        notice.setTitle("暂无公告");
        notice.setContent("暂无公告");
        notice.setEventTime(new Date().getTime());
        return notice;
    }


    // ========================== show case ==========================

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        menu.findItem(R.id.help).setIcon(getActionDrawable());
        return super.onCreateOptionsMenu(menu);
    }

    private Drawable getActionDrawable() {
        return new IconDrawable(this, Iconify.IconValue.zmdi_pin_help)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                showCase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCase() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setMaskColor(Color.parseColor("#cc111111"));
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(banner, "点击新闻图片查看新闻详情", "我知道了");
        sequence.addSequenceItem(cardStack, "向左下方或右下方滑动公告查看更多", "我知道了");
        sequence.start();
    }

}
