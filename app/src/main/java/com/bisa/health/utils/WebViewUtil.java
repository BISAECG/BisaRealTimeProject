package com.bisa.health.utils;

import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2018/8/28.
 */

public class WebViewUtil {

    public static  WebSettings buildSetting(final WebView webView){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        return webSettings;
    }


    public static  void build(final WebView webView){
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // android 6.0 以下通过title获取
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
//                        view.loadUrl("about:blank");// 避免出现默认的错误界面
//                        view.loadUrl(mErrorUrl);

                    }
                }
            }
        });

        //webView.loadUrl("https://192.168.1.137:8443/health-appserver/l/Calendar",extraHeaders);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                webView.loadUrl(url);
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);

                // 这个方法在6.0才出现
                int statusCode = errorResponse.getStatusCode();
                System.out.println("onReceivedHttpError code = " + statusCode);
                if (404 == statusCode || 500 == statusCode) {
                    //view.loadUrl("about:blank");// 避免出现默认的错误界面
                    // view.loadUrl(mErrorUrl);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 断网或者网络连接超时
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                    // view.loadUrl("about:blank"); // 避免出现默认的错误界面
                    view.loadUrl("file:///android_asset/mobile/html/bisserve/try.html");

                }
            }


        });

    }
}
