package com.unicorn.csp.activity.bookCity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.f2prateek.dart.InjectExtra;
import com.unicorn.csp.R;
import com.unicorn.csp.activity.base.ButterKnifeActivity;

import butterknife.Bind;


// clear
public class WebViewActivity extends ButterKnifeActivity {


    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.webview)
    WebView webView;

    @InjectExtra("url")
    String url;


    // ================ onCreate ================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initWebView();
    }

    private void initWebView() {

        initWebViewSetting();
        setUpWebViewClient();
        setUpWebChromeClient();
        webView.loadUrl(url);
    }

    private void initWebViewSetting() {

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
    }

    private void setUpWebViewClient() {

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        };
        webView.setWebViewClient(webViewClient);
    }

    private void setUpWebChromeClient() {

        WebChromeClient webChromeClient = new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
            }
        };
        webView.setWebChromeClient(webChromeClient);
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
