package com.pingan_us.pingan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class WebActivity extends AppCompatActivity implements View.OnClickListener{

    WebView wv;
    ProgressBar progressBar;
    ImageButton closeBtn, undoBtn, redoBtn;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        wv = (WebView) findViewById(R.id.webview);

        Intent intent = getIntent();

        url = intent.getStringExtra("url");

        closeBtn = (ImageButton) findViewById(R.id.web_close_btn);
        undoBtn = (ImageButton) findViewById(R.id.btnprev);
        redoBtn = (ImageButton) findViewById(R.id.btnnext);

        progressBar = (ProgressBar) findViewById(R.id.web_progressBar);

        wv.setWebViewClient(new myWebClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);
        wv.loadUrl(url);

        closeBtn.setOnClickListener(this);
        undoBtn.setOnClickListener(this);
        redoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_close_btn:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btnprev:
                if(wv.canGoBack())
                    wv.goBack();
                break;
            case R.id.btnnext:
                if(wv.canGoForward())
                    wv.goForward();
                break;
        }
    }

    public  class myWebClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

    }


}
