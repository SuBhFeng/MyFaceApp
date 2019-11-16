package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import myapp.ui.LoginButton;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private LoginButton loginBtn;
    private LoginButton detectBtn;
    private LoginButton compareBtn;
    private LoginButton searchBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setBtnString("人脸注册");
        detectBtn = findViewById(R.id.detectBtn);
        detectBtn.setBtnString("人脸检测");
        compareBtn = findViewById(R.id.compareBtn);
        compareBtn.setBtnString("人脸对比");
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setBtnString("人脸搜索");

        loginBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                loginBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                Intent intent = new Intent(context, RegisterByCamera.class);
                context.startActivity(intent);
                loginBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        detectBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                detectBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                Intent intent = new Intent(context, DetectByCamera.class);
                context.startActivity(intent);
                detectBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        compareBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                compareBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                Intent intent = new Intent(context, CompareByImage.class);
                context.startActivity(intent);
                compareBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        searchBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                searchBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                Intent intent = new Intent(context, SearchOneByCamera.class);
                context.startActivity(intent);
                searchBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loginBtn.reset();
        detectBtn.reset();
        compareBtn.reset();
        searchBtn.reset();
    }

    public void toRegister(View view) {
        Intent intent = new Intent(this, RegisterByCamera.class);
        this.startActivity(intent);
    }

    public void toDetect(View view) {
        Intent intent = new Intent(this, DetectByCamera.class);
        this.startActivity(intent);
    }

    public void toSearch(View view) {
        Intent intent = new Intent(this, SearchOneByCamera.class);
        this.startActivity(intent);
    }

    public void toCompare(View view) {
        Intent intent = new Intent(this, CompareByImage.class);
        this.startActivity(intent);
    }

}

