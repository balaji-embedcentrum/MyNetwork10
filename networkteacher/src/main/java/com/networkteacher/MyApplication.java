package com.networkteacher;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Dream on 17-Jan-16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this);
    }
}
