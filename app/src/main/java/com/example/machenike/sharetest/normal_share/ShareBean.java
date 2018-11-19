package com.example.machenike.sharetest.normal_share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * author 刘鉴钊
 * create at 2018/8/14
 * description 支持分享文本跟网页
 */
public class ShareBean {
    /**
     * 分享标题
     */
    private String title;
    /**
     * 分享描述
     */
    private String summary;

    /**
     * 分享链接
     */
    private String url;

    /**
     * 分享的图片，resource
     */
    private int icon_res;

    /**
     * 分享的图片，bitmap
     */
    private Bitmap shareBitmap;

    /**
     * 分享的图片，url
     */
    private String pic_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIcon_res() {
        return icon_res;
    }

    public void setIcon_res(int icon_res) {
        this.icon_res = icon_res;
    }

    public Bitmap getShareBitmap() {
        return shareBitmap;
    }

    public void setShareBitmap(Bitmap shareBitmap) {
        this.shareBitmap = shareBitmap;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }
}
