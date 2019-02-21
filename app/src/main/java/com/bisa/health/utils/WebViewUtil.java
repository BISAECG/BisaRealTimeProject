package com.bisa.health.utils;

import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Administrator on 2018/8/28.
 */

public class WebViewUtil {

    public static  WebSettings buildSetting(final WebView webView){
        WebSettings webSettings = webView.getSettings();
        /* 设置支持Js,必须设置的,不然网页基本上不能看 */
        webSettings.setJavaScriptEnabled(true);
        /* 设置WebView是否可以由JavaScript自动打开窗口，默认为false，通常与JavaScript的window.open()配合使用。*/
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        /* 设置为true表示支持使用js打开新的窗口 */
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        /* 大部分网页需要自己保存一些数据,这个时候就的设置下面这个属性 */
        webSettings.setDomStorageEnabled(true);
        /* 设置为使用webview推荐的窗口 */
        webSettings.setUseWideViewPort(true);
        /* 设置网页自适应屏幕大小 ---这个属性应该是跟上面一个属性一起用 */
        webSettings.setLoadWithOverviewMode(true);
        /* HTML5的地理位置服务,设置为true,启用地理定位 */
        webSettings.setGeolocationEnabled(true);
        /* 设置是否允许webview使用缩放的功能,我这里设为false,不允许 */
        webSettings.setBuiltInZoomControls(false);
        /* 提高网页渲染的优先级 */
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        return webSettings;
    }

}
