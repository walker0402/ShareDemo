package com.example.machenike.sharetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.machenike.sharetest.normal_share.ShareBean;
import com.example.machenike.sharetest.normal_share.ShareUtils;
import com.tencent.tauth.Tencent;

/**
 * author 刘鉴钊
 * create at 2018/11/12
 * description
 */
public class MainActivity extends AppCompatActivity {

    private ShareUtils utils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareBean shareBean = new ShareBean();
                shareBean.setTitle("测试title");
                shareBean.setSummary("哈哈哈");
                shareBean.setUrl("https://www.jianshu.com/u/df76d00c5d47");
//                shareBean.setIcon_res(R.mipmap.ic_launcher);
//                shareBean.setShareBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.local));
                shareBean.setPic_url("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1371225715,3629055554&fm=26&gp=0.jpg");
                utils = new ShareUtils();
                utils.init(MainActivity.this, shareBean, null).share();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getComponent() != null) {
            if (data.getComponent().getClassName().equals("com.sina.weibo.sdk.share.WbShareTransActivity")) {
                utils.getAction().wbShareHandler.doResultIntent(data, utils.getAction());
            } else if (data.getComponent().getClassName().equals("com.tencent.connect.common.AssistActivity")) {
                Tencent.onActivityResultData(requestCode, resultCode, data, utils.getAction());
            }
        }
    }
}
