package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/12.
 */
public class EventPartListRequest extends BaseRequest {

    public long matchId;
    public long teamId;
    public int partNumber;

    public EventPartListRequest(Context context) {
        super(context);
    }
}
