package com.example.machenike.sharetest.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.machenike.sharetest.normal_share.WeChatConstants;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;



public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        String result = "";

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "微信发送成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "微信发送取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "微信发送被拒绝";
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = "微信不支持错误";
                break;
            default:
                result = "微信发送返回";
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (api == null) {
            api = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID);
        }
        api.handleIntent(intent, this);
    }
}