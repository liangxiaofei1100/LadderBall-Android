package com.zhaoyan.ladderball.util;

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
}
