package com.unicorn.csp.activity.main;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.activity.news.FavoriteNewsActivity;
import com.unicorn.csp.activity.question.AddQuestionActivity;
import com.unicorn.csp.activity.search.BookSearchActivity;
import com.unicorn.csp.activity.search.NewsSearchActivity;
import com.unicorn.csp.activity.setting.PersonalInfoActivity;
import com.unicorn.csp.activity.setting.SettingActivity;
import com.unicorn.csp.adapter.viewpager.ViewPagerAdapter;
import com.unicorn.csp.greendao.Menu;
import com.unicorn.csp.greendao.MenuDao;
import com.unicorn.csp.home.HomeActivity;
import com.unicorn.csp.model.UserInfo;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.other.greenmatter.SelectColorActivity;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.utils.UpdateUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.NetworkCircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class MainActivity extends ToolbarActivity {

    // todo 用更酷的方法来完成
    // ========================== 保存选中项 ==========================

    final String SELECTED = "selected";

    int selected = -1;

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED, selected);
    }


    // ========================== onCreate ==========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar("", false);
        initViews();

        if (savedInstanceState == null) {
            selectBottomTab(0, true);
            startActivity(HomeActivity.class);
        } else {
            selectBottomTab(savedInstanceState.getInt(SELECTED), false);
        }

        UpdateUtils.checkUpdate(this);
