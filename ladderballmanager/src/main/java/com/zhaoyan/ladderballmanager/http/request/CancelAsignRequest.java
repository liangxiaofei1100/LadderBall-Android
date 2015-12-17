package com.zhaoyan.ladderballmanager.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/17.
 */
public class CancelAsignRequest extends BaseRequest {

    public long matchId;
    public int team;

    public CancelAsignRequest(Context context) {
        super(context);
    }
}
