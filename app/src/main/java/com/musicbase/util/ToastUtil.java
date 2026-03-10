package com.musicbase.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void showLong(Context mContext, String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context mContext, String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void closeToast() {

    }
}