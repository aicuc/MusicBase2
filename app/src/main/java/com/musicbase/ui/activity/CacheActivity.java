package com.musicbase.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.musicbase.R;
import com.musicbase.adapter.CacheAdapter;
import com.musicbase.download2.Utils.DownloadManager;
import com.musicbase.download2.db.DataBaseFiledParams;
import com.musicbase.download2.entity.DocInfo;
import com.musicbase.util.ActivityUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CacheActivity extends AppCompatActivity {

    @BindView(R.id.titlelayout_back)
    ImageButton titlelayoutBack;
    @BindView(R.id.titlelayout_title)
    TextView titlelayoutTitle;
    @BindView(R.id.btn_download)
    Button btnDownload;
    @BindView(R.id.btn_done)
    Button btnDone;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    DownloadManager manager;
    List<DocInfo> infos;
    BroadcastReceiver broadcastReciver;
    CacheAdapter adapter;
    private int flag_type;
    private final int DONE_LIST = 0x01;
    private final int DOING_LIST = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        ButterKnife.bind(this);
        initView();
        setListData(DOING_LIST);

    }

    private void setListData(int flag) {
        flag_type = flag;
        if (flag == DOING_LIST) {
            infos = manager.getListDoing();
            btnDownload.setTextColor(getResources().getColor(R.color.red_e61b19));
            btnDone.setTextColor(Color.BLACK);
        }else {
            infos = manager.getListDone();
            btnDownload.setTextColor(Color.BLACK);
            btnDone.setTextColor(getResources().getColor(R.color.red_e61b19));
        }
        Logger.d("size===="+infos.size());
        adapter.setNewData(infos);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        titlelayoutTitle.setText("缓存中心");
        titlelayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CacheAdapter(this, R.layout.item_cache, null);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.iv_cancel){
                    manager.cancel(CacheActivity.this,infos.get(position));
                    infos.remove(position);
                    CacheActivity.this.adapter.notifyDataSetChanged();

                }else if(view.getId() == R.id.iv_status){
                    DocInfo info = infos.get(position);
                    if (info.getStatus() == DataBaseFiledParams.LOADING) {
                        manager.pause(infos.get(position));
                        info.setStatus(DataBaseFiledParams.PAUSING);
                        infos.remove(position);
                        infos.add(position, info);
                        adapter.setNewData(infos);
                        adapter.notifyDataSetChanged();
                    } else {
                        manager.startForActivity(CacheActivity.this,infos.get(position));
                        info.setStatus(DataBaseFiledParams.LOADING);
                        manager.removeDownloadListener();
                        manager.addDownloadListener(new DownloadManager.DownloadListener() {
                            @Override
                            public void onUpdateProgress(DocInfo info) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.test");// action与接收器相同
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("info", info);
                                intent.putExtra("bundle", bundle);
                                intent.putExtra("flag", "update");
                                sendBroadcast(intent);
                                System.out.println("progress----------------"
                                        + info.getDownloadProgress());
                                System.out.println("speed----------------"
                                        + info.getSpeed());
                                System.out.println("name----------------"
                                        + info.getName());
                            }

                            public void onDownloadFailed(DocInfo info) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.test");// action与接收器相同
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("info", info);
                                intent.putExtra("bundle", bundle);
                                intent.putExtra("flag", "failed");
                                sendBroadcast(intent);
                                System.out.println("progress----------------"
                                        + info.getName());
                            }

                            @Override
                            public void onDownloadCompleted(DocInfo info) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.test");// action与接收器相同
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("info", info);
                                intent.putExtra("bundle", bundle);
                                intent.putExtra("flag", "success");
                                sendBroadcast(intent);
                            }
                        });
                        infos.remove(position);
                        infos.add(position, info);
                        adapter.setNewData(infos);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        recyclerview.setAdapter(adapter);

        manager = DownloadManager.getInstance(this);
        broadcastReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.test")) {
                    String flag = intent.getStringExtra("flag");
                    Bundle bundle = intent.getBundleExtra("bundle");
                    DocInfo info = (DocInfo) bundle.getSerializable("info");
                    if (null == info) {
                        return;
                    }
                    if (flag.equals("update")) {
                        if (flag_type == DOING_LIST) {
                            for (int i = 0; i < infos.size(); i++) {
                                if (infos.get(i).getId() == info.getId()) {
                                    infos.remove(i);
                                    infos.add(i, info);
                                    adapter.setNewData(infos);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                        return;
                    } else if (flag.equals("success")) {
                        for (int i = 0; i < infos.size(); i++) {
                            if (infos.get(i).getId() == info.getId()) {
                                infos.remove(i);
                                adapter.setNewData(infos);
                                adapter.notifyDataSetChanged();
                                return;
                            }
                        }
                    } else if (flag.equals("failed")) {
                        ActivityUtils.showToast(CacheActivity.this,
                                info.getBookName() + ":下载失败");
                        manager.cancel(CacheActivity.this, info);
                        infos.remove(info);
                        adapter.setNewData(infos);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                }
            }
        };
        registerReceiver(broadcastReciver, new IntentFilter(
                "android.intent.action.test"));

    }

    @OnClick({R.id.btn_download, R.id.btn_done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                setListData(DOING_LIST);
                break;
            case R.id.btn_done:
                setListData(DONE_LIST);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != broadcastReciver) {
            this.unregisterReceiver(broadcastReciver);
        }
    }
}
