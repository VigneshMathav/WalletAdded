package com.vk.uplogictask.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


import com.vk.uplogictask.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        displayData();


    }

    private void displayData() {

        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(() -> {

            if (sharedpreferences.contains("isUserLogin")) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}