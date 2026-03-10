package com.musicbase.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.BookAdapter;
import com.musicbase.entity.Bean;
import com.musicbase.entity.BookVOBean;
import com.musicbase.entity.TestPaperDetailBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.view.DragGridView;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.musicbase.preferences.Preferences.appInfo;
import static com.musicbase.util.DialogUtils.dismissMyDialog;


/**
 * Created by BAO on 2016-08-09.
 */
public class BookActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    BookAdapter adapter;// 书架的适配器
    private DragGridView gridView;
    private List<BookVOBean.DataBean> books = new ArrayList<BookVOBean.DataBean>();// 书籍列表数据
    private Handler mHandler;
    private final String TAG = getClass().getSimpleName();
    private SwipeRefreshLayout refresh_view;
    Button btn_record;
    ImageButton btn_scan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        initView();
        initBook();
    }

    /**
     * 初始化界面
     */
    public void initView() {
        ((ImageButton) findViewById(R.id.titlelayout_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.layout_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookActivity.this,SearchActivity.class));
            }
        });

        refresh_view = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        gridView = findViewById(R.id.gridview);
        adapter = new BookAdapter(books,this,R.layout.a_book_list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (btn_record != null) {
                    btn_record.setVisibility(View.INVISIBLE);
                    btn_record = null;
                }
                BookVOBean.DataBean book = books.get(position);
                if (book != null) {
                    if(book.getAllowOpen()==0) {
                        ActivityUtils.showToast(BookActivity.this,book.getBanReason());
                        return;
                    }
                    if (book.getDeadlineType() == 0) {
                        gotoBookDetail(book);
                    } else {
                        if (book.getIsOverdue() == 0)
                            gotoBookDetail(book);
                        else
                            ActivityUtils.showToast(BookActivity.this, "该图书已到期");
                    }
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (btn_record != null) {
                    btn_record.setVisibility(View.INVISIBLE);
                    btn_record = null;
                }
                final Button btn_delete = (Button) view.findViewById(R.id.btn_delete1);
                btn_delete.setVisibility(View.VISIBLE);
                btn_record = btn_delete;
                btn_delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_delete.setVisibility(View.GONE);
                        DialogUtils.showMyDialog(BookActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "删除书籍", "你确定永久删除此书籍,删除后将不可恢复？", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUtils.dismissMyDialog();
                                deleteBook(position);
                            }
                        });
                    }
                });
                return false;
            }
        });

        gridView.setOnChangeListener(new DragGridView.OnChangeListener() {
            @Override
            public void onChange(int from, int to) {
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(books, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(books, i, i - 1);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onStartChange() {
                if (btn_record != null) {
                    btn_record.setVisibility(View.INVISIBLE);
                    btn_record = null;
                }
                refresh_view.setEnabled(false);
            }

            @Override
            public void onEndChange() {
                refresh_view.setEnabled(true);
                //                gridView.setFocusable(false);
                //                gridView.setFocusableInTouchMode(false);
                saveSort();
            }
        });
        btn_scan = findViewById(R.id.btn_scan);

        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        findBooks();
                        break;
                    case 2:
                        adapter.notifyDataSetChanged();
                        refresh_view.setRefreshing(false);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        btn_scan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 当前系统大于等于6.0
                    permission();
                } else {
                    // 当前系统小于6.0，直接调用拍照
                    doBackup();
                }
            }
        });
        findViewById(R.id.tv_delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BookActivity.this,BookDeleteActivity.class),11);
            }
        });
    }

    private void gotoBookDetail(BookVOBean.DataBean bookVO) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("courseId", bookVO.getCourseId());
        startActivity(intent);
    }

    private void saveSort() {
        StringBuilder bookSort = new StringBuilder();
        for (BookVOBean.DataBean book : books) {
            bookSort.append(book.getCourseId());
            bookSort.append(",");
        }
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.SORTCOURSESHELF).tag(this).params("userId", SPUtility.getUserId(this) + "").params("shelfSorts", bookSort.toString()).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Bean bean = gson.fromJson(response.body(), Bean.class);
                dismissMyDialog();
                if (!bean.getCode().equals("SUCCESS")) {
                    Toast.makeText(BookActivity.this, bean.getCodeInfo(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(BookActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    private static final int MY_PERMISSION_REQUEST_CODE = 10;
    private String[] myPermissions = new String[]{Manifest.permission.CAMERA};

    private void permission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(myPermissions);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        Log.d(TAG, "isAllGranted==" + isAllGranted);
        if (isAllGranted) {
            doBackup();
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(this,myPermissions, MY_PERMISSION_REQUEST_CODE);
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            Log.d(TAG, "isAllGranted222==" + isAllGranted);
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 第 4 步: 后续操作
     */
    private void doBackup() {
        // 本文主旨是讲解如果动态申请权限, 具体备份代码不再展示, 就假装备份一下
        Intent intent = new Intent(this, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, Activity.RESULT_FIRST_USER);

        //		Intent intent = new Intent(getActivity(), WorkWebActivity.class);
        //        startActivity(intent);
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("加阅需要访问 “相机”，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            mHandler.sendEmptyMessageDelayed(1, 500);
        }else if(resultCode == 11)
            mHandler.sendEmptyMessageDelayed(1, 500);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化图书列表数据
     */
    private void initBook() {
        findBooks();
    }


    @Override
    public void onRefresh() {
        findBooks();
        if (btn_record != null) {
            btn_record.setVisibility(View.INVISIBLE);
            btn_record = null;
        }
    }

    /**
     * 查询书架列表
     */
    public void findBooks() {
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.GET_BOOKSHELF).tag(this).params("userId", SPUtility.getUserId(this) + "").params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                BookVOBean bean = gson.fromJson(response.body(), BookVOBean.class);
                dismissMyDialog();
                if (bean != null && bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS) && null != bean.getData()) {
                    Log.d(TAG, "BOOKLIST_URL----" + response.body());
                    books.clear();
                    books.addAll(bean.getData());
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);

                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(BookActivity.this, "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(BookActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    /**
     * 删除图书
     *
     * @param location
     */
    public void deleteBook(final int location) {
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.DELETE_BOOK_URL).tag(this).params("userId", SPUtility.getUserId(this) + "").params("courseId", books.get(location).getCourseId()).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Bean bean = gson.fromJson(response.body(), Bean.class);
                dismissMyDialog();
                if (bean != null && bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS)) {
                    books.remove(location);
                    adapter.notifyDataSetChanged();
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    DialogUtils.showMyDialog(BookActivity.this, Preferences.SHOW_ERROR_DIALOG, "删除失败", bean.getCodeInfo(), null);
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(BookActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

}
