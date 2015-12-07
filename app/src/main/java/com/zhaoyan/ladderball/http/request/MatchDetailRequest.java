package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/7.
 */
public class MatchDetailRequest extends BaseRequest{
    public int matchId;

    public MatchDetailRequest(Context context) {
        super(context);
    }
}
