package com.zhaoyan.ladderball.http;

/**
 *
 * Created by drakeet on 8/9/15.
 */
public class LadderBallFactory {

    protected static final Object monitor = new Object();
    private static LadderBallApi mSingleton = null;


    public static LadderBallApi getInstance() {
        synchronized (monitor) {
            if (mSingleton == null) {
                mSingleton = new LadderBallRetrofit().getLadderBallService();
            }
            return mSingleton;
        }
    }
}
