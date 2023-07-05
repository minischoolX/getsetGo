Android.Toast("atleast jsInterface is working");
window.nova_plugins = [];
window.nova_plugins.push({
   id: 'square-avatars',
   title: 'Square avatars',
   run_on_pages: '*, -live_chat',
   section: 'comments',
   desc: 'Make user images squared',
   _runtime: user_settings => {
      NOVA.css.push(
         [
            'yt-img-shadow',
            '.ytp-title-channel-logo',
            '#player .ytp-title-channel',
            'ytm-profile-icon',
            'a.ytd-thumbnail',
         ]
            .join(',\n') + ` {
               border-radius: 0 !important;
            }`);
      NOVA.waitUntil(() => {
         if (window.yt && (obj = yt?.config_?.EXPERIMENT_FLAGS) && Object.keys(obj).length) {
            yt.config_.EXPERIMENT_FLAGS.web_rounded_thumbnails = false;
            return true;
         }
      });
   },
});
const NOVA = {
   waitSelector(selector = required(), limit_data) {
      if (typeof selector !== 'string') return console.error('wait > selector:', typeof selector);
      if (limit_data?.container && !(limit_data.container instanceof HTMLElement)) return console.error('wait > container not HTMLElement:', limit_data.container);
      if (selector.includes(':has(') && !CSS.supports('selector(:has(*))')) {
         return new Promise((resolve, reject) => {
            console.warn('CSS ":has()" unsupported');
            reject('CSS ":has()" unsupported');
         });
      }
      return new Promise(resolve => {
         if (element = (limit_data?.container || document.body || document).querySelector(selector)) {
            return resolve(element);
         }
         const observer1 = new MutationObserver((mutationRecordsArray, observer) => {
            for (const record of mutationRecordsArray) {
               for (const node of record.addedNodes) {
                  if (![1, 3, 8].includes(node.nodeType) || !(node instanceof HTMLElement)) continue;
                  if (node.matches && node.matches(selector)) {
                     observer.disconnect();
                     return resolve(node);
                  }
                  else if (
                     (parentEl = node.parentElement || node)
                     && (parentEl instanceof HTMLElement)
                     && (element = parentEl.querySelector(selector))
                  ) {
                     observer.disconnect();
                     return resolve(element);
                  }
               }
            }
            if (document?.readyState != 'loading'
               && (element = (limit_data?.container || document?.body || document).querySelector(selector))
            ) {
               observer.disconnect();
               return resolve(element);
            }
         })
         observer1
            .observe(limit_data?.container || document.body || document.documentElement || document, {
               childList: true,
               subtree: true,
               attributes: true,
            });
         if (limit_data?.stop_on_page_change) {
            isURLChange();
            window.addEventListener('transitionend', ({ target }) => {
               if (isURLChange()) {
                  observer1.disconnect();
               }
            });
            function isURLChange() {
               return (this.prevURL === location.href) ? false : this.prevURL = location.href;
            }
         }
      });
   },
   waitUntil(condition = required(), timeout = 100) {
      if (typeof condition !== 'function') return console.error('waitUntil > condition is not fn:', typeof condition);
      return new Promise((resolve) => {
         if (result = condition()) {
            resolve(result);
         }
         else {
            const interval = setInterval(() => {
               if (result = condition()) {
                  clearInterval(interval);
                  resolve(result);
               }
            }, timeout);
         }
      });
   },

   delay(ms = 100) {
      return new Promise(resolve => setTimeout(resolve, ms));
   },

   css: {
      push(css = required(), selector, important) {
         if (typeof css === 'object') {
            if (!selector) return console.error('injectStyle > empty json-selector:', ...arguments);
            injectCss(selector + json2css(css));
            function json2css(obj) {
               let css = '';
               Object.entries(obj)
                  .forEach(([key, value]) => {
                     css += key + ':' + value + (important ? ' !important' : '') + ';';
                  });
               return `{ ${css} }`;
            }
         }
         else if (css && typeof css === 'string') {
            if (document.head) {
               injectCss(css);
            }
            else {
               window.addEventListener('load', () => injectCss(css), { capture: true, once: true });
            }
         }
         else {
            console.error('addStyle > css:', typeof css);
         }
         function injectCss(source = required()) {
            let sheet;
            if (source.endsWith('.css')) {
               sheet = document.createElement('link');
               sheet.rel = 'sheet';
               sheet.href = source;
            }
            else {
               const sheetId = 'NOVA-style';
               sheet = document.getElementById(sheetId) || (function () {
                  const style = document.createElement('style');
                  style.type = 'text/css';
                  style.id = sheetId;
                  return (document.head || document.documentElement).appendChild(style);
               })();
            }
            sheet.textContent += '\n' + source
               .replace(/\n+\s{2,}/g, ' ')
               + '\n';
         }
      },
      getValue(selector = required(), prop_name = required()) {
         return (el = (selector instanceof HTMLElement) ? selector : document.body?.querySelector(selector))
            ? getComputedStyle(el).getPropertyValue(prop_name) : null;
      },
   },

   getPlayerState(state) {
      return {
         '-1': 'UNSTARTED',
         0: 'ENDED',
         1: 'PLAYING',
         2: 'PAUSED',
         3: 'BUFFERING',
         5: 'CUED'
      }[state || movie_player.getPlayerState()];
   },

   videoElement: (() => {
      const videoSelector = '#movie_player:not(.ad-showing) video';
      document.addEventListener('canplay', ({ target }) => {
         target.matches(videoSelector) && (NOVA.videoElement = target);
      }, { capture: true, once: true });
      document.addEventListener('play', ({ target }) => {
         target.matches(videoSelector) && (NOVA.videoElement = target);
      }, true);
   })(),

   isFullscreen: () => (
      movie_player.classList.contains('ytp-fullscreen')
      || (movie_player.hasOwnProperty('isFullscreen') && movie_player.isFullscreen())
   ),

   log() {
      if (this.DEBUG && arguments.length) {
         console.groupCollapsed(...arguments);
         console.trace();
         console.groupEnd();
      }
   }
}


