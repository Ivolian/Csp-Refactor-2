package com.unicorn.csp.activity.main;

import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ivo.flatbutton.FlatButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.greendao.Menu;
import com.unicorn.csp.other.TinyDB;
import com.unicorn.csp.utils.AppUtils;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


// clear
public class LoginActivity extends ToolbarActivity {


    // ========================== SF 常量 ==========================

    public static final String SF_USERNAME = "username";

    public static final String SF_PASSWORD = "password";

    public static final String SF_REMEMBER_ME = "rememberMe";


    // ========================== views ==========================

    @Bind(R.id.et_username)
    MaterialEditText etUsername;

    @Bind(R.id.et_password)
    MaterialEditText etPassword;

    @Bind(R.id.cb_remember_me)
    CheckBox cbRememberMe;

    @Bind(R.id.btn_login)
    FlatButton btnLogin;


    // ========================== dialog handler ==========================

    MaterialDialog loginDialog;


    // ========================== onCreate ==========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolbar("登录", false);
    }


    // ========================== 登录按钮状态 ==========================

    @OnTextChanged(R.id.et_username)
    public void onUsernameChange() {

        onUsernameOrPasswordChange();
    }

    @OnTextChanged(R.id.et_password)
    public void onPasswordChange() {

        onUsernameOrPasswordChange();
    }

    private void onUsernameOrPasswordChange() {

        btnLogin.setEnabled(!isUsernameEmpty() && !isPasswordEmpty());
    }


    // ========================== 登录 ==========================

    @OnClick(R.id.btn_login)
    public void login() {

        loginDialog = showLoginDialog();
        MyVolley.addRequest(new JsonObjectRequest(getLoginUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoginDialog();
                        boolean result = JSONUtils.getBoolean(response, "result", false);
                        if (result) {
                            final String userId = JSONUtils.getString(response, "userId", "");
                            final String courtId = JSONUtils.getString(response, "courtId", "");
                            String userTag = idToTag(userId);
                            String courtTag = idToTag(courtId);
                            registerTag(userTag, courtTag);

                            ConfigUtils.saveUserId(userId);
                            saveMenu(response);
                            storeLoginInfo();

                            startActivityAndFinish(MainActivity.class);
                        } else {
                            ToastUtils.show(JSONUtils.getString(response, "errorMsg", ""));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideLoginDialog();
                        ToastUtils.show(VolleyErrorHelper.getErrorMessage(volleyError));
                    }
                }));
    }

    private void registerTag(final String userTag, final String courtTag) {

        Set<String> tags = new HashSet<>();
        tags.add(userTag);
        tags.add(courtTag);
        JPushInterface.setTags(LoginActivity.this, tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i == 0)
                    updatePushTag(userTag + "," + courtTag);
            }
        });
    }

    private void updatePushTag(String pushTag) {

        MyVolley.addRequest(new StringRequest(getUpdatePushTagUrl(pushTag),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getUpdatePushTagUrl(String pushTag) {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/user/updatePushTag?").buildUpon();
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        builder.appendQueryParameter("pushTag", pushTag);
        return builder.toString();
    }

    private MaterialDialog showLoginDialog() {

        return new MaterialDialog.Builder(this)
                .title("登录中")
                .content("请稍后...")
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    private String getLoginUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/user/login?").buildUpon();
        builder.appendQueryParameter("username", getUsername());
        builder.appendQueryParameter("password", getPassword());
        builder.appendQueryParameter("currentVersionName", AppUtils.getVersionName());
        return builder.toString();
    }

    private void hideLoginDialog() {

        if (loginDialog != null) {
            loginDialog.dismiss();
        }
    }

    private String idToTag(String id) {

        return id.replace("-", "_");
    }


    // ========================== 保存菜单到数据库 ==========================

    private void saveMenu(JSONObject response) {

        MyApplication.getMenuDao().deleteAll();

        JSONObject rootMenuItem = JSONUtils.getJSONObject(response, "rootMenuItem", null);
        Menu rootMenu = itemToMenu(rootMenuItem);
        rootMenu.setParent(null);
        MyApplication.getMenuDao().insert(rootMenu);

        copeChildMenuItems(rootMenuItem, rootMenu);
    }

    private void copeChildMenuItems(JSONObject menuItem, Menu menu) {

        JSONArray items = JSONUtils.getJSONArray(menuItem, "items", null);
        if (items != null) {
            for (int i = 0; i != items.length(); i++) {
                JSONObject subItem = JSONUtils.getJSONObject(items, i);
                Menu subMenu = itemToMenu(subItem);
                subMenu.setParent(menu);
                MyApplication.getMenuDao().insert(subMenu);
                copeChildMenuItems(subItem, subMenu);
            }
        }
    }

    private Menu itemToMenu(JSONObject item) {

        Menu menu = new Menu();
        menu.setId(JSONUtils.getString(item, "id", ""));
        menu.setName(JSONUtils.getString(item, "name", ""));
        menu.setType(JSONUtils.getString(item, "type", ""));
        menu.setOrderNo(JSONUtils.getInt(item, "orderNo", 0));
        return menu;
    }


    // ========================== 保存登录信息 ==========================

    private void storeLoginInfo() {

        TinyDB tinyDB = new TinyDB(this);
        tinyDB.putString(SF_USERNAME, getUsername());
        tinyDB.putString(SF_PASSWORD, getPassword());
        tinyDB.putBoolean(SF_REMEMBER_ME, cbRememberMe.isChecked());
    }


    // ========================== 其他方法 ==========================

    private boolean isUsernameEmpty() {

        return getUsername().equals("");
    }

    private boolean isPasswordEmpty() {

        return getPassword().equals("");
    }

    private String getUsername() {

        return etUsername.getText().toString().trim();
    }

    private String getPassword() {

        return etPassword.getText().toString().trim();
    }

}
