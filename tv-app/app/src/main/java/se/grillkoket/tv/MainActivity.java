package se.grillkoket.tv;

import android.app.Activity;
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
        // Fresh start once every 6 hours keeps memory low on small sticks.
        handler.postDelayed(new Runnable() {
            @Override public void run() { web.reload(); handler.postDelayed(this, 6L * 60 * 60 * 1000); }
        }, 6L * 60 * 60 * 1000);
    }

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
