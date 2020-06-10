package com.example.charletdemo;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.charletdemo.util.DeviceInfo;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initDevice();
    }


    private void initDevice() {
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        DeviceInfo.sScreenWidth = outMetrics.widthPixels;
        DeviceInfo.sScreenHeight = outMetrics.heightPixels;
        DeviceInfo.sAutoScaleX = DeviceInfo.sScreenWidth * 1.0f / DeviceInfo.UI_WIDTH;
        DeviceInfo.sAutoScaleY = DeviceInfo.sScreenHeight * 1.0f / DeviceInfo.UI_HEIGHT;
    }

}