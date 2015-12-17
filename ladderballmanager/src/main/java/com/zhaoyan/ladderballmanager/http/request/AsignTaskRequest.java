package com.zhaoyan.ladderballmanager.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/17.
 */
public class AsignTaskRequest extends BaseRequest {

    public long matchId;
    public String recorderPhone;
    public int asignedTeam;

    public AsignTaskRequest(Context context) {
        super(context);
    }
}
