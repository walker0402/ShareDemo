package com.example.machenike.sharetest.normal_share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.machenike.sharetest.R;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.concurrent.CountDownLatch;


/**
 * author 刘鉴钊
 * create at 2018/8/14
 * description 分享工具类
 * 微博分享需要在application里初始化
 * WbSdk.install(this,new AuthInfo(this, WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE));
 * 微信分享需要在程序入口activity或者application里初始化
 * iwxapi = WXAPIFactory.createWXAPI(this,WeChatConstants.APP_ID,true);
 * iwxapi.registerApp(WeChatConstants.APP_ID);
 * <p>
 * 微信的缩略图大小不可大于32k，无效时控制台会输出，checkArgs fail, thumbData is invalid的错误，请控制资源图大小
 */
public class ShareUtils {

    private ShareDialog shareDialog;

    //目前不知道有没有必要提供回调重写给调用者，如果不需要提供，单利内只需要实例化一次回调，而不用每次都置空又new一次
    private ShareListener mCallBack;

    private ShareAction action;

    private Activity mContext;

    public ShareUtils(final Activity context) {
        this.mContext = context;
        action = new ShareAction(context);
    }

    public ShareUtils setCallBack(ShareListener callback) {
        this.mCallBack = callback;
        return this;
    }

    public ShareAction getAction() {
        if (action == null) {
            throw new NullPointerException("ShareUtils must init");
        }
        return action;
    }

    public void share(final ShareBean bean) {
        shareDialog = new ShareDialog(mContext, new ShareDialog.OnShareClickListener() {
            @Override
            public void onClick(PlatForm platForm) {
                action.setPlatForm(platForm).setShareData(bean).setCallBack(checkCallBack());
                action.share();
            }
        });
        shareDialog.show();

    }

    private ShareListener checkCallBack() {
        if (mCallBack != null) {
            return mCallBack;
        } else {
            return new ShareListener() {
                @Override
                public void onSuccess(PlatForm platForm) {
                    Log.e("walker", platForm + "分享成功");
                }

                @Override
                public void onError(PlatForm platForm) {
                    Log.e("walker", platForm + "onError");
                }

                @Override
                public void onCancel(PlatForm platForm) {
                    Log.e("walker", platForm + "onCancel");
                }
            };
        }
    }

    public interface ShareListener {
        void onSuccess(PlatForm platForm);

        void onError(PlatForm platForm);

        void onCancel(PlatForm platForm);
    }

    public static class ShareAction implements WbShareCallback, IUiListener {

        private PlatForm mPlatForm;
        private Activity mContext;
        private ShareListener mShareListener;
        private ShareBean mShareBean;
        public WbShareHandler wbShareHandler;

        ShareAction(Activity context) {
            this.mContext = context;
        }

        ShareAction setPlatForm(PlatForm platForm) {
            this.mPlatForm = platForm;
            return this;
        }

        ShareAction setCallBack(ShareListener shareListener) {
            this.mShareListener = shareListener;
            return this;
        }

        ShareAction setShareData(ShareBean bean) {
            this.mShareBean = bean;
            return this;
        }

        void share() {

            if (mPlatForm == null) {
                throw new NullPointerException("请设置需要分享的平台");
            }
            if (mShareBean == null) {
                throw new NullPointerException("请设置需要分享的数据");
            }
            switch (mPlatForm) {
                case SINA:
                    shareToSina();
                    break;
                case WECHAT:
                case WECHAT_CIRCLE:
                    shareToWX(mPlatForm);
                    break;
                case QQ:
                    shareToQQ();
                    break;
            }

        }

