package com.unicorn.csp.activity.news;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.f2prateek.dart.InjectExtra;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;

import org.json.JSONObject;

import butterknife.Bind;


// clear
public class AddBookCommentActivity extends ToolbarActivity {


    // ==================== 必要参数，新闻 Id ====================

    @InjectExtra("bookId")
    String bookId;


    // ==================== view ====================

    @Bind(R.id.et_content)
    EditText etContent;


    // ==================== onCreate ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        initToolbar("发表评论", true);
        enableSlidr();
    }


    // ====================== toolbar 发送按钮 ======================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_comment:
                addComment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_comment, menu);
        menu.findItem(R.id.add_comment).setIcon(getActionDrawable());
        return super.onCreateOptionsMenu(menu);
    }

    private Drawable getActionDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_mail_send)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }


    // ==================== 发送评论 ====================

    private void addComment() {

        if (getContent().equals("")) {
            ToastUtils.show("评论不能为空");
            return;
        }
        MyVolley.addRequest(new JsonObjectRequest(getUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean result = JSONUtils.getBoolean(response, "result", false);
                        if (result) {
                            ToastUtils.show("发表评论成功");
                            startCommentActivityAndFinish();
                        } else {
                            ToastUtils.show(JSONUtils.getString(response, "errorMsg", ""));
                        }
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getContent() {

        return etContent.getText().toString().trim();
    }

    private String getUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/bookComment/create?").buildUpon();
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        builder.appendQueryParameter("bookId", bookId);
        builder.appendQueryParameter("content", getContent());
        return builder.toString();
    }


    // ====================  有两种可能: 1.打开评论界面 2.回到评论界面 ====================

    private void startCommentActivityAndFinish() {

        Intent intent = new Intent(this, BookCommentActivity.class);
        intent.putExtra("bookId", bookId);
        startActivity(intent);
        finish();
    }

}
