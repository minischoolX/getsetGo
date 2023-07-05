package com.example.app;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.os.Build;
import android.graphics.Bitmap;
import java.io.IOException;
import java.io.InputStream;
import android.widget.Toast;


class getsetGoClient extends WebViewClient {

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
              view.loadUrl("javascript:\n" + jsCode);
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


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String hostname;

        // YOUR HOSTNAME
        hostname = "example.com";

        Uri uri = Uri.parse(url);
        if (url.startsWith("file:") || uri.getHost() != null && uri.getHost().endsWith(hostname)) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
    }
}
