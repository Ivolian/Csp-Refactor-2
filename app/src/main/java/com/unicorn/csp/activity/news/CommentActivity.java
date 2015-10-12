package com.unicorn.csp.activity.news;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.f2prateek.dart.InjectExtra;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;
import com.r0adkll.slidr.model.SlidrConfig;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.adapter.recyclerView.CommentAdapter;
import com.unicorn.csp.model.Comment;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.RecycleViewUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class CommentActivity extends ToolbarActivity {


    // ==================== views ====================

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.fab)
    FloatingActionButton fab;


    // ==================== 必要参数 newsId ====================

    // 可能来自 NewsDetailActivity，可能来自 AddCommentActivity。
    @InjectExtra("newsId")
    String newsId;


    // ==================== commentAdapter ====================

    CommentAdapter commentAdapter;


    // ==================== page data ====================

    final Integer PAGE_SIZE = 10;

    Integer pageNo;

    boolean loadingMore;

    boolean lastPage;


    // ==================== onCreate ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initToolbar("评论", true);
        enableSlidr();

        initViews();
    }

    private void initViews() {

        initSwipeRefreshLayout();
        initRecyclerView();
        initFab();
        reload();
    }

    private void initSwipeRefreshLayout() {

        swipeRefreshLayout.setColorSchemeColors(ColorOverrider.getInstance(this).getColorAccent());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
    }

    private void initRecyclerView() {

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = RecycleViewUtils.getLinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentAdapter = new CommentAdapter());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (lastPage || loadingMore) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    if (totalItemCount != 0 && totalItemCount == (lastVisibleItem + 1)) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
    }

    private void initFab() {

        fab.setImageDrawable(getFabDrawable());
        fab.attachToRecyclerView(recyclerView);
    }

    private Drawable getFabDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_comment_text_alt)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }


    // ==================== onNewIntent ====================

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        reload();
    }


    // ==================== 发表评论点击事件 ====================

    @OnClick(R.id.fab)
    public void onFabClick() {

        startAddCommentActivity();
    }

    private void startAddCommentActivity() {

        Intent intent = new Intent(this, AddCommentActivity.class);
        intent.putExtra("newsId", newsId);
        startActivity(intent);

        SlidrConfig config = new SlidrConfig.Builder()
                .edge(true)
                .build();
    }


    // ====================== 通用的分页方法，可否抽象出父类呢 ======================

    private String getUrl(Integer pageNo) {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/comment/listForMobile?").buildUpon();
        builder.appendQueryParameter("pageNo", pageNo.toString());
        builder.appendQueryParameter("pageSize", PAGE_SIZE.toString());
        builder.appendQueryParameter("newsId", newsId);
        return builder.toString();
    }

    private List<Comment> parseCommentList(JSONObject response) {

        JSONArray commentJSONArray = JSONUtils.getJSONArray(response, "content", null);
        List<Comment> commentList = new ArrayList<>();
        for (int i = 0; i != commentJSONArray.length(); i++) {
            JSONObject commentJSONObject = JSONUtils.getJSONObject(commentJSONArray, i);
            String displayName = JSONUtils.getString(commentJSONObject, "displayName", "");
            Date eventTime = new Date(JSONUtils.getLong(commentJSONObject, "eventtime", 0));
            String content = JSONUtils.getString(commentJSONObject, "content", "");
            commentList.add(new Comment(displayName, eventTime, content));
        }
        return commentList;
    }

    public void reload() {

        clearPageData();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        MyVolley.addRequest(new JsonObjectRequest(getUrl(pageNo),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        stopRefreshing();
                        commentAdapter.setCommentList(parseCommentList(response));
                        commentAdapter.notifyDataSetChanged();
                        checkLastPage(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        stopRefreshing();
                        ToastUtils.show(VolleyErrorHelper.getErrorMessage(volleyError));
                    }
                }));
    }

    private void loadMore() {

        loadingMore = true;
        MyVolley.addRequest(new JsonObjectRequest(getUrl(pageNo + 1),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingMore = false;
                        pageNo++;
                        commentAdapter.getCommentList().addAll(parseCommentList(response));
                        commentAdapter.notifyDataSetChanged();
                        checkLastPage(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loadingMore = false;
                        ToastUtils.show(VolleyErrorHelper.getErrorMessage(volleyError));
                    }
                }));
    }

    private void clearPageData() {

        pageNo = 1;
        lastPage = false;
    }

    private void checkLastPage(JSONObject response) {

        if (lastPage = isLastPage(response)) {
            ToastUtils.show(noData(response) ? "暂无数据" : "已加载全部数据");
        }
    }

    private boolean isLastPage(JSONObject response) {

        return JSONUtils.getBoolean(response, "lastPage", false);
    }

    private boolean noData(JSONObject response) {

        return JSONUtils.getInt(response, "totalPages", 0) == 0;
    }

    private void stopRefreshing() {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
