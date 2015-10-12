package com.unicorn.csp.fragment.question;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unicorn.csp.R;
import com.unicorn.csp.adapter.recyclerView.question.QuestionAdapter;
import com.unicorn.csp.fragment.base.LazyLoadFragment;
import com.unicorn.csp.model.Question;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.RecycleViewUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;


public abstract class QuestionFragment extends LazyLoadFragment {

    abstract String getType();

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    // ==================== views ====================

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;


    // ==================== QuestionAdapter ====================

    public QuestionAdapter questionAdapter;


    // ==================== page data ====================

    final Integer PAGE_SIZE = 10;

    Integer pageNo;

    boolean loadingMore;

    boolean lastPage;


    // ==================== onFirstUserVisible ====================

    @Override
    public void onFirstUserVisible() {

        initViews();
    }

    private void initViews() {

        initSwipeRefreshLayout();
        initRecyclerView();
        reload();
    }

    private void initSwipeRefreshLayout() {

        swipeRefreshLayout.setColorSchemeColors(ColorOverrider.getInstance(getActivity()).getColorAccent());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
    }

    private void initRecyclerView() {

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = RecycleViewUtils.getLinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(questionAdapter = new QuestionAdapter());
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
    }

    public void reload() {

        clearPageData();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                startRefreshing();
            }
        });
        MyVolley.addRequest(new JsonObjectRequest(getUrl(pageNo),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        stopRefreshing();
                        questionAdapter.setQuestionList(parseQuestionList(response));
                        questionAdapter.notifyDataSetChanged();
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
                        questionAdapter.getQuestionList().addAll(parseQuestionList(response));
                        questionAdapter.notifyDataSetChanged();
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


    // ========================== 分页的常见方法 ==========================

    private String getUrl(Integer pageNo) {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/question/listForMobile").buildUpon();
        builder.appendQueryParameter("pageNo", pageNo.toString());
        builder.appendQueryParameter("pageSize", PAGE_SIZE.toString());
        builder.appendQueryParameter("type",getType());
        return builder.toString();
    }

    private List<Question> parseQuestionList(JSONObject response) {

        JSONArray questionJSONArray = JSONUtils.getJSONArray(response, "content", null);
        List<Question> questionList = new ArrayList<>();
        for (int i = 0; i != questionJSONArray.length(); i++) {
            JSONObject questionJSONObject = JSONUtils.getJSONObject(questionJSONArray, i);
            String id = JSONUtils.getString(questionJSONObject, "id", "");
            String content = JSONUtils.getString(questionJSONObject, "content", "");
            String displayName = JSONUtils.getString(questionJSONObject, "displayName", "");
            Date eventTime = new Date(JSONUtils.getLong(questionJSONObject, "eventTime", 0));
            questionList.add(new Question(id, content, displayName, eventTime));
        }
        return questionList;
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

    private void startRefreshing() {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

}
