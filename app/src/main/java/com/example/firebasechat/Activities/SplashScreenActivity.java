package com.example.firebasechat.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
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
        //configuring splashscreen
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(LogInActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundResource(android.R.color.white)
                .withBeforeLogoText("Firebase Chat")
                .withLogo(R.drawable.icon)
                .withAfterLogoText("House Of Code Application");

        Typeface segoeui = Typeface.createFromAsset(getAssets(),"fonts/segoeui.otf");
        config.getAfterLogoTextView().setTypeface(segoeui);
        config.getBeforeLogoTextView().setTypeface(segoeui);
        config.getBeforeLogoTextView().setShadowLayer(2,1,1,Color.parseColor("#000000"));
        config.getAfterLogoTextView().setShadowLayer(2,1,1,Color.parseColor("#000000"));
        //create the view for splashscreen
        View easySplashScreenView = config.create();
        setContentView(easySplashScreenView);

    }
}
