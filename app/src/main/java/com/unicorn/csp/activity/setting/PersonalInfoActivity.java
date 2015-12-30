package com.unicorn.csp.activity.setting;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.f2prateek.dart.InjectExtra;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;

import org.json.JSONObject;

import butterknife.Bind;


public class PersonalInfoActivity extends ToolbarActivity {


    // ============================== extra ================================

    @InjectExtra("userId")
    String userId;


    // ============================== views ================================

    @Bind(R.id.tv_cn_name)
    TextView tvCnName;

    @Bind(R.id.tv_username)
    TextView tvUsername;

    @Bind(R.id.tv_court)
    TextView tvCourt;

    @Bind(R.id.tv_department)
    TextView tvDepartment;

    @Bind(R.id.tv_position)
    TextView tvPosition;

    @Bind(R.id.tv_telephone)
    TextView tvTelephone;

    @Bind(R.id.tv_qq)
    TextView tvQq;

    @Bind(R.id.tv_email)
    TextView tvEmail;

    @Bind(R.id.loading_container)
    FrameLayout flLoadingContainer;

    @Bind(R.id.content_container)
    ScrollView svContentContainer;


    // ============================== onCreate ================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initToolbar("个人信息", true);
        initViews();
        enableSlidr();
    }

    private void initViews() {
        fetchPersonalInfo();
    }


    // ============================== fetchPersonalInfo ================================

    private void fetchPersonalInfo() {
        Request<JSONObject> jsonObjectRequest = new JsonObjectRequest(
                getUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoadingView();
                        copeResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingView();
                        ToastUtils.show(VolleyErrorHelper.getErrorMessage(error));
                    }
                }
        );
        MyVolley.addRequest(jsonObjectRequest);
    }

    private String getUrl() {
        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/user/personalInfo?").buildUpon();
        builder.appendQueryParameter("userId", userId);
        return builder.toString();
    }

    private void hideLoadingView() {
        flLoadingContainer.setVisibility(View.INVISIBLE);
        svContentContainer.setVisibility(View.VISIBLE);
    }

    private void copeResponse(JSONObject response) {
        tvCnName.setText(JSONUtils.getString(response, "cnName", ""));
        tvUsername.setText(JSONUtils.getString(response, "username", ""));
        tvCourt.setText(JSONUtils.getString(response, "courtName", ""));
        tvDepartment.setText(JSONUtils.getString(response, "departmentName", ""));
        tvPosition.setText(JSONUtils.getString(response, "position", ""));
        tvTelephone.setText(JSONUtils.getString(response, "telephone", ""));
        tvQq.setText(JSONUtils.getString(response, "qq", ""));
        tvEmail.setText(JSONUtils.getString(response, "email", ""));
    }

}
