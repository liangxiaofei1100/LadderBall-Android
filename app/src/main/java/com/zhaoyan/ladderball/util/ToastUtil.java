package com.zhaoyan.ladderball.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static final int DURATION = 1500;
    private static Runnable r = new Runnable() {
        public void run() {
            if (mToast != null) {
                mToast.cancel();
                // Release mToast because mToast hold a context object and the
                // context object will not be will not be released since mToast
                // is static.
                mToast = null;
            }
        }
    };

    public static void showToast(Context context, String text) {
        // Check context null point.
        if (context == null) {
            return;
        }

        mHandler.removeCallbacks(r);
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        mToast.show();
        mHandler.postDelayed(r, DURATION);
    }

    public static void showToast(Context context, int resId) {
        // Check context null point.
        if (context == null) {
            return;
        }
        showToast(context, context.getResources().getString(resId));
    }

    public static void showNetworkFailToast(Context context) {
        if (context == null) {
            return;
        }
        showToast(context, "无法连接到服务器，请重试");
    }
}