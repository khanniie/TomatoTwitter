package com.codepath.apps.restclienttemplate.models;

import android.app.Application;
import android.content.Context;

import com.codepath.apps.restclienttemplate.R;

public class App extends Application {

    private static Context mContext;
    private static String key;

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = this;
        key = getString(R.string.consumer_key);
    }

    public App(){
        mContext = this;
        key = getString(R.string.consumer_key);
    }

    public static Context getContext(){
        return mContext;
    }
    public static String getKey(){
        return key;
    }
}