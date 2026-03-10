package com.musicbase.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.musicbase.R;

public class Cardsuccess extends AppCompatActivity implements View.OnClickListener {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private TextView delete_text;
    private Button check_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardsuccess);
        initView();
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);
        delete_text = (TextView) findViewById(R.id.delete_text);
        check_button = (Button) findViewById(R.id.check_button);

        titlelayout_back.setOnClickListener(this);
        delete_text.setOnClickListener(this);
        check_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlelayout_back:

                break;
            case R.id.delete_text:

                break;
            case R.id.check_button:
                Intent intent = new Intent(Cardsuccess.this, MainActivity.class);
                intent.putExtra("mainFlag", 1);
                intent.putExtra("courseId", 0);
                intent.putExtra("systemCodeId", 0);
                startActivity(intent);

                break;
        }
    }
}
