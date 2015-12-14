package com.zhaoyan.ladderball.util;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/14.
 */
public class MatchUtil {

    public static final String EX_EXTRA_HAS_SET_TASK = "has_set_task_";
    public static final String EX_EXTRA_HAS_SET_PRACTICE = "has_set_practice_";

    /**
     * 当前比赛任务是否设置过
     * @param matchId 比赛任务id
     */
    public static boolean hasSetTask(Context context, long matchId) {
        return SharedPreferencesManager.get(context, EX_EXTRA_HAS_SET_TASK + matchId, false);
    }

    /**
     * 设置已经设置过该场比赛任务了，以任务设置界面提交成功为准
     * @param matchId 比赛任务id
     */
    public static void setHadSetTask(Context context, long matchId) {
        SharedPreferencesManager.put(context, EX_EXTRA_HAS_SET_TASK + matchId, true);
    }

    /**
     * 当前练习赛是否设置过
     * @param matchId 比赛任务id
     */
    public static boolean hasSetPractice(Context context, long matchId) {
        return SharedPreferencesManager.get(context, EX_EXTRA_HAS_SET_PRACTICE + matchId, false);
    }

    /**
     * 设置已经设置过该场练习赛了，以任务设置界面提交成功为准
     * @param matchId 比赛任务id
     */
    public static void setHadSetPractice(Context context, long matchId) {
        SharedPreferencesManager.put(context, EX_EXTRA_HAS_SET_PRACTICE + matchId, true);
    }
}
