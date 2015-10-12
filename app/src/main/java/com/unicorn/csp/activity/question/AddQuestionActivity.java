package com.unicorn.csp.activity.question;

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


public class AddQuestionActivity extends ToolbarActivity {


    // ==================== view ====================

    @Bind(R.id.et_content)
    EditText etContent;

    @InjectExtra("type")
    String type;

    // ==================== onCreate ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        initToolbar("我要提问", true);
        enableSlidr();
    }


    // ====================== toolbar 发送按钮 ======================e

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_question:
                addQuestion();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_question, menu);
        menu.findItem(R.id.add_question).setIcon(getActionDrawable());
        return super.onCreateOptionsMenu(menu);
    }

    private Drawable getActionDrawable() {

        return new IconDrawable(this, Iconify.IconValue.zmdi_mail_send)
                .colorRes(android.R.color.white)
                .actionBarSize();
    }


    // ==================== 提问 ====================

    private void addQuestion() {

        if (getQuestion().equals("")) {
            ToastUtils.show("问题不能为空");
            return;
        }
        MyVolley.addRequest(new JsonObjectRequest(getUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean result = JSONUtils.getBoolean(response, "result", false);
                        if (result) {
                            ToastUtils.show("提问成功");
                            finish();
                        } else {
                            ToastUtils.show("提问失败");
                        }
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/question/create?").buildUpon();
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        builder.appendQueryParameter("type", type);
        builder.appendQueryParameter("content", getQuestion());
        return builder.toString();
    }

    private String getQuestion() {

        return etContent.getText().toString().trim();
    }

}
