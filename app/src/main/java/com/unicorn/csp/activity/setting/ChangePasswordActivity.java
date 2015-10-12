package com.unicorn.csp.activity.setting;

import android.net.Uri;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivo.flatbutton.FlatButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;


// clear
public class ChangePasswordActivity extends ToolbarActivity {


    // ========================== views ==========================

    @Bind(R.id.et_old_password)
    MaterialEditText etOldPassword;

    @Bind(R.id.et_new_password)
    MaterialEditText etNewPassword;

    @Bind(R.id.et_confirm_password)
    MaterialEditText etConfirmPassword;

    @Bind(R.id.btn_modify)
    FlatButton btnModify;


    // ========================== dialog handler ==========================

    MaterialDialog dialog;


    // ========================== onCreate ==========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initToolbar("修改密码", true);
        enableSlidr();
    }


    // ========================== 确认密码 ==========================

    @OnClick(R.id.btn_modify)
    public void changePassword() {

        if (!getNewPassword().equals(getConfirmPassword())) {
            ToastUtils.show("确认密码有误");
            return;
        }

        dialog = showDialog();
        MyVolley.addRequest(new JsonObjectRequest(getUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        boolean result = JSONUtils.getBoolean(response, "result", false);
                        if (result) {
                            ToastUtils.show("修改成功");
                            finish();
                        } else {
                            ToastUtils.show(JSONUtils.getString(response,"errorMsg",""));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideDialog();
                        ToastUtils.show(VolleyErrorHelper.getErrorMessage(volleyError));
                    }
                }));
    }

    private MaterialDialog showDialog() {

        return new MaterialDialog.Builder(this)
                .title("处理中")
                .content("请稍后...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .show();
    }

    private String getUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/user/changePassword?").buildUpon();
        builder.appendQueryParameter("userId", ConfigUtils.getUserId());
        builder.appendQueryParameter("oldPassword", getOldPassword());
        builder.appendQueryParameter("newPassword", getNewPassword());
        return builder.toString();
    }

    private void hideDialog() {

        if (dialog != null) {
            dialog.dismiss();
        }
    }


    // ========================== 修改按钮状态 ==========================

    @OnTextChanged(R.id.et_old_password)
    public void onOldPasswordChange() {

        onPasswordChange();
    }

    @OnTextChanged(R.id.et_new_password)
    public void onNewPasswordChange() {

        onPasswordChange();
    }

    @OnTextChanged(R.id.et_confirm_password)
    public void onConfirmPasswordChange() {

        onPasswordChange();
    }

    private void onPasswordChange() {

        btnModify.setEnabled(!isOldPasswordEmpty() && !isNewPasswordEmpty() && !isConfirmPasswordEmpty());
    }


    // ========================== 其他方法 ==========================

    private boolean isOldPasswordEmpty() {

        return getOldPassword().equals("");
    }

    private boolean isNewPasswordEmpty() {

        return getNewPassword().equals("");
    }

    private boolean isConfirmPasswordEmpty() {

        return getConfirmPassword().equals("");
    }

    private String getOldPassword() {

        return etOldPassword.getText().toString().trim();
    }

    private String getNewPassword() {

        return etNewPassword.getText().toString().trim();
    }

    private String getConfirmPassword() {

        return etConfirmPassword.getText().toString().trim();
    }

}
