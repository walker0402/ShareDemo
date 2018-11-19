package com.example.machenike.sharetest.normal_share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.machenike.sharetest.R;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
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
public enum ShareUtils {
    INSTANCE;

    private ShareDialog shareDialog;

    //目前不知道有没有必要提供回调重写给调用者，如果不需要提供，单利内只需要实例化一次回调，而不用每次都置空又new一次
    private ShareListener mCallBack;

    private ShareAction action;


    /**
     * @param context
     * @param bean
     * @param callback 如果不实现，传null
     * @return
     */
    public ShareUtils init(final Activity context, final ShareBean bean, ShareListener callback) {
        mCallBack = callback;
        shareDialog = new ShareDialog(context, new ShareDialog.OnShareClickListener() {
            @Override
            public void onClick(PlatForm platForm) {
                action = new ShareAction(context).setPlatForm(platForm).setShareData(bean).setCallBack(checkCallBack());
                action.share();
            }
        });
        return this;
    }

    public ShareAction getAction() {
        if (action == null) {
            throw new NullPointerException("ShareUtils must init");
        }
        return action;
    }

    public void share() {
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
            //这条分享消息被好友点击后的跳转URL。
            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareBean.getUrl());
            //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_ SUMMARY不能全为空，最少必须有一个是有值的。
            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, mShareBean.getTitle());
            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareBean.getSummary());

            //QQ分享图片只能分享手机本地图片或者网络图片
            if (null != mShareBean.getShareBitmap()) {
                //如果bitmap不是null，存到本地后取出
                String filePath = Utils.saveBitmap(mContext, mShareBean.getShareBitmap());
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
            } else if (0 != mShareBean.getIcon_res()) {
                String filePath = Utils.saveBitmap(mContext, Utils.compressBitmap(BitmapFactory.decodeResource(mContext.getResources(), mShareBean.getIcon_res())));
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
            } else if (!TextUtils.isEmpty(mShareBean.getPic_url())) {
                //否则查看是否有设置网络图片url
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareBean.getPic_url());
            } else {
                String filePath = Utils.saveBitmap(mContext, Utils.compressBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher)));
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

            WXMediaMessage msg;
            if (TextUtils.isEmpty(mShareBean.getUrl())) {
                //分享文本
                WXTextObject textObject = new WXTextObject();
                textObject.text = mShareBean.getTitle();
                msg = new WXMediaMessage(textObject);
            } else {
                //有分享url，分享网页
                WXWebpageObject webPageObject = new WXWebpageObject();
                webPageObject.webpageUrl = mShareBean.getUrl();
                msg = new WXMediaMessage(webPageObject);

                //缩略图
                //先直接取mShareBean里的bitmap
                if (null != mShareBean.getShareBitmap()) {
                    bitmap = Utils.compressBitmap(mShareBean.getShareBitmap());
                } else if (0 != mShareBean.getIcon_res()) {
                    //没有则取设置的资源图
                    bitmap = Utils.compressBitmap(BitmapFactory.decodeResource(mContext.getResources(), mShareBean.getIcon_res()));
                } else if (null != mShareBean.getPic_url()) {
                    //如果有设置网络图片，等待网络图片加载返回，否则等待
                    //声明一个闭锁数
                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bitmap = Utils.getBitmapFromUrl(mShareBean.getPic_url());
                            countDownLatch.countDown();
                        }
                    }).start();
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //直接取ic_launcherd作为bitmap
                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
                }
                msg.thumbData = Utils.bitmapToByteArray(bitmap);
            }

            msg.title = mShareBean.getTitle();
            msg.description = mShareBean.getSummary();

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
            if (TextUtils.isEmpty(mShareBean.getUrl())) {
                //分享文本
                weiboMessage.textObject = getTextObj();
            } else {
                weiboMessage.mediaObject = getWebpageObj();
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

        /**
         * 微博分享网页
         *
         * @return
         */
        Bitmap bitmap = null;

        private WebpageObject getWebpageObj() {
            final WebpageObject mediaObject = new WebpageObject();
            mediaObject.identify = Utility.generateGUID();
            mediaObject.title = mShareBean.getTitle();
            mediaObject.description = mShareBean.getSummary();
            mediaObject.actionUrl = mShareBean.getUrl();

            if (null != mShareBean.getShareBitmap()) {
                bitmap = Utils.compressBitmap(mShareBean.getShareBitmap());
            } else if (0 != mShareBean.getIcon_res()) {
                //没有则取设置的资源图
                bitmap = Utils.compressBitmap(BitmapFactory.decodeResource(mContext.getResources(), mShareBean.getIcon_res()));
            } else if (null != mShareBean.getPic_url()) {
                //如果有设置网络图片，等待网络图片加载返回，否则等待
                //声明一个闭锁数
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = Utils.getBitmapFromUrl(mShareBean.getPic_url());
                        countDownLatch.countDown();
                    }
                }).start();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //直接取ic_launcherd作为bitmap
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
            }
            mediaObject.setThumbImage(bitmap);
            return mediaObject;
        }

        //微博分享回调
        @Override
        public void onWbShareSuccess() {
            mShareListener.onSuccess(PlatForm.SINA);
        }

        @Override
        public void onWbShareCancel() {
            mShareListener.onCancel(PlatForm.SINA);
        }

        @Override
        public void onWbShareFail() {
            mShareListener.onError(PlatForm.SINA);
        }

        //QQ分享回调
        @Override
        public void onComplete(Object o) {
            mShareListener.onSuccess(PlatForm.QQ);
        }

        @Override
        public void onError(UiError uiError) {
            mShareListener.onError(PlatForm.QQ);
        }

        @Override
        public void onCancel() {
            mShareListener.onCancel(PlatForm.QQ);
        }


    }

}
