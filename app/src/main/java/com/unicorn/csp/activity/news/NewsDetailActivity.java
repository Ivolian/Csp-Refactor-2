package com.unicorn.csp.activity.news;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.unicorn.csp.R;
import com.unicorn.csp.model.News;
import com.unicorn.csp.other.LoginHelper;
import com.unicorn.csp.other.TinyDB;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.wang.avi.AVLoadingIndicatorView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.video.venvy.param.JjVideoRelativeLayout;
import cn.com.video.venvy.param.JjVideoView;
import cn.com.video.venvy.param.OnJjOpenSuccessListener;
import cn.com.video.venvy.param.VideoJjMediaContoller;
import me.grantland.widget.AutofitTextView;


public class NewsDetailActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, FilterMenu.OnMenuChangeListener {


    // =============================== extra ===============================

    @InjectExtra("news")
    News news;


    // =============================== extra ===============================

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.atv_toolbar_title)
    AutofitTextView atvToolbarTitle;

    @Bind(R.id.videoLayout)
    JjVideoRelativeLayout jjVideoRelativeLayout;

    @Bind(R.id.video)
    JjVideoView videoView;

    @Bind(R.id.avloadingIndicatorView)
    AVLoadingIndicatorView avLoadingIndicatorView;

    @Bind(R.id.webview)
    ObservableWebView webView;

    @Bind(R.id.filter_menu_layout)
    FilterMenuLayout filterMenuLayout;


    // =============================== onCreate ===============================
    // video 不能和 ColorActivity 的主题兼容，所以...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dart.inject(this);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        atvToolbarTitle.setText(news.getTitle());
        toolbar.setBackgroundColor(ColorOverrider.getInstance(this).getColorPrimary());

        initViews();
        LoginHelper.checkLoginTime();
    }


    // =============================== onConfigurationChanged ===============================

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbar.setVisibility(View.VISIBLE);
            filterMenuLayout.setVisibility(View.VISIBLE);
        }
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
            filterMenuLayout.setVisibility(View.GONE);
        }
    }


    // =============================== initViews ===============================

    private void initViews() {
        initVideoView();
        initWebView();
        loadNewData();
        initFilterMenuLayout();
    }

    private void initVideoView() {
        if (news.getHasVideo() == 0) {
            jjVideoRelativeLayout.setVisibility(View.GONE);
            return;
        }

        videoView.setMediaController(new VideoJjMediaContoller(this, true));
        videoView.setMediaBufferingView(avLoadingIndicatorView);
        videoView.setOnJjOpenSuccess(new OnJjOpenSuccessListener() {

            @Override
            public void onJjOpenSuccess() {
                avLoadingIndicatorView.setVisibility(View.GONE);
            }
        });
        String appKey = "EyvxCaZBe";
        videoView.setVideoJjAppKey(appKey);
        videoView.setVideoJjPageName("com.unicorn.csp");
        videoView.setMediaCodecEnabled(true);// 是否开启 硬解 硬解对一些手机有限制
        // 判断是否源 0 代表 8大视频网站url 1代表自己服务器的视频源 2代表直播地址 3代表本地视频(手机上的视频源),4特殊需求
        videoView.setVideoJjType(news.getVideoType());
        videoView.setResourceVideo(news.getVideoUrl());
    }


    // =============================== initWebView ===============================

    private void initWebView() {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setTextZoom(getTextZoom());
        webView.setWebViewClient(new WebViewClient());
        webView.setScrollViewCallbacks(this);
    }


    // =============================== loadNewData ===============================

    private void loadNewData() {
        MyVolley.addRequest(new StringRequest(getNewDataUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webView.loadData(response, "text/html; charset=UTF-8", null);
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getNewDataUrl() {
        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/news/newsData?").buildUpon();
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        builder.appendQueryParameter("newsId", news.getId());
        return builder.toString();
    }


    // =============================== initFilterMenuLayout ===============================

    private void initFilterMenuLayout() {
        int darkerColor = (int) new ArgbEvaluator().evaluate(0.7f, Color.parseColor("#000000"), ColorOverrider.getInstance(this).getColorAccent());
        filterMenuLayout.setPrimaryDarkColor(darkerColor);
        filterMenuLayout.setPrimaryColor(ColorOverrider.getInstance(this).getColorPrimary());
        attachMenu(filterMenuLayout);
    }

    private FilterMenu attachMenu(FilterMenuLayout layout) {
        // 添加评论，查看评论，点赞，关注
        return new FilterMenu.Builder(this)
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_star, 28))
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_thumb_up, 24))
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_comment_more, 25))
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_comment_text_alt, 25))
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_view_list, 25))
                .attach(layout)
                .withListener(this)
                .build();
    }


    // =============================== FilterMenu 点击事件 ===============================

    @Override
    public void onMenuItemClick(View view, int position) {

        switch (position) {
            case 0:
                addFavorite();
                break;
            case 1:
                addThumb();
                break;
            case 2:
                startCommentActivity();
                break;
            case 3:
                startAddCommentActivity();
                break;
            case 4:
                startThumbActivity();
                break;
        }
    }

    @Override
    public void onMenuCollapse() {

    }

    @Override
    public void onMenuExpand() {

    }


    // =============================== 监听 WebView 滚动事件 ===============================

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            filterMenuLayout.setVisibility(View.GONE);
        } else if (scrollState == ScrollState.DOWN) {
            filterMenuLayout.setVisibility(View.VISIBLE);
        }
    }


    // =============================== 其他方法 ===============================

    private Drawable getIconDrawable(Iconify.IconValue iconValue, int size) {

        return new IconDrawable(this, iconValue)
                .colorRes(android.R.color.white)
                .sizeDp(size);
    }

    private void startAddCommentActivity() {

        Intent intent = new Intent(this, AddCommentActivity.class);
        intent.putExtra("newsId", news.getId());
        startActivity(intent);
    }

    private void startCommentActivity() {

        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("newsId", news.getId());
        startActivity(intent);
    }

    private void addFavorite() {

        MyVolley.addRequest(new StringRequest(getFavoriteUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean result = response.equals(Boolean.TRUE.toString());
                        ToastUtils.show(result ? "关注成功" : "已关注");
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getFavoriteUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/favorite/create?").buildUpon();
        builder.appendQueryParameter("newsId", news.getId());
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        return builder.toString();
    }

    private void addThumb() {

        MyVolley.addRequest(new StringRequest(getThumbUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean result = response.equals(Boolean.TRUE.toString());
                        ToastUtils.show(result ? "点赞成功" : "已点赞");
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getThumbUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/thumb/create?").buildUpon();
        builder.appendQueryParameter("newsId", news.getId());
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        return builder.toString();
    }

    private void startThumbActivity() {

        Intent intent = new Intent(this, ThumbActivity.class);
        intent.putExtra("newsId", news.getId());
        startActivity(intent);
    }


    // =============================== textZoom ===============================

    private String SF_TEXT_ZOOM = "text_zoom";

    private int getTextZoom() {

        TinyDB tinyDB = new TinyDB(this);
        return tinyDB.getInt(SF_TEXT_ZOOM, 100);
    }

    private void saveTextZoom(int textZoom) {

        TinyDB tinyDB = new TinyDB(this);
        tinyDB.putInt(SF_TEXT_ZOOM, textZoom);
    }


    // ========================== 菜单 ==========================

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail, menu);
        menu.findItem(R.id.font).setIcon(getActionDrawable());
        return super.onCreateOptionsMenu(menu);
    }

    private Drawable getActionDrawable() {
        return new IconDrawable(this, Iconify.IconValue.zmdi_font)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.font) {
            showFontAdjustDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFontAdjustDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_font_adjust, true)
                .show();
        if (dialog.getCustomView() != null) {
            DiscreteSeekBar seekBar = (DiscreteSeekBar) dialog.getCustomView().findViewById(R.id.seekbar);
            initSeekBar(seekBar);
        }
    }

    private void initSeekBar(DiscreteSeekBar seekBar) {
        seekBar.setProgress(getTextZoom());
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                int progress = discreteSeekBar.getProgress();
                webView.getSettings().setTextZoom(progress);
                saveTextZoom(progress);
            }
        });
    }

}
