package com.unicorn.csp.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cundong.utils.PatchUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.unicorn.csp.MyApplication;
import com.unicorn.csp.volley.MyVolley;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;


public class UpdateUtils {


    // ============================ activity handler ==============================

    private static Activity activity;

    private static void init(Activity activity) {
        UpdateUtils.activity = activity;
    }

    private static void clear() {
        UpdateUtils.activity = null;
    }


    // =========================== public method ===============================

    private static String md5Sign;

    private static MaterialDialog downloadDialog;

    public static void checkUpdate(Activity activity) {
        init(activity);

        // 增量更新
        MyVolley.addRequest(new JsonObjectRequest(getCheckUpdateUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean needUpdate = JSONUtils.getBoolean(response, "needUpdate", false);
                        if (needUpdate) {
                            String diff = JSONUtils.getString(response, "diff", "");
                            md5Sign = JSONUtils.getString(response, "md5Sign", "");
                            showConfirmUpdateDialog(diff);
                        }
                    }
                },
                MyVolley.getDefaultErrorListener()));
    }

    // 增量更新


    private static String getCheckUpdateUrl() {
        Uri.Builder builder = Uri.parse(ConfigUtils.getBaseUrl() + "/api/v1/appDiff/checkUpdate?").buildUpon();
        builder.appendQueryParameter("clientVersionName", AppUtils.getVersionName());
        return builder.toString();
    }

    private static MaterialDialog showConfirmUpdateDialog(final String diff) {
        return new MaterialDialog.Builder(activity)
                .title("检测到新的版本，请更新。")
                .cancelable(false)
                .positiveText("确认更新")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        downloadDiff(diff);
                    }
                })
                .show();
    }

    private static void downloadDiff(String diff) {
        final String dialogTitle = "下载分差包中";
        downloadDialog = showDownloadDialog(dialogTitle);
        String url = ConfigUtils.getBaseUrl() + diff;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(MyApplication.getInstance()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                afterDiffDownloaded(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                downloadDialog.setTitle("下载分差包失败,请在关于中手动下载完整安装包");
                downloadDialog.setCancelable(true);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                if (downloadDialog.getTitleView().getText().toString().equals(dialogTitle)) {
                    double totalSizeMB = ((double) totalSize) / 1024 / 1024;
                    DecimalFormat decimalFormat = new java.text.DecimalFormat("#.##");
                    downloadDialog.setTitle("下载差分包中 (" + decimalFormat.format(totalSizeMB) + "MB)");
                }
                downloadDialog.setMaxProgress((int) totalSize);
                downloadDialog.setProgress((int) bytesWritten);
            }
        });
    }

    private static void afterDiffDownloaded(File response) {
        String diffPath = ConfigUtils.getDownloadDirPath() + "/diff.patch";
        File diff = new File(diffPath);
        if (diff.exists()) {
            diff.delete();
        }
        try {
            FileUtils.copyFile(response, diff);
        } catch (Exception e) {
            //
        }

        downloadDialog.setTitle("合并差分包中");
        System.loadLibrary("ApkPatchLibrary");
        int patchResult = PatchUtils.patch(ApkUtils.getSourceApkPath(activity, "com.unicorn.csp"), getApkPath(), diffPath);
        if (patchResult == 0) {
            downloadDialog.setTitle("合并完成");
            File apk = new File(getApkPath());
            String apkMd5Sign = MD5Utils.getMd5ByFile(apk);

            if (apkMd5Sign.equals(md5Sign)) {
                installApk(new File(getApkPath()));
            } else {
                downloadDialog.setTitle("MD5校验失败,请在关于中手动下载完整安装包");
                downloadDialog.setCancelable(true);
            }
        } else {
            downloadDialog.setTitle("合并失败,请在关于中手动下载完整安装包");
            downloadDialog.setCancelable(true);
        }
    }


    private static String getApkPath() {
        return ConfigUtils.getDownloadDirPath() + "/csp.apk";
    }


    // =========================== 低层的方法 ===============================

    private static MaterialDialog showDownloadDialog(String title) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .progress(false, 100)
                .cancelable(false)
                .show();
    }

    private static void installApk(File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        activity.startActivity(intent);
        clear();
    }


}