        //qq分享
        private void shareToQQ() {
            Tencent tencent = Tencent.createInstance(QQConstants.APP_ID, mContext.getApplicationContext());

            if (!tencent.isQQInstalled(mContext)) {
                Toast.makeText(mContext, "未安装QQ客户端", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = new Bundle();
            if (mShareBean.type == 1) {
                //文字
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            } else if (mShareBean.type == 2) {
                //链接
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                //这条分享消息被好友点击后的跳转URL。
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareBean.getUrl());
                //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_ SUMMARY不能全为空，最少必须有一个是有值的。
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, mShareBean.getTitle());
                bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareBean.getSummary());
                //QQ分享图片只能分享手机本地图片或者网络图片,此处只是缩略图
                if (null != mShareBean.getThumbBitmap()) {
                    //如果bitmap不是null，存到本地后取出
                    String filePath = Utils.saveBitmap(mContext, mShareBean.getThumbBitmap());
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
                } else if (0 != mShareBean.getThumbRes()) {
                    String filePath = Utils.saveBitmap(mContext, Utils.compressBitmap(BitmapFactory.decodeResource(mContext.getResources(), mShareBean.getThumbRes())));
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
                } else if (!TextUtils.isEmpty(mShareBean.getThumbPicUrl())) {
                    //否则查看是否有设置网络图片url
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareBean.getThumbPicUrl());
                } else {
                    String filePath = Utils.saveBitmap(mContext, Utils.compressBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher)));
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
                }
            } else if (mShareBean.type == 3) {
                //图片
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                String filePath = Utils.saveBitmap(mContext, Utils.compressBitmap(mShareBean.getPictureBitmap()));
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
            }


            tencent.shareToQQ(mContext, bundle, this);

        }

        //微信分享,分享的缩略图不可以超过32k
        private void shareToWX(PlatForm platForm) {
            IWXAPI api = WXAPIFactory.createWXAPI(mContext, WeChatConstants.APP_ID);

            if (!api.isWXAppInstalled()) {
                Toast.makeText(mContext, "未安装微信客户端", Toast.LENGTH_SHORT).show();
                return;
            }

            WXMediaMessage msg = null;
            if (mShareBean.type == 1) {
                //分享文本
                WXTextObject textObject = new WXTextObject();
                textObject.text = mShareBean.getTitle();
                msg = new WXMediaMessage(textObject);
            } else if (mShareBean.type == 2) {
                //分享网页
                WXWebpageObject webPageObject = new WXWebpageObject();
                webPageObject.webpageUrl = mShareBean.getUrl();
                msg = new WXMediaMessage(webPageObject);
                msg.title = mShareBean.getTitle();
                msg.description = mShareBean.getSummary();


                msg.thumbData = Utils.bitmapToByteArray(bitmap = Utils.compressBitmap(getThumbBitmap(), 32));
            } else if (mShareBean.type == 3) {
                //分享图片
                if (null == mShareBean.getPictureBitmap()) {
                    throw new RuntimeException("图片分享请设置pictures");
                }
                Bitmap bmp = Utils.compressBitmap(mShareBean.getPictureBitmap(), 10 * 1024);
                WXImageObject imgObj = new WXImageObject(bmp);
                msg = new WXMediaMessage(imgObj);
                //设置缩略图
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2, true);
                msg.thumbData = Utils.bitmapToByteArray(bitmap = Utils.compressBitmap(thumbBmp, 32));
            }

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.message = msg;
            //transaction用于唯一标志一个请求，用当前时间表示
            req.transaction = System.currentTimeMillis() + "";
            if (platForm == PlatForm.WECHAT) {
                req.scene = SendMessageToWX.Req.WXSceneSession;
            } else if (platForm == PlatForm.WECHAT_CIRCLE) {
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
            }
            api.sendReq(req);
        }

        /**
         * 微博分享
         */
        private void shareToSina() {
            wbShareHandler = new WbShareHandler(mContext);
            wbShareHandler.registerApp();
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
            //根据ShareBean有没有分享链接来判断分享的是文本还是网页
            if (mShareBean.type == 1) {
                //分享文本
                weiboMessage.textObject = getTextObj();
            } else if (mShareBean.type == 2) {
                //网页
                weiboMessage.mediaObject = getWebpageObj();
            } else if (mShareBean.type == 3) {
                //图片
                if (null == mShareBean.getPictureBitmap()) {
                    throw new RuntimeException("图片分享请设置pictures");
                }
                weiboMessage.imageObject = getImageObject();
            }
            wbShareHandler.shareMessage(weiboMessage, false);
        }

        /**
         * 微博分享纯文本
         *
         * @return
         */
        private TextObject getTextObj() {
            TextObject textObject = new TextObject();
            textObject.title = mShareBean.getTitle();
            textObject.text = mShareBean.getSummary();
            textObject.actionUrl = "";
            return textObject;
        }

        //微博分享图片
        private ImageObject getImageObject() {
            ImageObject imageObject = new ImageObject();
            Bitmap bitmap = mShareBean.getPictureBitmap();
            imageObject.setImageObject(bitmap);
            return imageObject;
        }

        /**
         * 微博分享网页
         *
         * @return
         */
        private WebpageObject getWebpageObj() {
            final WebpageObject mediaObject = new WebpageObject();
            mediaObject.identify = Utility.generateGUID();
            mediaObject.title = mShareBean.getTitle();
            mediaObject.description = mShareBean.getSummary();
            mediaObject.actionUrl = mShareBean.getUrl();
            mediaObject.setThumbImage(getThumbBitmap());
            return mediaObject;
        }


        Bitmap bitmap;

        //获取缩略图bitmap
        private Bitmap getThumbBitmap() {
            if (null != mShareBean.getThumbBitmap()) {
                return Utils.compressBitmap(mShareBean.getThumbBitmap());
            } else if (0 != mShareBean.getThumbRes()) {
                //没有则取设置的资源图
                return Utils.compressBitmap(BitmapFactory.decodeResource(mContext.getResources(), mShareBean.getThumbRes()));
            } else if (null != mShareBean.getThumbPicUrl()) {
                //如果有设置网络图片，等待网络图片加载返回，否则等待
                //声明一个闭锁数
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = Utils.getBitmapFromUrl(mShareBean.getThumbPicUrl());
                        countDownLatch.countDown();
                    }
                }).start();
                try {
                    countDownLatch.await();
                    if (null != bitmap) {
                        return bitmap;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //直接取ic_launcherd作为bitmap
                return BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
            }
            return null;
        }

        //微博分享回调
        @Override
        public void onWbShareSuccess() {
            mShareListener.onSuccess(PlatForm.SINA);
            if (mContext != null) {
                mContext = null;
            }
        }

        @Override
        public void onWbShareCancel() {
            mShareListener.onCancel(PlatForm.SINA);
            if (mContext != null) {
                mContext = null;
            }
        }

        @Override
        public void onWbShareFail() {
            mShareListener.onError(PlatForm.SINA);
            if (mContext != null) {
                mContext = null;
            }
        }

        //QQ分享回调
        @Override
        public void onComplete(Object o) {
            mShareListener.onSuccess(PlatForm.QQ);
            if (mContext != null) {
                mContext = null;
            }
        }

        @Override
        public void onError(UiError uiError) {
            mShareListener.onError(PlatForm.QQ);
            if (mContext != null) {
                mContext = null;
            }
        }

        @Override
        public void onCancel() {
            mShareListener.onCancel(PlatForm.QQ);
            if (mContext != null) {
                mContext = null;
            }
        }


    }

}
