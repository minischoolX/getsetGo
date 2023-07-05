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

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

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

	JSONObject jsonObjectDemo = new JSONObject();
        try {
            jsonObjectDemo.put("name", "John Doe");
            jsonObjectDemo.put("age", 25);
            jsonObjectDemo.put("email", "johndoe@example.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }
	if (jsonObjectDemo != null) {
	  String stringifiedJson = jsonObjectDemo.toString();
//	  Toast.makeText(MainActivity.this, "StringifiedJson: \n" + stringifiedJson, Toast.LENGTH_LONG).show();	
	} else {
//	  Toast.makeText(MainActivity.this, "jsonObjectDemo itself is null", Toast.LENGTH_LONG).show();	
	}
	String setter = "Android.GM_setValue('demoKey'," + stringifiedJson + ");";
	mWebView.evaluateJavascript("javascript:\n" + setter , null);
	String getter = "Android.GM_getValue('demoKey');";
	mWebView.evaluateJavascript("javascript:\n" + getter , null);
	mWebView.evaluateJavascript("javascript:Android.GM_registerMenuCommand('caption', 'www.google.com');", null);
	mWebView.evaluateJavascript("javascript:Android.GM_openInWindow('https://raingart.github.io/options.html');", null);
        // Inject JavaScript code from the assets folder
          try {
            InputStream inputStream = getAssets().open("script.js");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String script = new String(buffer);
//	    String jsCode = "Android.Toast(\'Hello from injected JavaScript toast!\');\r\nwindow.nova_plugins = [];\r\nwindow.nova_plugins.push({\r\n   id: \'square-avatars\',\r\n   title: \'Square avatars\',\r\n   run_on_pages: \'*, -live_chat\',\r\n   section: \'comments\',\r\n   desc: \'Make user images squared\',\r\n   _runtime: user_settings => {\r\nAndroid.Toast(\'plugin runtime called\');\r\n      NOVA.css.push(\r\n         [\r\n            \'yt-img-shadow\',\r\n            \'.ytp-title-channel-logo\',\r\n            \'#player .ytp-title-channel\',\r\n            \'ytm-profile-icon\',\r\n            \'a.ytd-thumbnail\',\r\n         ]\r\n            .join(\',\\n\') + ` {\r\n               border-radius: 0 !important;\r\n            }`);\r\n      NOVA.waitUntil(() => {\r\n         if (window.yt && (obj = yt?.config_?.EXPERIMENT_FLAGS) && Object.keys(obj).length) {\r\n            yt.config_.EXPERIMENT_FLAGS.web_rounded_thumbnails = false;\r\n            return true;\r\n         }\r\n      });\r\n   },\r\n});\r\nconst NOVA = {\r\n   waitSelector(selector = required(), limit_data) {\r\n      if (typeof selector !== \'string\') return console.error(\'wait > selector:\', typeof selector);\r\n      if (limit_data?.container && !(limit_data.container instanceof HTMLElement)) return console.error(\'wait > container not HTMLElement:\', limit_data.container);\r\n      if (selector.includes(\':has(\') && !CSS.supports(\'selector(:has(*))\')) {\r\n         return new Promise((resolve, reject) => {\r\n            console.warn(\'CSS \":has()\" unsupported\');\r\n            reject(\'CSS \":has()\" unsupported\');\r\n         });\r\n      }\r\n      return new Promise(resolve => {\r\n         if (element = (limit_data?.container || document.body || document).querySelector(selector)) {\r\n            return resolve(element);\r\n         }\r\n         const observer1 = new MutationObserver((mutationRecordsArray, observer) => {\r\n            for (const record of mutationRecordsArray) {\r\n               for (const node of record.addedNodes) {\r\n                  if (![1, 3, 8].includes(node.nodeType) || !(node instanceof HTMLElement)) continue;\r\n                  if (node.matches && node.matches(selector)) {\r\n                     observer.disconnect();\r\n                     return resolve(node);\r\n                  }\r\n                  else if (\r\n                     (parentEl = node.parentElement || node)\r\n                     && (parentEl instanceof HTMLElement)\r\n                     && (element = parentEl.querySelector(selector))\r\n                  ) {\r\n                     observer.disconnect();\r\n                     return resolve(element);\r\n                  }\r\n               }\r\n            }\r\n            if (document?.readyState != \'loading\'\r\n               && (element = (limit_data?.container || document?.body || document).querySelector(selector))\r\n            ) {\r\n               observer.disconnect();\r\n               return resolve(element);\r\n            }\r\n         })\r\n         observer1\r\n            .observe(limit_data?.container || document.body || document.documentElement || document, {\r\n               childList: true,\r\n               subtree: true,\r\n               attributes: true,\r\n            });\r\n         if (limit_data?.stop_on_page_change) {\r\n            isURLChange();\r\n            window.addEventListener(\'transitionend\', ({ target }) => {\r\n               if (isURLChange()) {\r\n                  observer1.disconnect();\r\n               }\r\n            });\r\n            function isURLChange() {\r\n               return (this.prevURL === location.href) ? false : this.prevURL = location.href;\r\n            }\r\n         }\r\n      });\r\n   },\r\n   waitUntil(condition = required(), timeout = 100) {\r\n      if (typeof condition !== \'function\') return console.error(\'waitUntil > condition is not fn:\', typeof condition);\r\n      return new Promise((resolve) => {\r\n         if (result = condition()) {\r\n            resolve(result);\r\n         }\r\n         else {\r\n            const interval = setInterval(() => {\r\n               if (result = condition()) {\r\n                  clearInterval(interval);\r\n                  resolve(result);\r\n               }\r\n            }, timeout);\r\n         }\r\n      });\r\n   },\r\n\r\n   delay(ms = 100) {\r\n      return new Promise(resolve => setTimeout(resolve, ms));\r\n   },\r\n\r\n   css: {\r\n      push(css = required(), selector, important) {\r\n         if (typeof css === \'object\') {\r\n            if (!selector) return console.error(\'injectStyle > empty json-selector:\', ...arguments);\r\n            injectCss(selector + json2css(css));\r\n            function json2css(obj) {\r\n               let css = \'\';\r\n               Object.entries(obj)\r\n                  .forEach(([key, value]) => {\r\n                     css += key + \':\' + value + (important ? \' !important\' : \'\') + \';\';\r\n                  });\r\n               return `{ ${css} }`;\r\n            }\r\n         }\r\n         else if (css && typeof css === \'string\') {\r\n            if (document.head) {\r\n               injectCss(css);\r\n            }\r\n            else {\r\n               window.addEventListener(\'load\', () => injectCss(css), { capture: true, once: true });\r\n            }\r\n         }\r\n         else {\r\n            console.error(\'addStyle > css:\', typeof css);\r\n         }\r\n         function injectCss(source = required()) {\r\n            let sheet;\r\n            if (source.endsWith(\'.css\')) {\r\n               sheet = document.createElement(\'link\');\r\n               sheet.rel = \'sheet\';\r\n               sheet.href = source;\r\n            }\r\n            else {\r\n               const sheetId = \'NOVA-style\';\r\n               sheet = document.getElementById(sheetId) || (function () {\r\n                  const style = document.createElement(\'style\');\r\n                  style.type = \'text/css\';\r\n                  style.id = sheetId;\r\n                  return (document.head || document.documentElement).appendChild(style);\r\n               })();\r\n            }\r\n            sheet.textContent += \'\\n\' + source\r\n               .replace(/\\n+\\s{2,}/g, \' \')\r\n               + \'\\n\';\r\n         }\r\n      },\r\n      getValue(selector = required(), prop_name = required()) {\r\n         return (el = (selector instanceof HTMLElement) ? selector : document.body?.querySelector(selector))\r\n            ? getComputedStyle(el).getPropertyValue(prop_name) : null;\r\n      },\r\n   },\r\n\r\n   getPlayerState(state) {\r\n      return {\r\n         \'-1\': \'UNSTARTED\',\r\n         0: \'ENDED\',\r\n         1: \'PLAYING\',\r\n         2: \'PAUSED\',\r\n         3: \'BUFFERING\',\r\n         5: \'CUED\'\r\n      }[state || movie_player.getPlayerState()];\r\n   },\r\n\r\n   videoElement: (() => {\r\n      const videoSelector = \'#movie_player:not(.ad-showing) video\';\r\n      document.addEventListener(\'canplay\', ({ target }) => {\r\n         target.matches(videoSelector) && (NOVA.videoElement = target);\r\n      }, { capture: true, once: true });\r\n      document.addEventListener(\'play\', ({ target }) => {\r\n         target.matches(videoSelector) && (NOVA.videoElement = target);\r\n      }, true);\r\n   })(),\r\n\r\n   isFullscreen: () => (\r\n      movie_player.classList.contains(\'ytp-fullscreen\')\r\n      || (movie_player.hasOwnProperty(\'isFullscreen\') && movie_player.isFullscreen())\r\n   ),\r\n\r\n   log() {\r\n      if (this.DEBUG && arguments.length) {\r\n         console.groupCollapsed(...arguments);\r\n         console.trace();\r\n         console.groupEnd();\r\n      }\r\n   }\r\n}\r\nconst Plugins = {\r\n   run: ({ user_settings, app_ver }) => {\r\n      if (!window.nova_plugins?.length) return;\r\n      if (!user_settings) return;\r\n      NOVA.currentPage = (function () {\r\n         const pathnameArray = location.pathname.split(\'/\').filter(Boolean);\r\n         const [page, channelTab] = [pathnameArray[0], pathnameArray.pop()];\r\n         NOVA.channelTab = [\'featured\', \'videos\', \'shorts\', \'streams\', \'playlists\', \'community\', \'channels\', \'about\'].includes(channelTab) ? channelTab : false;\r\n         return (page != \'live_chat\')\r\n            && ([\'channel\', \'c\', \'user\'].includes(page)\r\n               || page?.startsWith(\'@\')\r\n               || /[A-Z\\d_]/.test(page)\r\n               || NOVA.channelTab\r\n            ) ? \'channel\' : (page == \'clip\') ? \'watch\' : page || \'home\';\r\n      })();\r\n      NOVA.isMobile = location.host == \'m.youtube.com\';\r\n      let logTableArray = [],\r\n         logTableStatus,\r\n         logTableTime;\r\n      window.nova_plugins?.forEach(plugin => {\r\n         const pagesAllowList = plugin?.run_on_pages?.split(\',\').map(p => p.trim().toLowerCase()).filter(Boolean);\r\n         logTableTime = 0;\r\n         logTableStatus = false;\r\n         if (!pluginChecker(plugin)) {\r\n            console.error(\'Plugin invalid\\n\', plugin);\r\n            alert(\'Plugin invalid: \' + plugin?.id);\r\n            logTableStatus = \'INVALID\';\r\n         }\r\n         else if (plugin.was_init && !plugin.restart_on_location_change) {\r\n            logTableStatus = \'skiped\';\r\n         }\r\n         else if (!user_settings.hasOwnProperty(plugin.id)) {\r\n            logTableStatus = \'off\';\r\n         }\r\n         else if (\r\n            (\r\n               pagesAllowList?.includes(NOVA.currentPage)\r\n               || (pagesAllowList?.includes(\'*\') && !pagesAllowList?.includes(\'-\' + NOVA.currentPage))\r\n            )\r\n            && (!NOVA.isMobile || (NOVA.isMobile && !pagesAllowList?.includes(\'-mobile\')))\r\n         ) {\r\n            try {\r\n               const startTableTime = performance.now();\r\n               plugin.was_init = true;\r\n               plugin._runtime(user_settings);\r\n               logTableTime = (performance.now() - startTableTime).toFixed(2);\r\n               logTableStatus = true;\r\n            } catch (err) {\r\n               console.groupEnd(\'plugins status\');\r\n               console.error(`[ERROR PLUGIN] ${plugin.id}\\n${err.stack}\\n\\nPlease report the bug: https://github.com/raingart/Nova-YouTube-extension/issues/new?body=` + encodeURIComponent(app_ver + \' | \' + navigator.userAgent));\r\n               if (user_settings.report_issues && _pluginsCaptureException) {\r\n                  _pluginsCaptureException({\r\n                     \'trace_name\': plugin.id,\r\n                     \'err_stack\': err.stack,\r\n                     \'app_ver\': app_ver,\r\n                     \'confirm_msg\': `ERROR in Nova YouTube™\\n\\nCrash plugin: \"${plugin.title || plugin.id}\"\\nPlease report the bug or disable the plugin\\n\\nSend the bug raport to developer?`,\r\n                  });\r\n               }\r\n               console.groupCollapsed(\'plugins status\');\r\n               logTableStatus = \'ERROR\';\r\n            }\r\n         }\r\n         logTableArray.push({\r\n            \'launched\': logTableStatus,\r\n            \'name\': plugin?.id,\r\n            \'time init (ms)\': logTableTime,\r\n         });\r\n      });\r\n      console.table(logTableArray);\r\n      console.groupEnd(\'plugins status\');\r\n      function pluginChecker(plugin) {\r\n         const result = plugin?.id && plugin.run_on_pages && \'function\' === typeof plugin._runtime;\r\n         if (!result) {\r\n            console.error(\'plugin invalid:\\n\', {\r\n               \'id\': plugin?.id,\r\n               \'run_on_pages\': plugin?.run_on_pages,\r\n               \'_runtime\': \'function\' === typeof plugin?._runtime,\r\n            });\r\n         }\r\n         return result;\r\n      }\r\n   },\r\n}\r\nconst\r\n   configPage = \'https://raingart.github.io/options.html\',\r\n   configStoreName = \'user_settings\',\r\n   user_settings = window.Android.GM_getValue(configStoreName, null);\r\nif (user_settings?.exclude_iframe && (window.frameElement || window.self !== window.top)) {\r\n   return Android.Toast(\'GM info script name\' + \': processed in the iframe disable\');\r\n}\r\nconst keyRenameTemplate = {\r\n   \'shorts_thumbnails_time\': \'shorts-thumbnails-time\',\r\n}\r\nfor (const oldKey in user_settings) {\r\n   if (newKey = keyRenameTemplate[oldKey]) {\r\n      console.log(oldKey, \'=>\', newKey);\r\n      delete Object.assign(user_settings, { [newKey]: user_settings[oldKey] })[oldKey];\r\n   }\r\n   window.Android.GM_setValue(configStoreName, user_settings);\r\n   window.Android.Toast(user_settings);\r\n}\r\nregisterMenuCommand();\r\nif (location.hostname === new URL(configPage).hostname) setupConfigPage();\r\nelse {\r\n   if (!user_settings?.disable_setting_button) insertSettingButton();\r\n   if (!user_settings || !Object.keys(user_settings).length) {\r\n      Android.GM_openInWindow(configPage);\r\n      //if (confirm(\'Active plugins undetected. Open the settings page now?\')) window.Android.GM_openInWindow(configPage);\r\n      //user_settings[\'report_issues\'] = \'on\';\r\n      Android.GM_setValue(configStoreName, user_settings);\r\n      Android.Toast(\'confirmationStage called without confirm\');\r\n   }\r\n   else landerPlugins();\r\n}\r\nfunction setupConfigPage() {\r\n   document.addEventListener(\'submit\', event => {\r\n      event.preventDefault();\r\n      let obj = {};\r\n      for (const [key, value] of new FormData(event.target)) {\r\n         if (obj.hasOwnProperty(key)) {\r\n            obj[key] += \',\' + value;\r\n            obj[key] = obj[key].split(\',\');\r\n         }\r\n         else {\r\n            switch (value) {\r\n               case \'true\': obj[key] = true; break;\r\n               case \'false\': obj[key] = false; break;\r\n               case \'undefined\': delete obj[key]; break;\r\n               default: obj[key] = value;\r\n            }\r\n         };\r\n      }\r\n      console.debug(`update ${configStoreName}:`, obj);\r\n      Android.GM_setValue(configStoreName, obj);\r\n   });\r\n   window.addEventListener(\'DOMContentLoaded\', () => {\r\n      localizePage(user_settings?.lang_code);\r\n      storeData = user_settings;\r\n      //unsafeWindow.window.nova_plugins = window.nova_plugins;\r\n   });\r\n   window.addEventListener(\'load\', () => {\r\n      document.body?.classList?.remove(\'preload\');\r\n      document.body.querySelector(\'a[href$=\"issues/new\"]\')\r\n         .addEventListener(\'click\', ({ target }) => {\r\n            target.href += \'?body=\' + encodeURIComponent(\'GM info script version\' + \' | \' + navigator.userAgent);\r\n         });\r\n   });\r\n}\r\nfunction landerPlugins() {\r\n   processLander();\r\n   function processLander() {\r\n      const plugins_lander = setInterval(() => {\r\n         const domLoaded = document?.readyState != \'loading\';\r\n         if (!domLoaded) return console.debug(\'waiting, page loading..\');\r\n         clearInterval(plugins_lander);\r\n         console.groupCollapsed(\'plugins status\');\r\n         Plugins.run({\r\n            \'user_settings\': user_settings,\r\n            \'app_ver\': \'0.43.0\',\r\n         });\r\n      }, 500);\r\n   }\r\n   let prevURL = location.href;\r\n   const isURLChanged = () => prevURL == location.href ? false : prevURL = location.href;\r\n   if (isMobile = (location.host == \'m.youtube.com\')) {\r\n      window.addEventListener(\'transitionend\', ({ target }) => target.id == \'progress\' && isURLChange() && processLander());\r\n   }\r\n   else {\r\n      document.addEventListener(\'yt-navigate-start\', () => isURLChanged() && processLander());\r\n   }\r\n}\r\nfunction registerMenuCommand() {\r\n   Android.GM_registerMenuCommand(\'Settings\', configPage);\r\n}\r\nfunction insertSettingButton() {\r\n   NOVA.waitSelector(\'#masthead #end\')\r\n      .then(menu => {\r\n         const\r\n            titleMsg = \'Nova Settings\',\r\n            a = document.createElement(\'a\'),\r\n            SETTING_BTN_ID = \'nova_settings_button\';\r\n         a.id = SETTING_BTN_ID;\r\n         a.href = configPage + \'?tabs=tab-plugins\';\r\n         a.target = \'_blank\';\r\n         a.innerHTML =\r\n            `<yt-icon-button class=\"style-scope ytd-button-renderer style-default size-default\">\r\n               <svg viewBox=\"-4 0 20 16\">\r\n                  <radialGradient id=\"nova-gradient\" gradientUnits=\"userSpaceOnUse\" cx=\"6\" cy=\"22\" r=\"18.5\">\r\n                     <stop class=\"nova-gradient-start\" offset=\"0\"/>\r\n                     <stop class=\"nova-gradient-stop\" offset=\"1\"/>\r\n                  </radialGradient>\r\n                  <g fill=\"deepskyblue\">\r\n                     <polygon points=\"0,16 14,8 0,0\"/>\r\n                  </g>\r\n               </svg>\r\n            </yt-icon-button>`;\r\n         a.addEventListener(\'click\', () => {\r\n            setTimeout(() => document.body.click(), 200);\r\n         });\r\n         a.title = titleMsg;\r\n         const tooltip = document.createElement(\'tp-yt-paper-tooltip\');\r\n         tooltip.className = \'style-scope ytd-topbar-menu-button-renderer\';\r\n         tooltip.textContent = titleMsg;\r\n         a.appendChild(tooltip);\r\n         menu.prepend(a);\r\n         NOVA.css.push(\r\n            `#${SETTING_BTN_ID}[tooltip]:hover:after {\r\n               position: absolute;\r\n               top: 50px;\r\n               transform: translateX(-50%);\r\n               content: attr(tooltip);\r\n               text-align: center;\r\n               min-width: 3em;\r\n               max-width: 21em;\r\n               white-space: nowrap;\r\n               overflow: hidden;\r\n               text-overflow: ellipsis;\r\n               padding: 1.8ch 1.2ch;\r\n               border-radius: 0.6ch;\r\n               background-color: #616161;\r\n               box-shadow: 0 1em 2em -0.5em rgb(0 0 0 / 35%);\r\n               color: #fff;\r\n               z-index: 1000;\r\n            }\r\n            #${SETTING_BTN_ID} {\r\n               position: relative;\r\n               opacity: .3;\r\n               transition: opacity .3s ease-out;\r\n            }\r\n            #${SETTING_BTN_ID}:hover {\r\n               opacity: 1 !important;\r\n            }\r\n            #${SETTING_BTN_ID} path,\r\n            #${SETTING_BTN_ID} polygon {\r\n               fill: url(#nova-gradient);\r\n            }\r\n            #${SETTING_BTN_ID} .nova-gradient-start,\r\n            #${SETTING_BTN_ID} .nova-gradient-stop {\r\n               transition: .6s;\r\n               stop-color: #7a7cbd;\r\n            }\r\n            #${SETTING_BTN_ID}:hover .nova-gradient-start {\r\n               stop-color: #0ff;\r\n            }\r\n            #${SETTING_BTN_ID}:hover .nova-gradient-stop {\r\n               stop-color: #0095ff;\r\n               \r\n            }`);\r\n      });\r\n}\r\nfunction _pluginsCaptureException({ trace_name, err_stack, confirm_msg, app_ver }) {\r\n}";
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//              mWebView.evaluateJavascript(script, null);
//	      mWebView.evaluateJavascript("javascript:\n" + jsCode, null);
//            } else {
//              view.loadUrl("javascript:\n" + script);
//            }
//            mWebView.evaluateJavascript(script, null);
          } catch (IOException e) {
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

  SharedPreferences sharedPreferences = getSharedPreferences("getsetGo", MainActivity.this.MODE_PRIVATE);
  SharedPreferences.Editor editor = sharedPreferences.edit();

    @JavascriptInterface
    public String GMX_getValue(String key, String value) {
	String v = valueStore.getValue(key);
	    if (v != null) {
		Toast.makeText(MainActivity.this, v, Toast.LENGTH_SHORT).show();
	    }
	  return (v != null) ? v : value;
    }

    @JavascriptInterface
    public String GM_getValue(String key) {
      String value = sharedPreferences.getString(key, null);
//	    if (value != null) {
//		Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
//	    } else {
//	        Toast.makeText(MainActivity.this, "value is null for GM_getValue", Toast.LENGTH_SHORT).show();
//	    }
      return value;
    }

    @JavascriptInterface
    public void GM_setValue(String key, String value) {
      editor.putString(key, value);
      editor.apply();
	if (value != null) {
	  Toast.makeText(MainActivity.this, "executing GM_setValue with :" + "\n" + "Key : " + key + "\n" + "value :" + "\n" + value, Toast.LENGTH_SHORT).show();
	} else {
	  Toast.makeText(MainActivity.this, "GM_setValue is failure", Toast.LENGTH_SHORT).show();
	}      
    }
        
    @JavascriptInterface
    public void GMX_setValue(String key, String value) {
	  valueStore.setValue(key, value);
    }
        
    @JavascriptInterface
    public void GM_registerMenuCommand(String caption, String configPage) {
      mWebView.loadUrl(configPage);
      Toast.makeText(MainActivity.this, "GM_registerMenuCommand called", Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void GM_openInWindow(String url) {
      mWebView.loadUrl(url);
      Toast.makeText(MainActivity.this, "GM_openInWindow called", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void Toast(String toastString) {
      Toast.makeText(MainActivity.this, toastString, Toast.LENGTH_SHORT).show();
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
