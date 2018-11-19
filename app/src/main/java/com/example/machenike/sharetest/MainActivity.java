package com.example.machenike.sharetest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.machenike.sharetest.normal_share.ShareBean;
import com.example.machenike.sharetest.normal_share.ShareUtils;
import com.example.machenike.sharetest.normal_share.Utils;
import com.tencent.tauth.Tencent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.CountDownLatch;

/**
 * author 刘鉴钊
 * create at 2018/11/12
 * description
 */
public class MainActivity extends AppCompatActivity {
    Bitmap bitmapFromUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String absolutePath = this.getExternalCacheDir() + File.separator;
        Log.e("walker", absolutePath);
        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ShareDialog(MainActivity.this,null).show();
                ShareBean shareBean = new ShareBean();
                shareBean.setTitle("测试title");
                shareBean.setSummary("哈哈哈");
                shareBean.setUrl("http://op.open.qq.com/mobile_appinfov2/detail?appid=1107967178");
//                shareBean.setIcon_res(R.mipmap.ic_launcher);
//                shareBean.setShareBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.local));
                shareBean.setPic_url("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1371225715,3629055554&fm=26&gp=0.jpg");
                ShareUtils.INSTANCE.init(MainActivity.this, shareBean, null).share();
            }
        });
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("walker","麻烦等待");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bitmapFromUrl  = Utils.getBitmapFromUrl("http://img15.haotuwu.com:8080/picture/180701/pic4/1.jpg");
                countDownLatch.countDown();


            }
        }).start();

        try {
            countDownLatch.await();
            Log.e("walker","等待结束了");
            findViewById(R.id.iv_bm).setBackgroundDrawable(new BitmapDrawable(bitmapFromUrl));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        findViewById(R.id.iv_bm).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.local)));
        findViewById(R.id.iv_bm2).setBackgroundDrawable(new BitmapDrawable(compressImageToFile(BitmapFactory.decodeResource(getResources(), R.drawable.local), 1)));



//        for (int i = 100; i > 0; i = i - 10) {
//            compressImageToFile(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), i);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getComponent() != null) {
            if (data.getComponent().getClassName().equals("com.sina.weibo.sdk.share.WbShareTransActivity")) {
                ShareUtils.INSTANCE.getAction().wbShareHandler.doResultIntent(data, ShareUtils.INSTANCE.getAction());
            } else if (data.getComponent().getClassName().equals("com.tencent.connect.common.AssistActivity")) {
                Tencent.onActivityResultData(requestCode, resultCode, data, ShareUtils.INSTANCE.getAction());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    public Bitmap compressImageToFile(Bitmap bmp, int quality) { // 0-100 100为不压缩

        Log.e("walker", "quality = " + quality + "压缩前 " + bmp.getByteCount() / 1024);
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        Log.e("walker", "quality =" + quality + "压缩后 " + bitmap.getByteCount() / 1024);
        return bitmap;
    }

}
