package com.lilialbum.player;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private WebView webView;
    private ImageView splash;
    private TextView exitButton;
    private float downX;
    private float downY;
    private boolean swiping;
    private boolean opened;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_main);

        splash = findViewById(R.id.splash);
        webView = findViewById(R.id.webview);
        exitButton = findViewById(R.id.exit_button);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");

        // The cover remains until the user swipes left, like turning a book page.
        splash.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        splash.setCameraDistance(getResources().getDisplayMetrics().density * 16000f);
        splash.setPivotX(0f);
        splash.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    swiping = true;
                    return true;
                case MotionEvent.ACTION_UP:
                    if (!swiping || opened) return true;
                    float dx = event.getX() - downX;
                    float dy = event.getY() - downY;
                    swiping = false;
                    if (dx < -80f && Math.abs(dx) > Math.abs(dy) * 1.1f) {
                        openPlayer();
                    }
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    swiping = false;
                    return true;
                default:
                    return true;
            }
        });

        exitButton.setOnClickListener(v -> finishAffinity());
    }

    private void openPlayer() {
        if (opened) return;
        opened = true;
        splash.animate()
                .rotationY(-92f)
                .setDuration(650)
                .withEndAction(() -> {
                    splash.setVisibility(View.GONE);
                    exitButton.setVisibility(View.VISIBLE);
                })
                .start();
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
