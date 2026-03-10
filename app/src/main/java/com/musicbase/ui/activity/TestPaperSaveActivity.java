package com.musicbase.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.musicbase.R;
import com.musicbase.adapter.TestPaperSaveAdapter;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.TestPaperDetailBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.view.JzvdVideoView;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestPaperSaveActivity extends AppCompatActivity {
    ImageButton titlelayout_back;
    DetailBean.DataBean.ResourceListBean.TestPaperInfoBean testPaperInfo;
    int resourceId;
    TestPaperDetailBean.DataBean.QuestionsBean questionsBean;
    int courseId;
    private List<File> files = new ArrayList<>();
    private RecyclerView recyclerView;
    private TestPaperSaveAdapter adapter;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_paper_save);
        getIntentData();
        getFiles();
        initView();
    }

    private void initView() {
        titlelayout_back = findViewById(R.id.titlelayout_back);
        titlelayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TestPaperSaveAdapter(this, null);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        // 开启滑动删除
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(onItemSwipeListener);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(TestPaperSaveActivity.this, TestPaperUploadActivity.class);
                intent.putExtra("testPaperInfo", testPaperInfo);
                intent.putExtra("resourceId", resourceId);
                intent.putExtra("url", files.get(position).getAbsolutePath());
                intent.putExtra("questionsBean", questionsBean);
                intent.putExtra("courseId", courseId);
                intent.putExtra("index",index);
                startActivityForResult(intent, 112);
            }
        });

        adapter.bindToRecyclerView(recyclerView);
        adapter.setNewData(files);
        adapter.notifyDataSetChanged();
    }

    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            deleteFile = files.get(pos);
            showMyDialog(pos);
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {
        }
    };

    private void getIntentData() {
        testPaperInfo = (DetailBean.DataBean.ResourceListBean.TestPaperInfoBean) getIntent().getSerializableExtra("testPaperInfo");
        resourceId = getIntent().getIntExtra("resourceId", 0);
        questionsBean = (TestPaperDetailBean.DataBean.QuestionsBean) getIntent().getSerializableExtra("questionsBean");
        courseId = getIntent().getIntExtra("courseId", 0);
        index = getIntent().getIntExtra("index",0);
    }

    Dialog dialog;
    File deleteFile;
    private void showMyDialog(int pos) {
        dialog = new Dialog(this, R.style.my_dialog);
        dialog.setContentView(R.layout.dialog_login_jiesuo);
        TextView textView02 = (TextView) dialog.findViewById(R.id.dialog_titile);
        textView02.setText(Html.fromHtml("删除视频"));
        TextView textView03 = (TextView) dialog.findViewById(R.id.error_message);
        textView03.setText(Html.fromHtml("确定删除视频吗？"));
        Button button01 = (Button) dialog.findViewById(R.id.btn_right);
        button01.setText("确认");
        Button button02 = (Button) dialog.findViewById(R.id.btn_left);
        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.deleteBookFormSD(deleteFile.getAbsolutePath());
                ActivityUtils.deleteBookFormSD(deleteFile.getAbsolutePath().replace(".mp4",".jpg"));
                dialog.dismiss();
            }
        });
        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFiles();
                adapter.setNewData(files);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getFiles() {
        files.clear();
        File dir = new File(ActivityUtils.getSDPath(TestPaperSaveActivity.this, resourceId + "").getAbsolutePath() + File.separator + ".nomedia");
        if(dir!=null&&dir.listFiles()!=null&&dir.listFiles().length>0) {
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith(".mp4")) {
                    files.add(f);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 112 && resultCode == 112 && adapter != null) {
            getFiles();
            adapter.setNewData(files);
            adapter.notifyDataSetChanged();
        }
    }
}