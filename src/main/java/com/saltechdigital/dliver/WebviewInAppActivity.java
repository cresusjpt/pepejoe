package com.saltechdigital.dliver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.saltechdigital.dliver.utils.Config;

public class WebviewInAppActivity extends AppCompatActivity {

    private WebView webView;
    private WebSettings webSettings;
    private Toolbar toolbar;
    private Intent intent;
    private String urltoload;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_in_app);
        intent = getIntent();
        urltoload = intent.getStringExtra(Config.INTENT_EXTRA_URL);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_close_24);

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);

        webView.loadUrl(urltoload);
        webView.setWebViewClient(new WebViewClientInApp());
        webView.setWebChromeClient(new WebChromeClientInApp());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public class WebChromeClientInApp extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
            if (progressBar.getProgress() == 100) {
                progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    public class WebViewClientInApp extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            //progressDialog = ProgressDialog.show(WebviewInAppActivity.this,"",getString(R.string.loading),true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            //progressDialog.dismiss();
            toolbar.setTitle(webView.getTitle());
            toolbar.setSubtitle(webView.getUrl());
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Toast.makeText(WebviewInAppActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
            super.onReceivedError(view, request, error);
        }
    }
}
