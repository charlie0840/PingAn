package com.pingan_us.pingan;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by yshui on 2/20/18.
 */

public class ParseInitialization extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getResources().getString(R.string.back4app_app_id), getResources().getString(R.string.back4app_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
