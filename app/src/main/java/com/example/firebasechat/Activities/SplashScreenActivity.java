package com.example.firebasechat.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.firebasechat.R;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {
    // Using EasySplashScreen library
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(LogInActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundResource(android.R.color.white)
                .withBeforeLogoText("Firebase Chat")
                .withLogo(R.drawable.icon)
                .withAfterLogoText("House Of Code Application");

        //finally create the view
        View easySplashScreenView = config.create();
        setContentView(easySplashScreenView);

    }
}
