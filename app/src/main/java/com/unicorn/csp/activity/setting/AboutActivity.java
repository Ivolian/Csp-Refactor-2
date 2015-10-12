package com.unicorn.csp.activity.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ToolbarActivity;
import com.unicorn.csp.utils.AppUtils;
import com.unicorn.csp.utils.ConfigUtils;
import com.unicorn.csp.utils.JSONUtils;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.MyVolley;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;

public class AboutActivity extends ToolbarActivity {

    @Bind(R.id.tv_version_name)
    TextView tvVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initToolbar("关于", true);
        enableSlidr();

        initViews();
        checkUpdate();
    }

    private void initViews(){

        tvVersionName.setText(AppUtils.getVersionName());
    }


    // ========================== 检查更新 ==========================

    private void checkUpdate() {

        MyVolley.addRequest(new JsonObjectRequest(getCheckUpdateUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean needUpdate = JSONUtils.getBoolean(response, "needUpdate", false);
                        if (needUpdate) {
                            String apk = JSONUtils.getString(response, "apk", "");
                            showConfirmUpdateDialog(apk);
                        }else {
                            ToastUtils.show("已经是最新版本");
                        }
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    private String getCheckUpdateUrl() {

        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/app/checkUpdate?").buildUpon();
        builder.appendQueryParameter("versionName", AppUtils.getVersionName());
        return builder.toString();
    }

    private MaterialDialog showConfirmUpdateDialog(final String apk) {

        // todo 添加更新细节
        return new MaterialDialog.Builder(this)
                .title("检测到新版本，是否立即更新？")
                .cancelable(false)
                .positiveText("立即更新")
                .negativeText("下次再说")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        downloadApk(apk);
                    }
                })
                .show();
    }

    private void downloadApk(String apk) {

        final MaterialDialog downloadDialog = showDownloadApkDialog();
        String url = ConfigUtils.getBaseUrl() + apk;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(MyApplication.getInstance()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {

                downloadDialog.dismiss();
                installApk(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                downloadDialog.dismiss();
                ToastUtils.show("下载失败");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

                downloadDialog.setMaxProgress((int) totalSize);
                downloadDialog.setProgress((int) bytesWritten);
            }
        });
    }

    private MaterialDialog showDownloadApkDialog() {

        return new MaterialDialog.Builder(this)
                .title("下载APK中")
                .progress(false, 100)
                .cancelable(false)
                .show();
    }

    private void installApk(File response) {

        String apkPath = ConfigUtils.getDownloadDirPath() + "/csp.apk";
        File apk = new File(apkPath);
        if (apk.exists()) {
            apk.delete();
        }
        try {
            FileUtils.copyFile(response, apk);
        } catch (Exception e) {
            //
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        startActivity(intent);
    }

}

