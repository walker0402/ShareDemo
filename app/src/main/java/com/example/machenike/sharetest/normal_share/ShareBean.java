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
     * 分享类型， 1 纯文本，2 链接， 3 图片
     */
    public final int type;

    public ShareBean(int type) {
        this.type = type;
    }

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
     * 分享的图片缩略图，resource
     */
    private int thumbRes;

    /**
     * 分享的图片缩略图，bitmap
     */
    private Bitmap thumbBitmap;

    /**
     * 分享的图片缩略图，url
     */
    private String thumbPicUrl;

    /**
     * 分享大图
     */
    private Bitmap pictureBitmap;
    private String pictureUrl;
    private int pictureRes;


    public void setPicture(Bitmap picture) {
        this.pictureBitmap = picture;
    }

    public void setPicture(String picture) {
        this.pictureUrl = picture;
    }

    public void setPicture(int picture) {
        this.pictureRes = picture;
    }

    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public int getPictureRes() {
        return pictureRes;
    }

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

    public void setThumb(int thumbRes) {
        this.thumbRes = thumbRes;
    }

    public void setThumb(Bitmap bitmap) {
        this.thumbBitmap = bitmap;
    }

    public void setThumb(String url) {
        this.thumbPicUrl = url;
    }

    public int getThumbRes() {
        return thumbRes;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public String getThumbPicUrl() {
        return thumbPicUrl;
    }


}
