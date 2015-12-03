package com.zhaoyan.ladderball.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用工具类
 * Created by Yuri on 2015/12/2.
 */
public class CommonUtil {

    /**验证手机号码是否合法的正则表达式*/
    private static final String PHONE_REG = "^1[3|4|5|7|8][0-9]\\d{8}$";
    /**
     * 验证手机号是否合法
     * @param number 需要验证的手机号码
     * @return true 手机号码合法；false手机号码不合法
     */
    public static boolean isValidPhoneNumber(String number) {
        Pattern p = Pattern.compile(PHONE_REG);
        Matcher m = p.matcher(number);
        return m.find();
    }

    /**
     * 获取应用版本号
     * @param context
     * @return 当前版本号
     * @throws PackageManager.NameNotFoundException
     */
    public static String getAppVersion(Context context){
        String version = "";
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionName;
        }catch(Exception e){
            Log.e("get app version error...");
        }
        return version;
    }

    /**SharedPrefenrence：记录登录用户的电话号码*/
    public static final String KEY_USER_PHONE = "ladderball_phone";
    /**判断当前是否登录状态*/
    public static boolean isLogin(Context context) {
        String phone = SharedPreferencesManager.get(context, KEY_USER_PHONE, "-1");
        return !phone.equals("-1");
    }

}
