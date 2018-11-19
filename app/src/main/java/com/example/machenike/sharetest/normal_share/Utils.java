package com.example.machenike.sharetest.normal_share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * author 刘鉴钊
 * create at 2018/11/13
 * description
 */
public class Utils {


    private Utils() {
    }

    /**
     * bitmap转byte[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("bitmap为空");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        //获取sd卡缓存路径，项目删除时数据不会保留/storage/emulated/0/Android/data/com.example.machenike.sharetest/cache/
        String savePath = context.getExternalCacheDir() + File.separator;
        File filePic = null;
        try {
            //获取当前版本号作为缓存图片名
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            filePic = new File(savePath + versionName + ".jpg");
            if (!filePic.exists()) {
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return filePic.getAbsolutePath();
    }


    /**
     * 矩阵压缩图片，宽高各压缩一般，内存会缩减至1/4
     *
     * @param bmp
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }


    /**
     * 根据图片的网络url获取bitmap，需要在子线程执行
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromUrl(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            // 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }


}