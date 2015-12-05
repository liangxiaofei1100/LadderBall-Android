package com.zhaoyan.ladderball.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间，日期相关公共处理
 * Created by Yuri on 2015/12/5.
 */
public class TimeUtil {

    public static String getFormatterDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日(E)HH:mm");
        return format.format(new Date(time));
    }
}
