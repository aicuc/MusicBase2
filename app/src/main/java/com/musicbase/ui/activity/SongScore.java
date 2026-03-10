package com.musicbase.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.musicbase.R;
import com.musicbase.adapter.SongScoreAdapter;
import com.musicbase.entity.SongSore;

import java.util.ArrayList;
import java.util.List;

public class SongScore extends AppCompatActivity implements View.OnClickListener {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private ListView song_list_view;
    private SongScoreAdapter songScoreAdapter;
    private List<SongSore> songSores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_score);
        initView();
        initData();
    }

    private void initData() {
        songSores = new ArrayList<>();
        songSores.add(new SongSore("曲目013.铃儿响叮当","13",""));
        songSores.add(new SongSore("曲目014.惊愕交响曲","14",""));
        songSores.add(new SongSore("曲目015.欢乐颂","15",""));
        songSores.add(new SongSore("曲目023.我和你","23",""));
        songSores.add(new SongSore("曲目025.康城赛马","25",""));
        songSores.add(new SongSore("曲目026.快乐少年","26",""));
        songSores.add(new SongSore("曲目029.小星星","29",""));
        songSores.add(new SongSore("曲目030.扬基·杜德尔","30",""));
        songSores.add(new SongSore("曲目032.老麦克唐纳有个农场","32",""));
        songSores.add(new SongSore("曲目033.老麦克唐纳有个农场","33",""));
        songScoreAdapter = new SongScoreAdapter(songSores, this, R.layout.item_song);
        song_list_view.setAdapter(songScoreAdapter);
        song_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(SongScore.this, SongScored.class).putExtra("name", songSores.get(position).getName()).putExtra("title", songSores.get(position).getTitle()));
            }
        });
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);
        song_list_view = (ListView) findViewById(R.id.song_list_view);
        titlelayout_back.setOnClickListener(this);
        titlelayout_title.setText("你唱我评");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlelayout_back:
                finish();
                break;
        }
    }
}
