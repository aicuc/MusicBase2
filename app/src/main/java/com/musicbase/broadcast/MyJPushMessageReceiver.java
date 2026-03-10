package com.musicbase.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.musicbase.entity.JPushEntity;
import com.musicbase.ui.activity.DetailActivity;
import com.musicbase.ui.activity.LoginActivity;
import com.musicbase.ui.activity.TestActivity;
import com.musicbase.util.TagAliasOperatorHelper;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 * */
public class MyJPushMessageReceiver extends JPushMessageReceiver {

    private static final String TAG = "PushMessageReceiver";

    @Override
    public void onTagOperatorResult(Context context,JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageOpened] "+message);
        try{
            JPushEntity jPushEntity=new Gson().fromJson(message.notificationExtras,JPushEntity.class);
            if (TextUtils.isEmpty(jPushEntity.getCourseId())||Integer.parseInt(jPushEntity.getCourseId())<=0){
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                context.startActivity(intent);
                return;
            }
            //打开自定义的Activity
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("courseId", Integer.parseInt(jPushEntity.getCourseId()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            context.startActivity(intent);
        }catch (Throwable throwable){

        }
    }
}
