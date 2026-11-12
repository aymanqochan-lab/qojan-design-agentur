package se.grillkoket.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    static final String URL = "https://aymanqochan-lab.github.io/qojan-design-agentur/tv/#tv";
    static boolean visible = false;
    private boolean popup = false;
    private WebView web;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        web.setBackgroundColor(0xFF15120F);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame()) {
                    // No internet yet (e.g. right after power-on): try again in 20 seconds.
                    handler.postDelayed(() -> web.loadUrl(URL), 20000);
                }
            }
        });
        setContentView(web);
        hideBars();
        web.loadUrl(URL);
        AdScheduler.schedule(this);
        handlePopup(getIntent());
        // Fresh start once every 6 hours keeps memory low on small sticks.
        handler.postDelayed(new Runnable() {
            @Override public void run() { web.reload(); handler.postDelayed(this, 6L * 60 * 60 * 1000); }
        }, 6L * 60 * 60 * 1000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlePopup(intent);
    }

    // Opened by the timer on top of another app: show the ads for a while, then step back.
    private void handlePopup(Intent intent) {
        popup = intent != null && intent.getBooleanExtra("popup", false);
        if (popup) {
            handler.postDelayed(() -> { if (popup) moveTaskToBack(true); }, AdScheduler.SHOW_SECONDS * 1000L);
        }
    }

    @Override
    public boolean dispatchKeyEvent(android.view.KeyEvent e) {
        popup = false; // someone pressed a key: they are watching on purpose, stay open
        return super.dispatchKeyEvent(e);
    }

    @Override
    protected void onStart() { super.onStart(); visible = true; }

    @Override
    protected void onStop() { super.onStop(); visible = false; }

    private void hideBars() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideBars();
    }

    @Override
    protected void onResume() { super.onResume(); web.onResume(); }

    @Override
    protected void onPause() { super.onPause(); web.onPause(); }
}
