package com.unicorn.csp.activity.news;

import android.os.Bundle;

import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.fragment.news.FavoriteNewsFragment;


// clear
public class FavoriteNewsActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_news);
        initToolbar("我的关注", true);
        enableSlidr();

        replaceFragment();
    }

    private void replaceFragment() {

        FavoriteNewsFragment favoriteNewsFragment = new FavoriteNewsFragment();
        favoriteNewsFragment.initPrepare();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, favoriteNewsFragment).commit();
    }

}
