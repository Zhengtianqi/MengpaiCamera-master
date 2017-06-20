package com.sobot.chat.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends SobotBaseActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private RelativeLayout sobot_rl_net_error;
    private Button sobot_btn_reconnect;
    @SuppressWarnings("unused")
    private TextView sobot_txt_loading;
    private String mUrl = "";
    private LinearLayout sobot_webview_toolsbar;
    private ImageView sobot_webview_goback;
    private ImageView sobot_webview_forward;
    private ImageView sobot_webview_reload;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(this, "layout",
                "sobot_activity_webview"));
        String bg_color = SharedPreferencesUtil.getStringData(this, "robot_current_themeColor", "");
        if (bg_color != null && bg_color.trim().length() != 0) {
            relative.setBackgroundColor(Color.parseColor(bg_color));
        }

        int robot_current_themeImg = SharedPreferencesUtil.getIntData(this, "robot_current_themeImg", 0);
        if (robot_current_themeImg != 0) {
            relative.setBackgroundResource(robot_current_themeImg);
        }
        Drawable drawable = getResources().getDrawable(ResourceUtils.getIdByName(this, "drawable", "sobot_btn_back_selector"));
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        sobot_tv_left.setCompoundDrawables(drawable, null, null, null);
        sobot_tv_left.setText(getResString("sobot_back"));
        mWebView = (WebView) findViewById(getResId("sobot_mWebView"));
        mProgressBar = (ProgressBar) findViewById(getResId("sobot_loadProgress"));
        sobot_rl_net_error = (RelativeLayout) findViewById(getResId("sobot_rl_net_error"));
        sobot_webview_toolsbar = (LinearLayout) findViewById(getResId("sobot_webview_toolsbar"));
        sobot_btn_reconnect = (Button) findViewById(getResId("sobot_btn_reconnect"));
        sobot_btn_reconnect.setOnClickListener(this);
        sobot_txt_loading = (TextView) findViewById(getResId("sobot_txt_loading"));
        sobot_webview_goback = (ImageView) findViewById(getResId("sobot_webview_goback"));
        sobot_webview_forward = (ImageView) findViewById(getResId("sobot_webview_forward"));
        sobot_webview_reload = (ImageView) findViewById(getResId("sobot_webview_reload"));
        sobot_webview_goback.setOnClickListener(this);
        sobot_webview_forward.setOnClickListener(this);
        sobot_webview_reload.setOnClickListener(this);
        sobot_webview_goback.setEnabled(false);
        sobot_webview_forward.setEnabled(false);
        sobot_tv_left.setOnClickListener(this);
        setTitle("");
        setShowNetRemind(false);
        resetViewDisplay();
        initWebView();
        initBundleData(savedInstanceState);
        mWebView.loadUrl(mUrl);
        LogUtils.i("webViewActivity---" + mUrl);
    }

    private void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra("url"))) {
                mUrl = getIntent().getStringExtra("url");
            }
        } else {
            mUrl = savedInstanceState.getString("url");
        }
    }

    @Override
    public void forwordMethod() { }

    @Override
    public void onClick(View view) {
        if (view == sobot_tv_left) {// 返回按钮
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
                finish();
            }
        } else if (view == sobot_btn_reconnect) {
            if (!TextUtils.isEmpty(mUrl)) {
                resetViewDisplay();
            }
        } else if (view == sobot_webview_forward) {
            mWebView.goForward();
        } else if (view == sobot_webview_goback) {
            mWebView.goBack();
        } else if(view == sobot_webview_reload){
            mWebView.reload();
        }
    }

    /**
     * 根据有无网络显示不同的View
     */
    private void resetViewDisplay() {
        if (CommonUtils.isNetWorkConnected(getApplicationContext())) {
            mWebView.setVisibility(View.VISIBLE);
            sobot_webview_toolsbar.setVisibility(View.VISIBLE);
            sobot_rl_net_error.setVisibility(View.GONE);
        } else {
            mWebView.setVisibility(View.GONE);
            sobot_webview_toolsbar.setVisibility(View.GONE);
            sobot_rl_net_error.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NewApi")
    private void initWebView() {
        mWebView.getSettings().setDefaultFontSize(16);
        mWebView.getSettings().setTextZoom(100);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以使用localStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " sobot");

        // 应用可以有数据库
        mWebView.getSettings().setDatabaseEnabled(true);

        // 应用可以有缓存
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //注释的地方是打开其它应用，比如qq
                /*if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                } else {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(in);
                    return true;
                }*/
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                sobot_webview_goback.setEnabled(mWebView.canGoBack());
                sobot_webview_forward.setEnabled(mWebView.canGoForward());
                if(!mUrl.replace("http://","").replace("https://","").equals(view.getTitle())){
                    setTitle(view.getTitle());
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.i("网页--title---：" + title);
                if(!mUrl.replace("http://","").replace("https://","").equals(title)){
                    setTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                } else if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putString("url", mUrl);
        super.onSaveInstanceState(outState);
    }
}