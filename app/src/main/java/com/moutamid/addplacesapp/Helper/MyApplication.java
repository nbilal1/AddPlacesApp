package com.moutamid.addplacesapp.Helper;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.fxn.stash.Stash;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "channel";


    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);

    }

}
