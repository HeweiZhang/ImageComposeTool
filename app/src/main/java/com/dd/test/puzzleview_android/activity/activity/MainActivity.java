package com.dd.test.puzzleview_android.activity.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dd.test.puzzleview_android.R;
import com.dd.test.puzzleview_android.activity.view.TopView;

/**
 * 原作者信息：
 * 项目博客地址：http://blog.5ibc.net/p/42648.html
 * github地址：https://github.com/feinimoshu753/puzzleview-android
 */
public class MainActivity extends AppCompatActivity {

    private TopView topView;
    private Button picSelectBtn;
    private ImageView picShowImageView;
    private MyBroadCastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topView = (TopView) findViewById(R.id.top_view);
        picSelectBtn = (Button) findViewById(R.id.pic_select);
        picShowImageView = (ImageView) findViewById(R.id.pic_show);

        topView.setTitle("puzzle-android");
        topView.hide(TopView.LEFT);

        picSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    };
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 100);
                } else {
                    Intent intentToPuzzle = new Intent(MainActivity.this, PuzzlePickerActivity.class);
                    startActivity(intentToPuzzle);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intentToPuzzle = new Intent(MainActivity.this, PuzzlePickerActivity.class);
            startActivity(intentToPuzzle);
        }
    }

    @Override
    protected void onStart() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new MyBroadCastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter("puzzle");
        registerReceiver(broadcastReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String picPath = intent.getStringExtra("picPath");
            if (picPath != null) {
                Glide.with(context)
                        .load(String.format("file://%s", picPath))
                        .crossFade()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(picShowImageView);

            }
        }
    }
}
