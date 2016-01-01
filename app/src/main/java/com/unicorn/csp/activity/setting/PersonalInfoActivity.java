package com.unicorn.csp.activity.setting;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.f2prateek.dart.InjectExtra;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.other.greenmatter.ColorOverrider;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.NetworkCircleImageView;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;

import org.apache.http.Header;
import org.json.JSONObject;
import org.maestrodroid.takeandcroplib.ImageSelectionHelper;
import org.maestrodroid.takeandcroplib.ImageSelectionListener;
import org.maestrodroid.takeandcroplib.crop.CropType;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;


public class PersonalInfoActivity extends ToolbarActivity implements ImageSelectionListener {


    // ============================== extra ================================

    @InjectExtra("userId")
    String userId;


    // ============================== views 1 ================================

    @Bind(R.id.nciv_avatar)
    NetworkCircleImageView ncivAvatar;

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


    // ============================== views 2 ================================

    @Bind(R.id.content_container)
    ScrollView svContentContainer;

    @Bind(R.id.loading_container)
    FrameLayout flLoadingContainer;

    @Bind(R.id.fab)
    FloatingActionButton fabTakePhoto;


    // ============================== onCreate ================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initToolbar("个人信息", true);
        initViews();
        enableSlidr();
    }

    ImageSelectionHelper mImageSelectionHelper;

    private void initViews() {
        fabTakePhoto.setImageDrawable(getTakePhotoDrawable());
        mImageSelectionHelper = new ImageSelectionHelper(this, this);
        fetchPersonalInfo();
    }

    private Drawable getTakePhotoDrawable() {
        return new IconDrawable(this, Iconify.IconValue.zmdi_camera)
                .color(ColorOverrider.getInstance(this).getColorPrimary())
                .sizeDp(36);
    }


    // ============================== 获取用户信息 ================================

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

    private void copeResponse(JSONObject response) {
        String avatar = JSONUtils.getString(response, "avatar", "");
        ToastUtils.show(avatar);
        ncivAvatar.setImageUrl(ConfigUtils.getBaseUrl() + avatar, MyVolley.getImageLoader());
        ncivAvatar.setDefaultImageResId(R.drawable.profile);
        tvCnName.setText(JSONUtils.getString(response, "cnName", ""));
        tvUsername.setText(JSONUtils.getString(response, "username", ""));
        tvCourt.setText(JSONUtils.getString(response, "courtName", ""));
        tvDepartment.setText(JSONUtils.getString(response, "departmentName", ""));
        tvPosition.setText(JSONUtils.getString(response, "position", ""));
        tvTelephone.setText(JSONUtils.getString(response, "telephone", ""));
        tvQq.setText(JSONUtils.getString(response, "qq", ""));
        tvEmail.setText(JSONUtils.getString(response, "email", ""));
    }

    private void hideLoadingView() {
        flLoadingContainer.setVisibility(View.INVISIBLE);
        svContentContainer.setVisibility(View.VISIBLE);
    }


    // ============================== 选择和剪裁头像 ================================

    @OnClick(R.id.fab)
    public void civProfileOnClick() {
        mImageSelectionHelper.startPhotoSelection(CropType.SQUARE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mImageSelectionHelper != null) {
            mImageSelectionHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onImageSelected(String s) {
        if (!TextUtils.isEmpty(s)) {
            ncivAvatar.setImageURI(Uri.parse(s));
            uploadAvatar(s);
        }
    }


    private void uploadAvatar(String avatarPath) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = ConfigUtils.getBaseUrl() + "/api/v1/user/uploadAvatar";
        File myFile = new File(avatarPath);

        RequestParams params = new RequestParams();

        try {
            params.put("Avatar", myFile);
            params.put("userId", ConfigUtils.getUserId());
        } catch (Exception e) {
            //
        }
        client.post(
                url,
                params,
                new AsyncHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        ToastUtils.show(new String(response));

                        // called when response HTTP status is "200 OK"
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        ToastUtils.show(e.getMessage());
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }

                });
    }

}
