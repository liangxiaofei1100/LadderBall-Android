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

    /**
     * 格式化时间
     * @param time audio/video time like 12323312
     * @return the format time string like 2'12'
     */
    public static String timeFormat(long time) {
        long hour = time / (60 * 60 * 1000);
        String min = time % (60 * 60 * 1000) / (60 * 1000) + "";
        String sec = time % (60 * 60 * 1000) % (60 * 1000) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        }

        if (sec.length() == 4) {
            sec = "0" + sec;
        } else if (sec.length() == 3) {
            sec = "00" + sec;
        } else if (sec.length() == 2) {
            sec = "000" + sec;
        } else if (sec.length() == 1) {
            sec = "0000" + sec;
        }

        if (hour == 0) {
            return min + "'  " + sec.trim().substring(0, 2) + "''";
        } else {
            String hours = "";
            if (hour < 10) {
                hours = "0" + hour;
            } else {
                hours = hours + "";
            }
            return hours + "' " + min + "' " + sec.trim().substring(0, 2) + "''";
        }
    }

    /**
     * 格式化时间
     * @param time audio/video time like 12323312
     * @return the format time string like 2'12'
     */
    public static String[] getMinuteSecond(long time) {
        String[] result = new String[2];
        long hour = time / (60 * 60 * 1000);
        String min = time % (60 * 60 * 1000) / (60 * 1000) + "";
        String sec = time % (60 * 60 * 1000) % (60 * 1000) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        }

        result[0] = min;

        if (sec.length() == 4) {
            sec = "0" + sec;
        } else if (sec.length() == 3) {
            sec = "00" + sec;
        } else if (sec.length() == 2) {
            sec = "000" + sec;
        } else if (sec.length() == 1) {
            sec = "0000" + sec;
        }

        result[1] = sec.trim().substring(0, 2);

        return result;
    }
}
