package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.webkit.JavascriptInterface;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebViewClient;

import android.graphics.Bitmap;
import java.io.IOException;
import java.io.InputStream;
import android.widget.Toast;

public class MainActivity extends Activity {
  private WebView mWebView;
  private ValueStore valueStore;
	
  @Override
  @SuppressLint("SetJavaScriptEnabled")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mWebView = findViewById(R.id.activity_main_webview);
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    //mWebView.setWebViewClient(new getsetGoClient());
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (url.matches("^(https?://(www|m)\\.youtube\\.com/.*)|(https?://.*\\.youtube-nocookie\\.com/embed/.*)|(https?://youtube\\.googleapis\\.com/embed/.*)|(https?://raingart\\.github\\.io/options\\.html.*)$") &&
          !url.matches("^(https?://.*\\.youtube\\.com/.*\\.xml.*)|(https?://.*\\.youtube\\.com/error.*)|(https?://music\\.youtube\\.com/.*)|(https?://accounts\\.youtube\\.com/.*)|(https?://studio\\.youtube\\.com/.*)|(https?://.*\\.youtube\\.com/redirect\\?.*)$")) {
        
        // Inject JavaScript code from the assets folder
          try {
            InputStream inputStream = getAssets().open("script.js");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String script = new String(buffer);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              view.evaluateJavascript(script, null);
            } else {
              view.loadUrl("javascript:\n" + script);
            }
//            view.evaluateJavascript(script, null);
            Toast.makeText(MainActivity.this, "javascript injected: " + url, Toast.LENGTH_SHORT).show();

          } catch (IOException e) {
            Toast.makeText(MainActivity.this, "javascript didn't inject ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
          }
        }
      }

      @Override
      public void onPageFinished(WebView view, String url) {
      
      }

    });

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
