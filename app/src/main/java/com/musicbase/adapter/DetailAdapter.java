package com.musicbase.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.download2.db.DataBaseFiledParams;
import com.musicbase.download2.db.DataBaseHelper;
import com.musicbase.download2.entity.DocInfo;
import com.musicbase.entity.DetailBean;
import com.musicbase.util.ActivityUtils;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class DetailAdapter extends BaseQuickAdapter<DetailBean.DataBean.ResourceListBean, BaseViewHolder> {

    private Context context;
    private int autoId = -1;
    private boolean isSearch = false;

    public DetailAdapter(Context context, @LayoutRes int layoutResId, @Nullable List<DetailBean.DataBean.ResourceListBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    public DetailAdapter(Context context, @LayoutRes int layoutResId, @Nullable List<DetailBean.DataBean.ResourceListBean> data,boolean isSearch) {
        super(layoutResId, data);
        this.context = context;
        this.isSearch = isSearch;
    }

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    @Override
    protected void convert(BaseViewHolder helper, DetailBean.DataBean.ResourceListBean item) {
        int imageId = 0;
        switch (item.getResourceType()) {
            case "cloudVideo":
            case "mp4":
                imageId = R.mipmap.icon_video;
                break;
            case "cloudAudio":
            case "mp3":
            case "MP3":
                imageId = R.mipmap.icon_audio;
                break;
            case "testPaper":
            case "proto_testPaper":
                imageId = R.mipmap.icon_exercises;
                break;
            case "courseware":
            case "url":
            case "pdf":
                imageId = R.mipmap.icon_courseware;
                break;
        }
        if (item.getIsFolder() == 1) {
            imageId = R.mipmap.icon_folder;
            helper.setVisible(R.id.nodownload, false);
        }
        helper.setImageResource(R.id.im, imageId);
        helper.setText(R.id.content, item.getResourceFileName());
        if(autoId!=-1&&item.getResourceId()== autoId){
            helper.setBackgroundColor(R.id.layout,mContext.getResources().getColor(R.color.grey_ef));
        }else{
            helper.setBackgroundColor(R.id.layout,mContext.getResources().getColor(R.color.transparent));
        }
        if(isSearch){
            helper.setVisible(R.id.tv_search,true);
            helper.setText(R.id.tv_search,item.getCourseName());
        }else{
            helper.setVisible(R.id.tv_search,false);
        }
        if(!isSearch){
            if (item.getIsPay() == 1 && item.getCurrentPrice().equals("0.00")) {
                helper.setVisible(R.id.free, false);

            } else if (item.getIsPay() == 1) {
                helper.setVisible(R.id.free, true);
                TextView free = helper.getView(R.id.free);
                free.setText("  ¥ " + item.getCurrentPrice() + "  ");

            } else {
                helper.setVisible(R.id.free, false);

                if(item.getResourceType().equals("proto_testPaper")){
                    String[] saveName = item.getTestPaperInfo().getPackSavePath().split("/");
                    if (ActivityUtils.isExistByName(context, item.getResourceId() + "", saveName[2])) {
                        helper.setVisible(R.id.nodownload, false);
                    } else{
                        helper.setVisible(R.id.nodownload, true);
                    }
                }else {
                    if (ActivityUtils.isExistByName(context, item.getResourceId() + "", item.getResourceSaveName())) {
                        helper.setVisible(R.id.nodownload, false);
                    } else {
                        if ((item.getResourceType().equals("testPaper") || item.getResourceType().equalsIgnoreCase("mp3") || item.getResourceType().equalsIgnoreCase("mp4")|| item.getResourceType().equals("courseware")) && TextUtils.isEmpty(item.getOnlineLinks()))
                            helper.setVisible(R.id.nodownload, true);
                        else
                            helper.setVisible(R.id.nodownload, false);
                    }
                }
                helper.setText(R.id.progress, "");
                DataBaseHelper dbhelper = new DataBaseHelper(context);
                List<DocInfo> infos = dbhelper.getInfo2(item.getResourceId() + "");
                String saveName = null;
                if(item.getResourceType().equals("proto_testPaper")){
                    String[] ss = item.getTestPaperInfo().getPackSavePath().split("/");
                    saveName = ss[2];
                }else
                    saveName = item.getResourceSaveName();
                for (DocInfo docInfo : infos) {
                    if (saveName.equals(docInfo.getName())) {
                        if (docInfo.getStatus() == DataBaseFiledParams.PAUSING) {
                            helper.setText(R.id.progress, "暂停");
                        } else if (docInfo.getStatus() == DataBaseFiledParams.WAITING) {
                            helper.setText(R.id.progress, "等待中");
                        } else if (docInfo.getStatus() == DataBaseFiledParams.FAILED) {
                            helper.setText(R.id.progress, "服务器忙");
                        } else {
                            helper.setText(R.id.progress, docInfo.getDownloadProgress() + "%");
                            if (docInfo.getDownloadProgress() == 100) {
                                helper.setText(R.id.progress, "");
                            }
                        }
                    }
                }
            }
        }
    }
}


