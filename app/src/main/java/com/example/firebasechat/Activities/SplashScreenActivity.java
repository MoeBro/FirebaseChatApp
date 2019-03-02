package com.example.firebasechat.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.firebasechat.R;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(LogInActivity.class)
                .withSplashTimeOut(4000)
                .withBackgroundResource(android.R.color.holo_red_light)
                .withHeaderText("Header")
                .withFooterText("Copyright 2016")
                .withBeforeLogoText("My cool company")
                .withLogo(R.drawable.splash)
                .withAfterLogoText("Some more details with custom font");


        //change text color
        config.getHeaderTextView().setTextColor(Color.WHITE);

        //finally create the view
        View easySplashScreenView = config.create();
        setContentView(easySplashScreenView);

    }
}