//        useHandler();
    }


      Handler mHandler;
    public void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 5000);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {


            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    getUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    },
                    MyVolley.getDefaultErrorListener()
            );
            MyVolley.addRequest(stringRequest);    Log.e("Handlers", "Calls");
            /** Do something **/
            mHandler.postDelayed(mRunnable, 5000);
        }
    };
    private String getUrl() {
        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/user/heartbeat?").buildUpon();
        builder.appendQueryParameter("userId",ConfigUtils.getUserId());
        return builder.toString();
    }
    private String getCurrentApkPath() {
        return ConfigUtils.getDownloadDirPath() + "/app-1.1.apk";
    }


    private void initViews() {

        initDrawer();
        initBottomTabList();
    }


    // ========================== 侧滑菜单 ==========================

    Drawer drawer;

    private void initDrawer() {

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withToolbar(getToolbar())
                .withActionBarDrawerToggleAnimated(true)
                .withHeader(R.layout.drawer_header)
                .withHeaderDivider(true)
                .withCloseOnClick(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("首页").withIcon(FontAwesome.Icon.faw_home).withIdentifier(0).withCheckable(false),
                        new PrimaryDrawerItem().withName("我的关注").withIcon(FontAwesome.Icon.faw_star).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("互动专区").withIcon(FontAwesome.Icon.faw_users).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName("主题色彩").withIcon(FontAwesome.Icon.faw_paint_brush).withIdentifier(3).withCheckable(false),
                        new PrimaryDrawerItem().withName("用户登出").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(4).withCheckable(false),
                        new PrimaryDrawerItem().withName("更多设置").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(5).withCheckable(false)
//                        new SwitchDrawerItem().withName("隐藏标题栏").withChecked(false).withOnCheckedChangeListener(new OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {
//                                toggleToolbar();
//                            }
//                        })
                )
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case 0:
                                startActivity(HomeActivity.class);
                                break;
                            case 1:
                                startActivity(FavoriteNewsActivity.class);
//                                startActivity(DbInspectorActivity.class);
                                break;
                            case 2:
                                showChoiceDialog();
//                                startActivity(AddQuestionActivity.class);
                                break;
                            case 3:
                                startSelectColorActivity();
                                break;
                            case 4:
                                showSignOutDialog();
                                break;
                            case 5:
                                startActivity(SettingActivity.class);
                        }
                        return false;
                    }
                }).build();

        initDrawerHeader(drawer.getHeader());
    }

    private void initDrawerHeader(View header) {

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        TextView tvThumbCount = (TextView) header.findViewById(R.id.tv_thumb_count);
        tvThumbCount.setText("本月点赞: " + userInfo.getThumbCount());
        TextView tvFavoriteNewsCount = (TextView) header.findViewById(R.id.tv_favorite_news_count);
        tvFavoriteNewsCount.setText("本月关注: " + userInfo.getFavoriteNewsCount());
        TextView tvCommentCount = (TextView) header.findViewById(R.id.tv_comment_count);
        tvCommentCount.setText("本月评论: " + userInfo.getCommentCount());
        TextView tvReadTimes = (TextView) header.findViewById(R.id.tv_read_times);
        tvReadTimes.setText("本月阅读: " + userInfo.getReadTimes());
        TextView tvLoginTimes = (TextView) header.findViewById(R.id.tv_login_times);
        tvLoginTimes.setText("本月登录: " + userInfo.getLoginTimes());

        NetworkCircleImageView ncivAvatar = (NetworkCircleImageView) header.findViewById(R.id.nciv_avatar);
        ncivAvatar.setErrorImageResId(R.drawable.profile);
        ncivAvatar.setDefaultImageResId(R.drawable.profile);
        String avatar = getIntent().getStringExtra("avatar");
        ncivAvatar.setImageUrl(ConfigUtils.getBaseUrl() + avatar, MyVolley.getImageLoader());
        ncivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                intent.putExtra("userId", ConfigUtils.getUserId());
                startActivity(intent);
            }
        });
    }

    private void showChoiceDialog() {

        new MaterialDialog.Builder(this)
                .title("互动专区")
                .items(new String[]{"学习交流", "建议意见"})
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        startAddQuestionActivity(text.toString());
                        return true;
                    }
                })
                .positiveText("确定")
                .show();
    }

    private void startAddQuestionActivity(String type) {

        Intent intent = new Intent(this, AddQuestionActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }


    // ========================== 用户登出 ==========================

    private MaterialDialog showSignOutDialog() {

        return new MaterialDialog.Builder(this)
                .title("确认登出？")
                .positiveText("确认")
                .negativeText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        mHandler.removeCallbacks(mRunnable);
                        startActivityAndFinish(LoginActivity.class);
                    }
                })
                .show();
    }


    // ========================== 再按一次退出 ==========================

    long exitTime = 0;

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.show("再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
        Log.e("result","Main Activity is destory");
    }

    // ========================== 主题色彩 ==========================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == SelectColorActivity.SELECT_COLOR_SUCCESS) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startSelectColorActivity() {

        Intent intent = new Intent(this, SelectColorActivity.class);
        startActivityForResult(intent, 2333);
    }


    // ========================== 底部栏 ==========================

    // todo replace bookcity
    @Bind({R.id.llHotSpot, R.id.llStudyField, R.id.llSocialRegion, R.id.llMyStudy, R.id.llBookCity})
    List<LinearLayout> bottomTabList;

    private void initBottomTabList() {

        for (LinearLayout bottomTab : bottomTabList) {
            changeBottomTabColor(bottomTab, getResources().getColor(R.color.tab_unselected));
        }
    }

    @OnClick({R.id.llHotSpot, R.id.llStudyField, R.id.llSocialRegion, R.id.llMyStudy, R.id.llBookCity})
    public void onBottomTabClick(LinearLayout bottomTab) {
        selectBottomTab(bottomTabList.indexOf(bottomTab), true);
    }

    private void selectBottomTab(int index, boolean replaceFragment) {

        // 如果已经选中
        if (index == selected) {
            return;
        }
        changeBottomTabColor(bottomTabList.get(index), ColorOverrider.getInstance(this).getColorAccent());
        if (selected != -1) {
            changeBottomTabColor(bottomTabList.get(selected), getResources().getColor(R.color.tab_unselected));
        }
        changeToolbarTitle(index);
        if (replaceFragment) {
            replaceFragment(index);
        }
        selected = index;
    }

    private void changeBottomTabColor(LinearLayout bottomTab, int color) {

        ((ImageView) bottomTab.getChildAt(0)).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        ((TextView) bottomTab.getChildAt(1)).setTextColor(color);
    }

    private void changeToolbarTitle(int index) {

        switch (index) {
            case 0:
                setToolbarTitle(R.string.hot_spot);
                break;
            case 1:
                setToolbarTitle(R.string.study_field);
                break;
            case 2:
                setToolbarTitle(R.string.social_region);
                break;
            case 3:
                setToolbarTitle(R.string.my_study);
                break;
            case 4:
                setToolbarTitle(R.string.book_city);
                break;
        }
    }

    private void replaceFragment(int index) {

        String[] names = {"资讯热点", "学习园地", "互动专区", "我的学习", "学术交流"};
        Menu menu = findMenuByName(names[index]);
        Fragment fragment = ViewPagerAdapter.getFragmentByMenu(menu, true);
        replaceFragment_(fragment);
    }

    private Menu findMenuByName(String name) {

        List<Menu> menuList = MyApplication.getMenuDao().queryBuilder()
                .where(MenuDao.Properties.Name.eq(name))
                .list();
        return menuList.get(0);
    }

    private void replaceFragment_(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }


    // ========================== 查询 ==========================

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.search).setIcon(getActionDrawable());
        return super.onCreateOptionsMenu(menu);
    }

    private Drawable getActionDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_search)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search_news:
                startActivity(NewsSearchActivity.class);
                return true;
            case R.id.search_book:
                startActivity(BookSearchActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
