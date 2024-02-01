package com.invoate.amybridges.invoatewebinspector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.instabug.library.Instabug;
import im.delight.android.webview.AdvancedWebView;
import org.apache.commons.io.FileUtils;

/* loaded from: classes.dex */
public class MainActivity extends Activity implements AdvancedWebView.Listener {
    EditText mEdit;
    private AdvancedWebView mWebView;
    private Menu menu;
    String urlGoTo = "";
    boolean desktopMode = false;
    boolean preventCaching = true;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(web.hacker.R.layout.activity_main);
        InstabugHandler instabugHandler = (InstabugHandler) getApplication();
        this.mWebView = (AdvancedWebView) findViewById(web.hacker.R.id.webview);
        if (Build.VERSION.SDK_INT >= 19) {
            AdvancedWebView advancedWebView = this.mWebView;
            AdvancedWebView.setWebContentsDebuggingEnabled(true);
        }
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        this.mWebView.getSettings().setDisplayZoomControls(false);
        this.mWebView.setListener(this, this);
        this.mEdit = (EditText) findViewById(web.hacker.R.id.et_web_address);
        this.mEdit.setOnKeyListener(new View.OnKeyListener() { // from class: com.invoate.amybridges.invoatewebinspector.MainActivity.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0 && keyCode == 66) {
                    MainActivity.this.urlGoTo = MainActivity.this.mEdit.getText().toString();
                    MainActivity.this.mWebView.setWebViewClient(new WebViewClient() { // from class: com.invoate.amybridges.invoatewebinspector.MainActivity.1.1
                        @Override // android.webkit.WebViewClient
                        public void onPageFinished(WebView view, String url) {
                        }
                    });
                    MainActivity.this.mWebView.setWebChromeClient(new WebChromeClient() { // from class: com.invoate.amybridges.invoatewebinspector.MainActivity.1.2
                        @Override // android.webkit.WebChromeClient
                        public void onProgressChanged(WebView view, int progress) {
                            ProgressBar progressbar = (ProgressBar) MainActivity.this.findViewById(web.hacker.R.id.progressBar);
                            progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#fb7800"), PorterDuff.Mode.SRC_IN);
                            progressbar.getProgressDrawable().setColorFilter(Color.parseColor("#fb7800"), PorterDuff.Mode.SRC_IN);
                            progressbar.setProgress(progress);
                            if (progress == 100) {
                                progressbar.setVisibility(8);
                                Toast.makeText(MainActivity.this, "Finished Loading", 0).show();
                                MainActivity.this.changeMenuItemStatus(MainActivity.this.menu, web.hacker.R.id.action_refresh, true);
                                return;
                            }
                            progressbar.setVisibility(0);
                        }
                    });
                    if (MainActivity.this.urlGoTo.contains("https")) {
                        MainActivity.this.urlGoTo = MainActivity.this.urlGoTo;
                    } else if (MainActivity.this.urlGoTo.contains("http")) {
                        MainActivity.this.urlGoTo = MainActivity.this.urlGoTo;
                    } else {
                        MainActivity.this.urlGoTo = "http://" + MainActivity.this.urlGoTo;
                    }
                    CookieManager.getInstance().setAcceptCookie(true);
                    MainActivity.this.mWebView.loadUrl(MainActivity.this.urlGoTo.trim(), MainActivity.this.preventCaching);
                    return true;
                }
                return false;
            }
        });
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(web.hacker.R.menu.my_options_menu, menu);
        changeMenuItemStatus(menu, web.hacker.R.id.action_back, false);
        changeMenuItemStatus(menu, web.hacker.R.id.action_refresh, false);
        return true;
    }

    public void changeMenuItemStatus(Menu grabMenu, int item, Boolean status) {
        MenuItem itemTrigger = grabMenu.findItem(item);
        itemTrigger.setEnabled(status.booleanValue());
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case web.hacker.R.id.action_back /* 2131230728 */:
                if (this.mWebView.canGoBack()) {
                    this.mWebView.goBack();
                    break;
                }
                break;
            case web.hacker.R.id.action_clearcookies /* 2131230736 */:
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= 21) {
                    cookieManager.removeAllCookies(new ValueCallback<Boolean>() { // from class: com.invoate.amybridges.invoatewebinspector.MainActivity.2
                        @Override // android.webkit.ValueCallback
                        public void onReceiveValue(Boolean aBoolean) {
                            Toast.makeText(MainActivity.this, "All Cookies Removed", 0).show();
                        }
                    });
                    break;
                } else {
                    cookieManager.removeAllCookie();
                    break;
                }
            case web.hacker.R.id.action_desktop /* 2131230739 */:
                if (!this.desktopMode) {
                    this.desktopMode = true;
                    this.mWebView.setInitialScale(getScale());
                    this.mWebView.setDesktopMode(this.desktopMode);
                    this.mWebView.getSettings().setUseWideViewPort(false);
                    item.setTitle("Unforce Desktop Mode");
                } else {
                    this.desktopMode = false;
                    this.mWebView.setInitialScale(0);
                    this.mWebView.setDesktopMode(this.desktopMode);
                    this.mWebView.getSettings().setSupportZoom(true);
                    this.mWebView.getSettings().setBuiltInZoomControls(true);
                    item.setTitle("Force Desktop Mode");
                }
                this.mWebView.setScrollBarStyle(33554432);
                this.mWebView.setScrollbarFadingEnabled(false);
                this.mWebView.loadUrl(this.urlGoTo.trim(), this.preventCaching);
                break;
            case web.hacker.R.id.action_refresh /* 2131230747 */:
                this.mWebView.reload();
                break;
            case web.hacker.R.id.action_report /* 2131230748 */:
                Instabug.invoke();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getScale() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Double val = Double.valueOf(new Double(width).doubleValue() / new Double(width).doubleValue());
        return Double.valueOf(val.doubleValue() * 100.0d).intValue();
    }

    @Override // android.app.Activity
    @SuppressLint({"NewApi"})
    protected void onResume() {
        super.onResume();
        this.mWebView.onResume();
    }

    @Override // android.app.Activity
    @SuppressLint({"NewApi"})
    protected void onPause() {
        this.mWebView.onPause();
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mWebView.onDestroy();
        FileUtils.deleteQuietly(getCacheDir());
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        this.mWebView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.mWebView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onPageStarted(String url, Bitmap favicon) {
        this.mWebView.setVisibility(4);
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onPageFinished(String url) {
        this.mEdit.setText(this.mWebView.getUrl());
        this.mWebView.loadUrl("javascript:(function(F,i,r,e,b,u,g,L,I,T,E){if(F.getElementById(b))return;E=F[i+'NS']&&F.documentElement.namespaceURI;E=E?F[i+'NS'](E,'script'):F[i]('script');E[r]('id',b);E[r]('src',I+g+T);E[r](b,u);(F[e]('head')[0]||F[e]('body')[0]).appendChild(E);E=new%20Image;E[r]('src',I+L);})(document,'createElement','setAttribute','getElementsByTagName','FirebugLite','4','firebug-lite.js','releases/lite/latest/skin/xp/sprite.png','http://firebug-lite.s3.us-east-2.amazonaws.com/','#startOpened');");
        this.mWebView.setVisibility(0);
        if (this.mWebView.canGoBack()) {
            changeMenuItemStatus(this.menu, web.hacker.R.id.action_back, true);
        } else {
            changeMenuItemStatus(this.menu, web.hacker.R.id.action_back, false);
        }
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onExternalPageRequest(String url) {
    }
}
