package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * 领取练习赛请求
 * Created by Yuri on 2015/12/14.
 */
public class ReceivePracticeRequest extends BaseRequest {

    public long matchId;

    public ReceivePracticeRequest(Context context) {
        super(context);
    }
}
