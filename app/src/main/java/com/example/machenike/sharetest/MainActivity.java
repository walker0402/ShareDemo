package com.example.machenike.sharetest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.example.machenike.sharetest.normal_share.ShareBean;
import com.example.machenike.sharetest.normal_share.ShareUtils;
import com.example.machenike.sharetest.normal_share.Utils;
import com.tencent.tauth.Tencent;

/**
 * author 刘鉴钊
 * create at 2018/11/12
 * description
 */
public class MainActivity extends AppCompatActivity {

    public ShareUtils utils;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareBean shareBean = new ShareBean(3);
                shareBean.setTitle("测试title");
                shareBean.setSummary("哈哈哈");
                shareBean.setUrl("https://www.jianshu.com/u/df76d00c5d47");
                shareBean.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.ee));
//                shareBean.setThumb("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566302373047&di=3f87d74abdf1a9aa45eed9a90a8c51a2&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201607%2F13%2F20160713110827_vyiPR.thumb.700_0.png");
                utils = new ShareUtils(MainActivity.this);
                utils.share(shareBean);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmapFromUrl = Utils.getBitmapFromUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561353916626&di=6d5fc5c5a72c6e07fd00f64ca88252a8&imgtype=0&src=http%3A%2F%2Fwww.chinadaily.com.cn%2Fhqzx%2Fimages%2Fattachement%2Fjpg%2Fsite385%2F20120924%2F00221918200911ca40e52b.jpg");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.iv_bm).setBackground(new BitmapDrawable(bitmapFromUrl));
                    }
                });
            }
        }).start();


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
