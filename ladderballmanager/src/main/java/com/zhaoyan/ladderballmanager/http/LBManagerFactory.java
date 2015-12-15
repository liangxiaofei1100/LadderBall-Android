package com.zhaoyan.ladderballmanager.http;

/**
 *
 * Created by drakeet on 8/9/15.
 */
public class LBManagerFactory {

    protected static final Object monitor = new Object();
    private static LBManagerApi mSingleton = null;


    public static LBManagerApi getInstance() {
        synchronized (monitor) {
            if (mSingleton == null) {
                mSingleton = new LBManagerRetrofit().getService();
            }
            return mSingleton;
        }
    }
}
