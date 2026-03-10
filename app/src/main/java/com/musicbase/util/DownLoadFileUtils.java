package com.musicbase.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;
import com.orhanobut.logger.Logger;

import java.io.File;
/**
 * Description: 使用okgo下载文件 zip video music txt image
 * Created by wcystart on 2018/7/4.
 */
public class DownLoadFileUtils {
    private static String mBasePath; //本地文件存储的完整路径  /storage/emulated/0/book/恰似寒光遇骄阳.txt
    /**
     *
     * @param context 上下文
     * @param fileUrl 下载完整url
     * @param destFileDir  SD路径
     * @param destFileName  文件名
     */
    public static void downloadFile(final Context context, String fileUrl, String destFileDir, String destFileName){
//        String  mDestFileName =destFileName+mFileRelativeUrl.substring(mFileRelativeUrl.lastIndexOf("."),mFileRelativeUrl.length());
        OkGo.<File>get(fileUrl).tag(context).execute(new FileCallback(destFileDir,destFileName) { //文件下载时指定下载的路径以及下载的文件的名称
            @Override
            public void onStart(Request<File, ? extends Request> request) {
                super.onStart(request);
                Logger.e("开始下载文件"+"DDDDD");
            }

            @Override
            public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                Logger.e("下载文件成功"+"DDDDD"+response.body().length());
                mBasePath=response.body().getAbsolutePath();
             }

            @Override
            public void onFinish() {
                super.onFinish();
                Logger.e("下载文件完成"+"DDDDD");
                SPUtility.putSPString(context,"localPath",mBasePath);
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<File> response) {
                super.onError(response);
                Logger.e("下载文件出错"+"DDDDD"+response.message());

            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                float  dLProgress= progress.fraction;
                Logger.e("文件下载的进度"+"DDDDD"+dLProgress);
            }
        });
    }

    //拼接一个本地的完整的url 供下载文件时传入一个本地的路径
    private static final String mSDPath = Environment.getExternalStorageDirectory().getPath();
    //分类别路径
    private static String mClassifyPath=null;
    public static String customLocalStoragePath(String differentName){
        File basePath=new File(mSDPath); // /storage/emulated/0
        mClassifyPath=mSDPath+"/"+differentName+"/";  //如果传来的是 book 拼接就是 /storage/emulated/0/book/
                                                    //如果传来的是game  那拼接就是 /storage/emulated/0/game/
        if(!basePath.exists()){
            basePath.mkdirs();
            System.out.println("文件夹创建成功");
        }

        return mClassifyPath;
    }

}
