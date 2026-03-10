package com.musicbase.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.PhotoListAdapter;
import com.musicbase.entity.Bean;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.GetPhotoBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.musicbase.util.UriToFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.musicbase.util.DialogUtils.dismissMyDialog;

public class PhotoListActivity extends AppCompatActivity {
    ImageButton titlelayout_back;
    DetailBean.DataBean.UserAvatarsBean userAvatars;
    RecyclerView recyclerView;
    Button btn_submit;
    PhotoListAdapter photoListAdapter;
    // 相册选择回传吗
    public final static int GALLERY_REQUEST_CODE = 1;
    // 照片所在的Uri地址
    private Uri imageUri;
    int resourceId;
    int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        userAvatars = (DetailBean.DataBean.UserAvatarsBean) getIntent().getSerializableExtra("userAvatars");
        resourceId = getIntent().getIntExtra("resourceId", 0);
        courseId = getIntent().getIntExtra("courseId", 0);
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        photoListAdapter = new PhotoListAdapter(this, userAvatars!=null?userAvatars.getAvatarList():null);
        photoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectPhoto(position);
            }
        });
        recyclerView.setAdapter(photoListAdapter);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userAvatars!=null&&userAvatars.getAvatarState().equals("no_upload")){
                    ActivityUtils.showToast(PhotoListActivity.this, "请购买后再上传头像");
                    return;
                }
                if (checkPermission())
                    return;
                choosePhoto();
            }
        });
    }

    private void selectPhoto(int position) {
        for (int i = 0; i < userAvatars.getAvatarList().size(); i++) {
            if (i == position)
                userAvatars.getAvatarList().get(i).setIsDefault(1);
            else
                userAvatars.getAvatarList().get(i).setIsDefault(0);
        }
        finishPhotoList();
    }

    private void finishPhotoList() {
        Intent intent = new Intent();
        intent.putExtra("userAvatars", userAvatars);
        setResult(1111, intent);
        finish();
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, GALLERY_REQUEST_CODE);
    }

    //当拍摄照片完成时会回调到onActivityResult 在这里处理照片的裁剪
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE: {
                    // 获取图片
                    try {
                        //该uri是上一个Activity返回的
                        imageUri = data.getData();
                        if (imageUri != null) {
                            File file = new File(UriToFile.getFileAbsolutePath(this,imageUri));
                            uploadPhoto(file);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File uri2File(Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor.getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }

    private void uploadPhoto(File file) {
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.UPLOAD_PHOTO).tag(this).params("userId", SPUtility.getUserId(this) + "").params("resourceId", resourceId+"").params("avatar", file).isMultipart(true).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Bean bean = gson.fromJson(response.body(), Bean.class);
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS)) {
                    finishPhotoList();
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(PhotoListActivity.this, "上传失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(PhotoListActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    private void getPhoto() {
        OkGo.<String>post(Preferences.GET_PHOTO).tag(this).params("userId", SPUtility.getUserId(this) + "").params("courseId", courseId+"").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                GetPhotoBean bean = gson.fromJson(response.body(), GetPhotoBean.class);
                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS)) {
                    userAvatars = bean.getData();

                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(PhotoListActivity.this, "获取失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(PhotoListActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    private boolean checkPermission() {
        boolean isCheck = false;
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                isCheck = true;
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
        return isCheck;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                //                doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("下载需要访问 “外部存储器”，请到 “设置 -> 应用权限” 中授予！");
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

}