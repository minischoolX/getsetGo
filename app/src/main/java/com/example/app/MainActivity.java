package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {
  private WebView mWebView;

  @Override
  @SuppressLint("SetJavaScriptEnabled")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mWebView = findViewById(R.id.activity_main_webview);
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    mWebView.setWebViewClient(new getsetGoClient());
    mWebView.addJavascriptInterface(new JavaScriptInterface(), "Android");
    mWebView.loadUrl("https://google.com");
}

  private class JavaScriptInterface {
    @JavascriptInterface
    public String GM_getValue(String key, String value) {
	  String v = valueStore.getValue(key);
	  return (v != null) ? v : value;
	}
        
    @JavascriptInterface
	public void GM_setValue(String key, String value) {
	  valueStore.setValue(key, value);
	}
        
    @JavascriptInterface
    public void GM_registerMenuCommand(String caption, String configPage) {
      mWebView.loadUrl(configPage);
    }

    @JavascriptInterface
    public void GM_openInWindow(String url) {
      mWebView.loadUrl(url);
    }
  }

  @Override
  public void onBackPressed() {
    if(mWebView.canGoBack()) {
      mWebView.goBack();
    } else {
      super.onBackPressed();
    }
  }
}
