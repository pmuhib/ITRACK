package com.client.itrack.activities;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.client.itrack.R;

public class BrowserActivity extends AppCompatActivity {

    ProgressBar progressBar;
    WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        Intent  intent= getIntent();
        String url   = intent.getStringExtra("url");
        String title   = intent.getStringExtra("title");
        progressBar = (ProgressBar) findViewById(R.id.progressBar) ;
        if(url!=null && !url.isEmpty())
        {
             browser = (WebView) findViewById(R.id.browser);
            WebSettings webSettings = browser.getSettings();
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setSupportZoom(true);
            webSettings.setJavaScriptEnabled(true);
            browser.setWebViewClient(webViewClient);
            browser.loadUrl(url);
        }

        final Toolbar browserToolbar = (Toolbar) findViewById(R.id.browserToolbar);
        final ImageView btnCloseBrowser = (ImageView) browserToolbar.findViewById(R.id.btnCloseBrowser);
        btnCloseBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final TextView titleText  = (TextView) browserToolbar.findViewById(R.id.tvTitle) ;
        titleText.setText(title);

    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.loadUrl(request.getUrl().getEncodedPath());
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if(browser.canGoBack())
        {
            browser.goBack();
            return  ;
        }
        super.onBackPressed();
    }
}


