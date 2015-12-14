package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/14.
 */
public class CommitTaskRequest extends BaseRequest {

    public long matchId;

    public CommitTaskRequest(Context context) {
        super(context);
    }
}
