package com.alier.com.androidtools.ui.webView;


import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alier.com.androidtools.R;
import com.alier.com.commons.BaseActivity;

/**
 * @author 作者 : gavin_fool
 * @version 1.0
 * @date 创建时间：2016年12月9日 下午2:35:20
 */
public class SuperMapOnLine extends BaseActivity {

    private WebView webView;

    @Override
    public void init() {
        setContentView(R.layout.supermap_online);
        webView = (WebView) this.findViewById(R.id.myWebView);
        webView.getSettings().setJavaScriptEnabled(true);
//		webView.loadUrl("http://itest.supermapol.com/apps/viewer/1018/share?key=xSZdDPsfa5c8XRgRxcT4sCSJ");
        webView.loadUrl("http://192.168.1.179:8080/wirelessdata/static/requestUp/problemRequest.jsp");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }
        });
    }

    @Override
    public void exec() {
    }
}
