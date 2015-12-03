package com.zhaoyan.ladderball.http.request;

import android.content.Context;

import com.zhaoyan.ladderball.util.CommonUtil;

/**
 *
 * Created by Yuri on 2015/12/2.
 */
public class BaseRequest {

    public RequestHeader header;

    public BaseRequest(Context context) {
        header = new RequestHeader();
        header.clientVersion = getClientVersion(context);
        header.requestTime = getRequestTime();
        header.serviceVersion = getServieVersion();
        header.userToken = getUserToken(context);
    }

    private String getUserToken(Context context) {
        return CommonUtil.getUserHttpHeaderToken(context);
    }

    private int getServieVersion() {
        return 1;
    }

    private long getRequestTime() {
        return System.currentTimeMillis();
    }

    private String getClientVersion(Context context) {
        return CommonUtil.getAppVersion(context);
    }
}
