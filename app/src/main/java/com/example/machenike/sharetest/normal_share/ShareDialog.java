package com.example.machenike.sharetest.normal_share;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.machenike.sharetest.R;


/**
 * author 刘鉴钊
 * create at 2018/8/13
 * description
 */
public class ShareDialog extends Dialog implements View.OnClickListener {

    private OnShareClickListener onShareClickListener;

    private Context mContext;

    public ShareDialog(@NonNull Context context, @NonNull OnShareClickListener onShareClickListener) {
        super(context, R.style.AppDialog_Bottom);
        this.onShareClickListener = onShareClickListener;
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        setContentView(view);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
        setCanceledOnTouchOutside(false);
        setClickListener();
    }

    private void setClickListener() {
        findViewById(R.id.iv_wechat).setOnClickListener(this);
        findViewById(R.id.iv_wxcircle).setOnClickListener(this);
        findViewById(R.id.iv_sina).setOnClickListener(this);
        findViewById(R.id.iv_qq).setOnClickListener(this);
        findViewById(R.id.tv_share_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_wechat) {
            onShareClickListener.onClick(PlatForm.WECHAT);
        } else if (id == R.id.iv_wxcircle) {
            onShareClickListener.onClick(PlatForm.WECHAT_CIRCLE);
        } else if (id == R.id.iv_sina) {
            onShareClickListener.onClick(PlatForm.SINA);
        } else if (id == R.id.iv_qq) {
            onShareClickListener.onClick(PlatForm.QQ);
        }
        dismiss();
    }

    public interface OnShareClickListener {
        void onClick(PlatForm platForm);
    }
}
