package com.zhaoyan.ladderball;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhaoyan.ladderball.util.Log;

import java.util.List;

/**
 * 自定义Application用于一些数据初始化
 * Created by Yuri on 2015/12/1.
 */
public class AppAplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //避免调用多次onCreate()时，执行多次初始化
        String processName = getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(getPackageName());
            if (defaultProcess) {
                //默认进程才进行初始化动作
                initApplication();
            }
        }
    }

    private void initApplication() {
        Log.d();
        //tencent bugly init
        CrashReport.initCrashReport(getApplicationContext(), BallConstants.BUGLY_APP_ID, BuildConfig.DEBUG);
        //ActiveAndriod init
        ActiveAndroid.initialize(this);
    }

    /**
     * 返回进程名称
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
