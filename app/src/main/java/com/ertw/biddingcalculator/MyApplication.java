package com.ertw.biddingcalculator;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

import androidx.room.Room;

import com.ertw.biddingcalculator.constants.GlobalConstants;
import com.ertw.biddingcalculator.db.AppDatabase;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.toast.XToast;


import java.io.File;

/**
 * Author: wenweibo
 * Time: 2025/2/12 16:19
 * Description:This is MyApplication
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    public static Context applicationContext;

    public static AppDatabase appDatabase;

    public static File logDir;
    public static File historyDir;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        applicationContext = this;
        XUI.init(this);
//        XUI.initFontStyle("fonts/yu_gothic_regular.ttf");
        //        XUI.initFontStyle("fonts/meiryo.ttf");
        XToast.Config.get().setGravity(Gravity.CENTER);

        // writer crash log
//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
        // init database
        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase.class, GlobalConstants.DB_NAME).build();

    }
}
