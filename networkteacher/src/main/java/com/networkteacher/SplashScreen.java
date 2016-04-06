package com.networkteacher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import com.digits.sdk.android.Digits;
import com.networkteacher.utils.ReusableClass;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "q5deSIwB19UsFrib7gopnpVhX";
    private static final String TWITTER_SECRET = "8IytfyeEraXZmvKZBa1hBZF7Yrawbftrg9jTUYNNVxOI0jjiOS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());
        setContentView(R.layout.activity_splash_screen);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!ReusableClass.getFromPreference("session", SplashScreen.this).equalsIgnoreCase("")) {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                    startActivity(i);
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                    finish();
                }
            }
        }, 1000);
    }
}
