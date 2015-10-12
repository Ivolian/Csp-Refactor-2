package com.unicorn.csp.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.unicorn.csp.greendao.Menu;
import com.unicorn.csp.greendao.SearchHistory;

import com.unicorn.csp.greendao.MenuDao;
import com.unicorn.csp.greendao.SearchHistoryDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig menuDaoConfig;
    private final DaoConfig searchHistoryDaoConfig;

    private final MenuDao menuDao;
    private final SearchHistoryDao searchHistoryDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        menuDaoConfig = daoConfigMap.get(MenuDao.class).clone();
        menuDaoConfig.initIdentityScope(type);

        searchHistoryDaoConfig = daoConfigMap.get(SearchHistoryDao.class).clone();
        searchHistoryDaoConfig.initIdentityScope(type);

        menuDao = new MenuDao(menuDaoConfig, this);
        searchHistoryDao = new SearchHistoryDao(searchHistoryDaoConfig, this);

        registerDao(Menu.class, menuDao);
        registerDao(SearchHistory.class, searchHistoryDao);
    }
    
    public void clear() {
        menuDaoConfig.getIdentityScope().clear();
        searchHistoryDaoConfig.getIdentityScope().clear();
    }

    public MenuDao getMenuDao() {
        return menuDao;
    }

    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

}
