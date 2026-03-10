package com.musicbase.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.musicbase.R;
import com.musicbase.util.DownLoadFileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static com.musicbase.preferences.Preferences.FILE_DOWNLOAD_URL;

public class TestActivity extends AppCompatActivity {

    private TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        text2 = (TextView) findViewById(R.id.text2);
        String imageUrl = FILE_DOWNLOAD_URL + "area/area_1.0.0.json";//http://47.98.121.127/upload/advert/20180706/1530849977.jpg
        String localPath = DownLoadFileUtils.customLocalStoragePath("yinji");// /storage/emulated/0/image/
        DownLoadFileUtils.downloadFile(this, imageUrl, localPath, "area.json");


        try {
            FileInputStream fis = new FileInputStream(localPath + "area.json");

            InputStreamReader isr = new InputStreamReader(fis, "utf8");
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                text2.append(line);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


}