const Plugins = {
   run: ({ user_settings, app_ver }) => {
      if (!window.nova_plugins?.length) return window.Android.Toast('nova_plugins empty');
      if (!user_settings) return window.Android.Toast('user_settings empty');
      NOVA.currentPage = (function () {
         const pathnameArray = location.pathname.split('/').filter(Boolean);
         const [page, channelTab] = [pathnameArray[0], pathnameArray.pop()];
         NOVA.channelTab = ['featured', 'videos', 'shorts', 'streams', 'playlists', 'community', 'channels', 'about'].includes(channelTab) ? channelTab : false;
         return (page != 'live_chat')
            && (['channel', 'c', 'user'].includes(page)
               || page?.startsWith('@')
               || /[A-Z\d_]/.test(page)
               || NOVA.channelTab
            ) ? 'channel' : (page == 'clip') ? 'watch' : page || 'home';
      })();
      NOVA.isMobile = location.host == 'm.youtube.com';
      let logTableArray = [],
         logTableStatus,
         logTableTime;
      window.nova_plugins?.forEach(plugin => {
         const pagesAllowList = plugin?.run_on_pages?.split(',').map(p => p.trim().toLowerCase()).filter(Boolean);
         logTableTime = 0;
         logTableStatus = false;
         if (!pluginChecker(plugin)) {
            console.error('Plugin invalid\n', plugin);
            alert('Plugin invalid: ' + plugin?.id);
            logTableStatus = 'INVALID';
         }
         else if (plugin.was_init && !plugin.restart_on_location_change) {
            logTableStatus = 'skiped';
         }
         else if (!user_settings.hasOwnProperty(plugin.id)) {
            logTableStatus = 'off';
         }
         else if (
            (
               pagesAllowList?.includes(NOVA.currentPage)
               || (pagesAllowList?.includes('*') && !pagesAllowList?.includes('-' + NOVA.currentPage))
            )
            && (!NOVA.isMobile || (NOVA.isMobile && !pagesAllowList?.includes('-mobile')))
         ) {
            try {
               const startTableTime = performance.now();
               plugin.was_init = true;
               plugin._runtime(user_settings);
               logTableTime = (performance.now() - startTableTime).toFixed(2);
               logTableStatus = true;
            } catch (err) {
               console.groupEnd('plugins status');
               console.error(`[ERROR PLUGIN] ${plugin.id}\n${err.stack}\n\nPlease report the bug: https://github.com/raingart/Nova-YouTube-extension/issues/new?body=` + encodeURIComponent(app_ver + ' | ' + navigator.userAgent));
               if (user_settings.report_issues && _pluginsCaptureException) {
                  _pluginsCaptureException({
                     'trace_name': plugin.id,
                     'err_stack': err.stack,
                     'app_ver': app_ver,
                     'confirm_msg': `ERROR in Nova YouTube™\n\nCrash plugin: "${plugin.title || plugin.id}"\nPlease report the bug or disable the plugin\n\nSend the bug raport to developer?`,
                  });
               }
               console.groupCollapsed('plugins status');
               logTableStatus = 'ERROR';
            }
         }
         logTableArray.push({
            'launched': logTableStatus,
            'name': plugin?.id,
            'time init (ms)': logTableTime,
         });
      });
      console.table(logTableArray);
      console.groupEnd('plugins status');
      function pluginChecker(plugin) {
         const result = plugin?.id && plugin.run_on_pages && 'function' === typeof plugin._runtime;
         if (!result) {
            console.error('plugin invalid:\n', {
               'id': plugin?.id,
               'run_on_pages': plugin?.run_on_pages,
               '_runtime': 'function' === typeof plugin?._runtime,
            });
         }
         return result;
      }
   },
}
window.Android.Toast('%c /• %s •/');
const
   configPage = 'https://raingart.github.io/options.html',
   configStoreName = 'user_settings',
   user_settings = window.Android.GM_getValue(configStoreName, null);
