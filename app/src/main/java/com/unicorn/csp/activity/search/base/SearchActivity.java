package com.unicorn.csp.activity.search.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ButterKnifeActivity;
import com.unicorn.csp.fragment.BookFragment;
import com.unicorn.csp.fragment.news.NewsFragment;
import com.unicorn.csp.fragment.base.LazyLoadFragment;
import com.unicorn.csp.greendao.SearchHistory;
import com.unicorn.csp.greendao.SearchHistoryDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.Bind;


// clear
public abstract class SearchActivity extends ButterKnifeActivity implements SearchBox.SearchListener {


    // ================================ 抽象方法 ================================

    // return book or news
    abstract protected String getType();


    // ================================ views ================================

    @Bind(R.id.searchbox)
    SearchBox searchBox;

    LazyLoadFragment fragment;


    // ================================ 关键词队列，用于维护用户查询历史 ================================

    Queue<String> keywordQueue = new LinkedList<>();


    // ================================ onCreate ================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
    }

    private void initViews() {

        initSearchBox();
        initFragment();
    }


    // ================================ initSearchBox ================================

    private void initSearchBox() {

        searchBox.enableVoiceRecognition(this);
        searchBox.setSearchListener(this);
        searchBox.setLogoText("请输入查询内容");
        initTitleQueue();
        refreshSearchBox();
    }

    private void initTitleQueue() {

        for (SearchHistory searchHistory : getBookSearchHistory()) {
            keywordQueue.add(searchHistory.getKeyword());
        }
    }

    private List<SearchHistory> getBookSearchHistory() {

        return MyApplication.getSearchHistoryDao().queryBuilder().where(SearchHistoryDao.Properties.Type.eq(getType())).list();
    }

    private void refreshSearchBox() {

        ArrayList<SearchResult> searchResultList = new ArrayList<>();
        for (String keyword : keywordQueue) {
            searchResultList.add(new SearchResult(keyword, getHistoryDrawable()));
        }
        Collections.reverse(searchResultList);
        searchBox.setSearchables(searchResultList);
    }

    private Drawable getHistoryDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_time)
                .colorRes(android.R.color.darker_gray)
                .actionBarSize();
    }


    // ================================ initFragment ================================

    private void initFragment() {

        fragment = createFragment();
        // 不知道为什么，这里没有调用 setUserVisibleHint 方法，所以手动调用 initPrepare 方法
        // todo 有空可以研究一下
        fragment.initPrepare();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private LazyLoadFragment createFragment() {

        return getType().equals("news") ? new NewsFragment() : new BookFragment();
    }


    // ================================ 语音识别 ================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // 过滤标点符号，语音识别会自动添加标点符号，太智能了...
            matches.set(0, matches.get(0).replaceAll("\\p{P}", ""));
            searchBox.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // ================================ 查询 ================================

    private void search(String keyword) {

        searchByKeyword(keyword);
        addKeywordToQueue(keyword);
        refreshSearchBox();
    }

    private void addKeywordToQueue(String keyword) {

        if (keywordQueue.contains(keyword)) {
            keywordQueue.remove(keyword);
        }
        keywordQueue.add(keyword);
        if (keywordQueue.size() == 6) {
            keywordQueue.remove();
        }
    }

    private void searchByKeyword(String keyword) {

        fragment.getArguments().putString("keyword", keyword);
        fragment.reload();
    }


    // ================================ 保存查询历史 ================================

    @Override
    protected void onDestroy() {

        saveSearchHistory();
        super.onDestroy();
    }

    private void saveSearchHistory() {

        MyApplication.getSearchHistoryDao().deleteInTx(getBookSearchHistory());
        List<SearchHistory> searchHistoryList = new ArrayList<>();
        for (String keyword : keywordQueue) {
            searchHistoryList.add(new SearchHistory(keyword, getType()));
        }
        MyApplication.getSearchHistoryDao().insertInTx(searchHistoryList);
    }


    // ================================ 查询事件 ================================

    @Override
    public void onSearch(String keyword) {

        search(keyword);
    }

    @Override
    public void onSearchOpened() {

    }

    @Override
    public void onSearchCleared() {

    }

    @Override
    public void onSearchClosed() {

    }

    @Override
    public void onSearchTermChanged() {

    }

}
