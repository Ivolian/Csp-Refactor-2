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
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;

import org.json.JSONObject;
import org.maestrodroid.takeandcroplib.ImageSelectionHelper;
import org.maestrodroid.takeandcroplib.ImageSelectionListener;
import org.maestrodroid.takeandcroplib.crop.CropType;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class PersonalInfoActivity extends ToolbarActivity implements ImageSelectionListener {


    // ============================== extra ================================

    @InjectExtra("userId")
    String userId;


    // ============================== views 1 ================================

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

    @Bind(R.id.loading_container)
    FrameLayout flLoadingContainer;

    @Bind(R.id.content_container)
    ScrollView svContentContainer;

    @Bind(R.id.civ_profile)
    CircleImageView civProfile;

    @Bind(R.id.fab)
    FloatingActionButton fabUploadProfile;

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
        mImageSelectionHelper = new ImageSelectionHelper(this, this);

        fabUploadProfile.setImageDrawable(getUploadProfileDrawable());
        fetchPersonalInfo();
    }

    private Drawable getUploadProfileDrawable() {
        return new IconDrawable(this, Iconify.IconValue.zmdi_camera)
                .color(ColorOverrider.getInstance(this).getColorPrimary())
                .sizeDp(36);
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


    private ImageSelectionHelper mImageSelectionHelper;
    //

    @OnClick(R.id.fab)
    public void civProfileOnClick() {
        mImageSelectionHelper.startPhotoSelection(CropType.FREE);
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
            civProfile.setImageURI(Uri.parse(s));
        }
    }
}
