package com.unicorn.csp.activity.search;

import com.quinny898.library.persistentsearch.SearchBox;
import com.unicorn.csp.activity.search.base.SearchActivity;


// clear
public class NewsSearchActivity extends SearchActivity implements SearchBox.SearchListener {

    @Override
    protected String getType() {

        return "news";
    }

}
