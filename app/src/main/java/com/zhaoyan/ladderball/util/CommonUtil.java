package com.zhaoyan.ladderball.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.AppApplication;
import com.zhaoyan.ladderball.model.User;

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

    /**
     * @description 获取AppApplication对象
     * @param context
     * @return AppApplication对象
     */
    private static AppApplication getApp(Context context){
        return (AppApplication)context.getApplicationContext();
    }

    /**SharedPrefenrence：记录登录用户的电话号码*/
    public static final String KEY_USER_PHONE = "ladderball_phone";
    /**判断当前是否登录状态*/
    public static boolean isLogin(Context context) {
        String phone = SharedPreferencesManager.get(context, KEY_USER_PHONE, "-1");
        return !phone.equals("-1");
    }

    public static String getUserPhone(Context context) {
        String phone = SharedPreferencesManager.get(context, KEY_USER_PHONE, "-1");
        return phone;
    }

    /**
     * @description 该token是服务端返回的标识登录用户的id，客户端发送Http请求是需要在header头添加该token，标识是登录用户
     * @param context
     */
    public static String getUserHttpHeaderToken(Context context){
        String userToken = getApp(context).getUserToken();
        if (TextUtils.isEmpty(userToken)) {
            String phone = getUserPhone(context);
            User user = new Select().from(User.class).where("phone=?", phone).executeSingle();
            if (user == null){
                return null;
            }
            userToken = user.mUserToken;
        }

        return userToken;
    }

    /**
     * @description 设定用户token
     * @param context
     * @param userToken 用户token
     */
    public static void setUserHttpHeaderToken(Context context, String userToken){
        getApp(context).setUserToken(userToken);
    }

}
