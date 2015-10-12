package com.unicorn.csp.activity.question;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.f2prateek.dart.InjectExtra;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.adapter.recyclerView.question.QuestionDetailAdapter;
import com.unicorn.csp.adapter.recyclerView.question.QuestionDetailHeaderAdapter;
import com.unicorn.csp.model.Answer;
import com.unicorn.csp.model.Question;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class QuestionDetailActivity extends ToolbarActivity {


    @InjectExtra("question")
    Question question;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        initToolbar("问答详情", true);
        enableSlidr();

        initViews();
    }


    // ==================== answerAdapter ====================

    public QuestionDetailAdapter questionDetailAdapter;


    // ==================== page data ====================

    protected final Integer PAGE_SIZE = 10;

    Integer pageNo;

    boolean loadingMore;

    boolean lastPage;


    // ==================== initViews ====================

    private void initViews() {

        initRecyclerView();
        initFab();
        reload();
    }

    private void initRecyclerView() {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(questionDetailAdapter = new QuestionDetailAdapter());
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
        StickyHeadersItemDecoration stickyHeadersItemDecoration = new StickyHeadersBuilder()
                .setAdapter(questionDetailAdapter)
                .setRecyclerView(recyclerView)
                .setStickyHeadersAdapter(new QuestionDetailHeaderAdapter())
                .build();
        recyclerView.addItemDecoration(stickyHeadersItemDecoration);
    }

    private void initFab() {

        fab.setImageDrawable(getFabDrawable());
        fab.attachToRecyclerView(recyclerView);
    }

    private Drawable getFabDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_edit)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }

    // ==================== 发表评论点击事件 ====================

    @OnClick(R.id.fab)
    public void onFabClick() {

        startAddAnswerActivity();
    }

    private void startAddAnswerActivity() {

        Intent intent = new Intent(this, AddAnswerActivity.class);
        intent.putExtra("questionId", question.getId());
        startActivity(intent);
    }

    public void reload() {

        clearPageData();
        MyVolley.addRequest(new JsonObjectRequest(getUrl(pageNo),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // 将 Question 作为 Answer列表的第一个。
                        Answer answer = new Answer(question.getContent(), question.getDisplayName(), question.getEventTime());
                        List<Answer> answerList = new ArrayList<Answer>();
                        answerList.add(answer);
                        questionDetailAdapter.setAnswerList(answerList);
                        questionDetailAdapter.getAnswerList().addAll(parseAnswerList(response));
                        questionDetailAdapter.notifyDataSetChanged();
                        checkLastPage(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
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
                        questionDetailAdapter.getAnswerList().addAll(parseAnswerList(response));
                        questionDetailAdapter.notifyDataSetChanged();
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


    // ==================== onNewIntent ====================

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        reload();
    }

    // ========================== 通用分页方法 ==========================

    protected String getUrl(Integer pageNo) {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/answer/listForMobile?").buildUpon();
        builder.appendQueryParameter("pageNo", pageNo.toString());
        builder.appendQueryParameter("pageSize", PAGE_SIZE.toString());
        builder.appendQueryParameter("questionId", question.getId());
        return builder.toString();
    }

    private List<Answer> parseAnswerList(JSONObject response) {

        JSONArray contents = JSONUtils.getJSONArray(response, "content", null);
        List<Answer> answerList = new ArrayList<>();
        for (int i = 0; i != contents.length(); i++) {
            JSONObject answerJSONObject = JSONUtils.getJSONObject(contents, i);
            String content = JSONUtils.getString(answerJSONObject, "content", "");
            String displayName = JSONUtils.getString(answerJSONObject, "displayName", "");
            Date eventTime = new Date(JSONUtils.getLong(answerJSONObject, "eventTime", 0));
            answerList.add(new Answer( content, displayName, eventTime));
        }
        return answerList;
    }

    private void clearPageData() {

        pageNo = 1;
        lastPage = false;
    }

    private void checkLastPage(JSONObject response) {

        if (lastPage = isLastPage(response)) {
            ToastUtils.show(noData(response) ? "暂无回答" : "已加载全部数据");
        }
    }

    private boolean isLastPage(JSONObject response) {

        return JSONUtils.getBoolean(response, "lastPage", false);
    }

    private boolean noData(JSONObject response) {

        return JSONUtils.getInt(response, "totalPages", 0) == 0;
    }

}
