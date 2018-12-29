package com.example.machenike.sharetest;

import android.app.Application;

import com.example.machenike.sharetest.normal_share.WeChatConstants;
import com.example.machenike.sharetest.normal_share.WeiBoConstants;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * author 刘鉴钊
 * create at 2018/11/12
 * description
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);


        WbSdk.install(this,new AuthInfo(this, WeiBoConstants.APP_KEY, WeiBoConstants.REDIRECT_URL, WeiBoConstants.SCOPE));

        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID, true);
        iwxapi.registerApp(WeChatConstants.APP_ID);
    }
}
