package com.unicorn.csp.activity.news;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.f2prateek.dart.InjectExtra;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.model.News;
import com.unicorn.csp.other.TinyDB;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.other.webview.VideoEnabledWebChromeClient;
import com.unicorn.csp.other.webview.VideoEnabledWebView;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;


public class NewsDetailActivity extends ToolbarActivity implements ObservableScrollViewCallbacks, FilterMenu.OnMenuChangeListener {

    /*
        新闻详情界面
        1. 可以通过 VideoEnabledWebView 加载服务器上的视频
        也可以通过优酷连接什么的，直接播放优酷的视频。
        2. VideoEnabledWebView 和 video 标签研究较少，有需要时再说
     */

    @InjectExtra("news")
    News news;

    @Bind(R.id.filter_menu_layout)
    FilterMenuLayout filterMenuLayout;

    @Bind(R.id.webview)
    VideoEnabledWebView webView;

    VideoEnabledWebChromeClient webChromeClient;

    @Bind(R.id.seekbar)
    DiscreteSeekBar seekBar;


    // =============================== onCreate ===============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        initToolbar(news.getTitle(), true);
        enableSlidr();

        initViews();


    }

    private void initViews() {

        initSeekBar();
        initWebView();
        initFilterMenuLayout();
        loadContent();
    }

    private void initSeekBar() {

        seekBar.setVisibility(View.GONE);
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
        enableVideo();
    }

    private void enableVideo() {

        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
    }

    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }


    // =============================== loadContent ===============================

    private void loadContent() {

        MyVolley.addRequest(new StringRequest(getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webView.loadData(response, "text/html; charset=UTF-8", null);
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/news/newsData?").buildUpon();
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        builder.appendQueryParameter("newsId", news.getId());
        return builder.toString();
    }


    // =============================== initFilterMenuLayout ===============================

    private void initFilterMenuLayout() {

        int darkerColor = (int) new ArgbEvaluator().evaluate(0.7f, Color.parseColor("#000000"), ColorOverrider.getInstance(this).getColorAccent());
        filterMenuLayout.setPrimaryDarkColor(darkerColor);
        attachMenu(filterMenuLayout);
    }

    private FilterMenu attachMenu(FilterMenuLayout layout) {

        // 添加评论，查看评论，点赞，关注
        return new FilterMenu.Builder(this)
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_star, 28))
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_thumb_up, 24))
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_comment_more, 25))
                .addItem(getIconDrawable(Iconify.IconValue.zmdi_comment_text_alt, 25))
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

        if (seekBar.getVisibility() == View.GONE) {
            seekBar.setVisibility(View.VISIBLE);
        } else {
            seekBar.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }


}
