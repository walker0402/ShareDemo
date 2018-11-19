package com.example.machenike.sharetest;

import android.app.Application;

import com.example.machenike.sharetest.normal_share.WeChatConstants;
import com.example.machenike.sharetest.normal_share.WeiBoConstants;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * author 刘鉴钊
 * create at 2018/11/12
 * description
 */
public class App extends Application {

private IWXAPI iwxapi;

    @Override
    public void onCreate() {
        super.onCreate();
        WbSdk.install(this,new AuthInfo(this, WeiBoConstants.APP_KEY, WeiBoConstants.REDIRECT_URL, WeiBoConstants.SCOPE));

        iwxapi = WXAPIFactory.createWXAPI(this,WeChatConstants.APP_ID,true);
        iwxapi.registerApp(WeChatConstants.APP_ID);
    }
}