if (user_settings?.exclude_iframe && (window.frameElement || window.self !== window.top)) {
   return window.Android.Toast('GM info script name' + ': processed in the iframe disable');
}
//console.debug(`current ${configStoreName}:`, user_settings);
const keyRenameTemplate = {
   'shorts_thumbnails_time': 'shorts-thumbnails-time',
}
for (const oldKey in user_settings) {
   if (newKey = keyRenameTemplate[oldKey]) {
      console.log(oldKey, '=>', newKey);
      delete Object.assign(user_settings, { [newKey]: user_settings[oldKey] })[oldKey];
   }
   window.Android.GM_setValue(configStoreName, user_settings);
   window.Android.Toast(user_settings);
}
registerMenuCommand();
if (location.hostname === new URL(configPage).hostname) setupConfigPage();
else {
   if (!user_settings?.disable_setting_button) insertSettingButton();
   window.Android.Toast('checking for user_setting or confirmationStage');
   if (!user_settings || !Object.keys(user_settings).length) {
      if (confirm('Active plugins undetected. Open the settings page now?')) window.Android.GM_openInWindow(configPage);
      user_settings['report_issues'] = 'on';
      window.Android.GM_setValue(configStoreName, user_settings);
      window.Android.Toast('confirmationStage called without confirm');
   }
   else landerPlugins();
}
function setupConfigPage() {
   document.addEventListener('submit', event => {
      event.preventDefault();
      let obj = {};
      for (const [key, value] of new FormData(event.target)) {
         if (obj.hasOwnProperty(key)) {
            obj[key] += ',' + value;
            obj[key] = obj[key].split(',');
         }
         else {
            switch (value) {
               case 'true': obj[key] = true; break;
               case 'false': obj[key] = false; break;
               case 'undefined': delete obj[key]; break;
               default: obj[key] = value;
            }
         };
      }
      console.debug(`update ${configStoreName}:`, obj);
      window.Android.GM_setValue(configStoreName, obj);
   });
   window.addEventListener('DOMContentLoaded', () => {
      localizePage(user_settings?.lang_code);
      storeData = user_settings;
      //unsafeWindow.window.nova_plugins = window.nova_plugins;
   });
   window.addEventListener('load', () => {
      document.body?.classList?.remove('preload');
      document.body.querySelector('a[href$="issues/new"]')
         .addEventListener('click', ({ target }) => {
            target.href += '?body=' + encodeURIComponent('GM info script version' + ' | ' + navigator.userAgent);
         });
   });
}
function landerPlugins() {
   processLander();
   function processLander() {
      const plugins_lander = setInterval(() => {
         const domLoaded = document?.readyState != 'loading';
         if (!domLoaded) return console.debug('waiting, page loading..');
         clearInterval(plugins_lander);
         console.groupCollapsed('plugins status');
         Plugins.run({
            'user_settings': user_settings,
            'app_ver': '0.43.0',
         });
      }, 500);
   }
   let prevURL = location.href;
   const isURLChanged = () => prevURL == location.href ? false : prevURL = location.href;
   if (isMobile = (location.host == 'm.youtube.com')) {
      window.addEventListener('transitionend', ({ target }) => target.id == 'progress' && isURLChange() && processLander());
   }
   else {
      document.addEventListener('yt-navigate-start', () => isURLChanged() && processLander());
   }
}
function registerMenuCommand() {
   window.Android.GM_registerMenuCommand('Settings', configPage);
//   GM_registerMenuCommand('Import settings', () => {
//      const f = document.createElement('input');
//      f.type = 'file';
//      f.accept = 'application/JSON';
//      f.style.display = 'none';
//      f.addEventListener('change', function () {
//         if (f.files.length !== 1) return alert('file empty');
//         const rdr = new FileReader();
//         rdr.addEventListener('load', function () {
//            try {
//               window.Android.GM_setValue(configStoreName, JSON.parse(rdr.result));
//               alert('Settings imported');
//               location.reload();
//            }
//            catch (err) {
//               alert(`Error parsing settings\n${err.name}: ${err.message}`);
//            }
//         });
//         rdr.addEventListener('error', error => alert('Error loading file\n' + rdr?.error || error));
//         rdr.readAsText(f.files[0]);
//      });
//      document.body.append(f);
//      f.click();
//      f.remove();
//   });
//   GM_registerMenuCommand('Export settings', () => {
//      let d = document.createElement('a');
//      d.style.display = 'none';
//      d.download = 'nova-settings.json';
//      d.href = 'data:text/plain;charset=utf-8,' + encodeURIComponent(JSON.stringify(user_settings));
//      document.body.append(d);
//      d.click();
//      d.remove();
//   });
}
function insertSettingButton() {
   NOVA.waitSelector('#masthead #end')
      .then(menu => {
         const
            titleMsg = 'Nova Settings',
            a = document.createElement('a'),
            SETTING_BTN_ID = 'nova_settings_button';
         a.id = SETTING_BTN_ID;
         a.href = configPage + '?tabs=tab-plugins';
         a.target = '_blank';
         a.innerHTML =
            `<yt-icon-button class="style-scope ytd-button-renderer style-default size-default">
               <svg viewBox="-4 0 20 16">
                  <radialGradient id="nova-gradient" gradientUnits="userSpaceOnUse" cx="6" cy="22" r="18.5">
                     <stop class="nova-gradient-start" offset="0"/>
                     <stop class="nova-gradient-stop" offset="1"/>
                  </radialGradient>
                  <g fill="deepskyblue">
                     <polygon points="0,16 14,8 0,0"/>
                  </g>
               </svg>
            </yt-icon-button>`;
         a.addEventListener('click', () => {
            setTimeout(() => document.body.click(), 200);
         });
         a.title = titleMsg;
         const tooltip = document.createElement('tp-yt-paper-tooltip');
         tooltip.className = 'style-scope ytd-topbar-menu-button-renderer';
         tooltip.textContent = titleMsg;
         a.appendChild(tooltip);
         menu.prepend(a);
         NOVA.css.push(
            `#${SETTING_BTN_ID}[tooltip]:hover:after {
               position: absolute;
               top: 50px;
               transform: translateX(-50%);
               content: attr(tooltip);
               text-align: center;
               min-width: 3em;
               max-width: 21em;
               white-space: nowrap;
               overflow: hidden;
               text-overflow: ellipsis;
               padding: 1.8ch 1.2ch;
               border-radius: 0.6ch;
               background-color: #616161;
               box-shadow: 0 1em 2em -0.5em rgb(0 0 0 / 35%);
               color: #fff;
               z-index: 1000;
            }
            #${SETTING_BTN_ID} {
               position: relative;
               opacity: .3;
               transition: opacity .3s ease-out;
            }
            #${SETTING_BTN_ID}:hover {
               opacity: 1 !important;
            }
            #${SETTING_BTN_ID} path,
            #${SETTING_BTN_ID} polygon {
               fill: url(#nova-gradient);
            }
            #${SETTING_BTN_ID} .nova-gradient-start,
            #${SETTING_BTN_ID} .nova-gradient-stop {
               transition: .6s;
               stop-color: #7a7cbd;
            }
            #${SETTING_BTN_ID}:hover .nova-gradient-start {
               stop-color: #0ff;
            }
            #${SETTING_BTN_ID}:hover .nova-gradient-stop {
               stop-color: #0095ff;
               
            }`);
      });
}
function _pluginsCaptureException({ trace_name, err_stack, confirm_msg, app_ver }) {
}
