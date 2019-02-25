package com.hlibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


/**
 * Created by linwenhui on 2015/11/1.
 */
public class ProgressWebView extends RelativeLayout {
    private WebView webView;
    private ProgressBar progressBar;
    private WebSettings webSettings;
    private boolean isOverrideUrl;
    private IWebViewTitle webViewTitle;

    public ProgressWebView(Context context) {
        super(context);
        initview(context);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview(context);
    }

    public void initview(Context context) {
        webView = new WebView(context);
        progressBar = new ProgressBar(context);
        final int progressWidth = (int) (context.getResources().getDisplayMetrics().widthPixels*0.4);
        LayoutParams probarp = new LayoutParams(progressWidth, progressWidth);
        probarp.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(probarp);

        LayoutParams webviewp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(webviewp);
        addView(webView);
        addView(progressBar);
        setWebView();

    }

    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void setWebViewTitle(IWebViewTitle webViewTitle) {
        this.webViewTitle = webViewTitle;
    }

    private void setWebView() {

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isOverrideUrl) {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress * 100);
                if (newProgress > 80) {
                    progressBar.setVisibility(View.GONE);
                }
                if (newProgress == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (webViewTitle != null) {
                    webViewTitle.onTitle(title);
                }
            }
        });

        webSettings = webView.getSettings();
        //支持js脚本
        webSettings.setJavaScriptEnabled(true);
    }

    /**
     * 设置是否缩放，true为缩放
     */
    public void setwebSettingZoom(boolean istrue) {
        if (istrue) {
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        }
    }

    /**
     * 设置是否重定向，true为重定向
     */
    public void setOverrideUrl(boolean isOverrideurl) {
        this.isOverrideUrl = isOverrideurl;
    }

    public void goForward() {
        webView.goForward();
    }

    public void goBack() {
        webView.goBack();
    }

    public void reload() {
        webView.reload();
    }

    public interface IWebViewTitle {
        void onTitle(CharSequence title);
    }
}
